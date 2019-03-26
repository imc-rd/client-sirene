package com.imcfr.imc.java.api.sirene;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Main implementation of the SIRENE API client.
 */
public class SireneClientImpl implements SireneClient {

	private static final String MESSAGE_SIRET_UNKNOWN = "Siret %s non reconnu par l'API SIRENE";
	private static final Logger LOGGER = LoggerFactory.getLogger(SireneClientImpl.class);
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String HEADER_AUTHORIZATION = "Authorization";

	public static final String ERROR_MESSAGE_INVALID_SIRET = "Le siret %s est invalide";
	public static final String ERROR_MESSAGE_SIRET_NULL_OR_EMPTY = "Le siret est null ou vide";
	public static final String ERROR_MESSAGE_CONFIGURATION = "Une erreur est survenue dans la configuration du client SIRENE";
	public static final String ERROR_MESSAGE_API_DOWN = "API SIRENE indisponible";
	public static final String ERROR_MESSAGE_UNEXPECTED_RESPONSE = "La réponse de l'API SIRENE n'est pas celle attendue";
	public static final String ERROR_MESSAGE_FAIL_RESPONSE_READ = "Une erreur est survenue lors de la lecture de la réponse du client de l'API SIRENE";

	private final ObjectMapper jsonMapper = new ObjectMapper();
	private final String sireneUrl;
	private final Integer sireneTimeout;
	private final URL sireneRefreshTokenUrl;
	private final URL sireneRevokeTokenUrl;
	private final Map<String, String> sireneTokenHeaders;
	private final Map<String, String> sireneTokenParams;

	private String bearerToken;

	/**
	 * Build the sirene client api implementation.
	 * 
	 * @param sireneUrl            the SIRENE API URL
	 * @param sireneTimeout        the SIRENE API desired request timeout
	 * @param sirenTokenRefreshUrl the SIRENE API refresh token url
	 * @param sireneTokenValidity  the SIRENE API desired maximum token validity
	 * @param sireneConsumerKey    the SIRENE API consumer key
	 * @param sireneConsumerSecret the SIRENE API consumer secret key
	 * @throws MalformedURLException if an exception occurred when preparing SIRENE
	 *                               URLs
	 * @throws SireneClientException if an exception occurred during the token
	 *                               initialization
	 */
	public SireneClientImpl(String sireneUrl, Integer sireneTimeout, String sirenTokenRefreshUrl,
			Integer sireneTokenValidity, String sireneConsumerKey, String sireneConsumerSecret)
			throws MalformedURLException, SireneClientException {
		Objects.requireNonNull(sireneUrl, "Sirene URL must not be null");
		Objects.requireNonNull(sireneUrl, "Sirene timeout must not be null");
		Objects.requireNonNull(sireneUrl, "Sirene token refresh URL must not be null");
		Objects.requireNonNull(sireneUrl, "Sirene consumer key must not be null");
		Objects.requireNonNull(sireneUrl, "Sirene consumer secret key must not be null");
		this.sireneUrl = sireneUrl;
		this.sireneTimeout = sireneTimeout;
		this.sireneRefreshTokenUrl = new URL(sirenTokenRefreshUrl + "/token");
		this.sireneRevokeTokenUrl = new URL(sirenTokenRefreshUrl + "/revoke");

		Map<String, String> sireneTokenHeadersTemp = new HashMap<>();
		sireneTokenHeadersTemp.put(HEADER_AUTHORIZATION, String.format("Basic %s",
				Base64.getEncoder().encodeToString((sireneConsumerKey + ":" + sireneConsumerSecret).getBytes())));
		sireneTokenHeaders = Collections.unmodifiableMap(sireneTokenHeadersTemp);

		Map<String, String> sireneTokenParamsTemp = new HashMap<>();
		sireneTokenParamsTemp.put("grant_type", "client_credentials");

		sireneTokenParamsTemp.put("validity_period", sireneTokenValidity.toString());
		sireneTokenParams = Collections.unmodifiableMap(sireneTokenParamsTemp);

		revokeToken();
		getOrRefreshToken();
	}

	@Override
	public JsonNode getBySiret(String siret) throws SireneClientException {
		if (siret == null || siret.isEmpty()) {
			throw new SireneClientException(ERROR_MESSAGE_SIRET_NULL_OR_EMPTY);
		}
		if (!SireneClientValidatorUtils.validateSiret(siret)) {
			throw new SireneClientException(String.format(ERROR_MESSAGE_INVALID_SIRET, siret));
		}

		Map<String, String> headers = new HashMap<>();
		headers.put(HEADER_AUTHORIZATION, bearerToken);

		Map<String, String> parameters = new HashMap<>();
		parameters.put("siret", siret);

		URL url;
		try {
			url = new URL(sireneUrl + "/siret/" + siret);
		} catch (MalformedURLException e) {
			LOGGER.error(ERROR_MESSAGE_CONFIGURATION, e);
			throw new SireneClientException(ERROR_MESSAGE_CONFIGURATION, e);
		}

		String response = "";
		try {
			response = executeGet(url, true, sireneTimeout, sireneTimeout, headers, null);
		} catch (Exception e) {
			if (e.getCause() instanceof FileNotFoundException) {
				if (LOGGER.isInfoEnabled())
					LOGGER.error(String.format(MESSAGE_SIRET_UNKNOWN, siret));
				return null;
			} else if (e.getCause() instanceof IOException) {
				getOrRefreshToken();
				headers.replace(HEADER_AUTHORIZATION, bearerToken);
				try {
					response = executeGet(url, true, sireneTimeout, sireneTimeout, headers, null);
				} catch (SireneClientException e1) {
					if (e1.getCause() instanceof FileNotFoundException) {
						if (LOGGER.isInfoEnabled())
							LOGGER.error(String.format(MESSAGE_SIRET_UNKNOWN, siret));
						return null;
					} else {
						LOGGER.error(ERROR_MESSAGE_API_DOWN);
						throw new SireneClientException(ERROR_MESSAGE_API_DOWN, e1);
					}
				}
			} else {
				LOGGER.error(ERROR_MESSAGE_API_DOWN);
				throw new SireneClientException(ERROR_MESSAGE_API_DOWN, e);
			}
		}
		return extractResponse(response);
	}

	private void revokeToken() throws SireneClientException {
		getOrRefreshToken();
		Map<String, String> parameters = new HashMap<>();
		parameters.put("token",
				bearerToken.startsWith(TOKEN_PREFIX)
						? bearerToken.substring(TOKEN_PREFIX.length(), bearerToken.length())
						: bearerToken);
		try {
			executeGet(sireneRevokeTokenUrl, false, sireneTimeout, sireneTimeout, sireneTokenHeaders, parameters);
		} catch (SireneClientException e) {
			throw new SireneClientException(
					"Une erreur est survenue lors du la demande de révocation du token de l'API SIRENE", e);
		}
		if (LOGGER.isInfoEnabled())
			LOGGER.info(String.format("Jeton d'accès à l'API SIRENE révoqué : %s", bearerToken));
		bearerToken = "";
	}

	private void getOrRefreshToken() throws SireneClientException {
		String response;
		try {
			response = executeGet(sireneRefreshTokenUrl, false, sireneTimeout, sireneTimeout, sireneTokenHeaders,
					sireneTokenParams);
		} catch (SireneClientException e) {
			throw new SireneClientException(
					"Une erreur est survenue lors du la demande de rafraichissment du token de l'API SIRENE", e);
		}
		JsonNode rootNode;
		try {
			rootNode = jsonMapper.readTree(response);
		} catch (IOException e) {
			throw new SireneClientException(ERROR_MESSAGE_FAIL_RESPONSE_READ, e);
		}
		if (!rootNode.has("access_token")) {
			throwUnexpectedResponseException();
		}
		bearerToken = TOKEN_PREFIX + rootNode.get("access_token").asText();
		if (LOGGER.isInfoEnabled())
			LOGGER.info(String.format("Jeton d'accès à l'API SIRENE récupéré : %s - expire dans %s secondes",
					bearerToken, rootNode.get("expires_in").asText()));
	}

	private JsonNode extractResponse(String response) throws SireneClientException {
		JsonNode rootNode;
		try {
			rootNode = jsonMapper.readTree(response);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new SireneClientException(ERROR_MESSAGE_FAIL_RESPONSE_READ, e);
		}
		if (!rootNode.has("header")) {
			throwUnexpectedResponseException();
		}
		JsonNode header = rootNode.get("header");
		if (!header.has("statut") || header.get("statut").asInt() != 200) {
			throwUnexpectedResponseException();
		}
		return rootNode;
	}

	private void throwUnexpectedResponseException() throws SireneClientException {
		LOGGER.error(ERROR_MESSAGE_UNEXPECTED_RESPONSE);
		throw new SireneClientException(ERROR_MESSAGE_UNEXPECTED_RESPONSE);
	}

	private String executeGet(URL url, boolean useCache, int connectTimeout, int readTimeout,
			Map<String, String> headers, Map<String, String> parameters) throws SireneClientException {
		HttpURLConnection connection = prepareConnection(url, useCache, connectTimeout, readTimeout, headers,
				parameters);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

			String inputLine;
			StringBuilder content = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			return content.toString();

		} catch (Exception e) {
			throw new SireneClientException(
					String.format("Une erreur est survenue lors de la tentative de connexion à l'API SIRENE : %s", url),
					e);
		} finally {
			connection.disconnect();
		}
	}

	private HttpURLConnection prepareConnection(URL url, boolean useCache, int connectTimeout, int readTimeout,
			Map<String, String> headers, Map<String, String> parameters) throws SireneClientException {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setUseCaches(useCache);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			if (headers != null && !headers.isEmpty()) {
				extractHeaders(headers, connection);
			}
			if (parameters != null && !parameters.isEmpty()) {
				extractParameters(parameters, connection);
			}
		} catch (IOException e) {
			if (connection != null) {
				connection.disconnect();
			}
			throw new SireneClientException(String.format(
					"Une erreur est survenue lors de l'initialisation de la connexion à l'API SIRENE : %s", url), e);
		}
		return connection;
	}

	private void extractHeaders(Map<String, String> headers, HttpURLConnection connection) {
		for (Entry<String, String> header : headers.entrySet()) {
			connection.setRequestProperty(header.getKey(), header.getValue());
		}
	}

	private void extractParameters(Map<String, String> parameters, HttpURLConnection connection) throws IOException {
		connection.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		out.writeBytes(getParamsString(parameters));
		out.flush();
		out.close();
	}

	private String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			result.append("&");
		}

		String resultString = result.toString();
		return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
	}

	public String getBearerToken() {
		return bearerToken;
	}
	
}
