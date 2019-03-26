package com.imcfr.imc.java.api.sirene.test.client;

import static com.imcfr.imc.java.api.sirene.SireneClientImpl.ERROR_MESSAGE_INVALID_SIRET;
import static com.imcfr.imc.java.api.sirene.SireneClientImpl.ERROR_MESSAGE_SIRET_NULL_OR_EMPTY;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.imcfr.imc.java.api.sirene.SireneClient;
import com.imcfr.imc.java.api.sirene.SireneClientException;
import com.imcfr.imc.java.api.sirene.SireneClientImpl;

public class SireneClientTest {

	private static final String sireneUrl = "https://api.insee.fr/entreprises/sirene/V3";
	private static final Integer sireneTimeout = 5000;
	private static final String sirenTokenRefreshUrl = "https://api.insee.fr";
        private static final Integer sireneTokenValidity = 120;
	private static String sireneConsumerKey = null;
	private static String sireneConsumerSecret = null;
	
	private static final String IMC_SIRET = "41831046200050";
	private static final String IMC_SIREN = "418310462";
	
	private SireneClient sireneClient;
	
	@Before
	public void setUp() throws MalformedURLException, SireneClientException {
		sireneConsumerKey = System.getProperty("sirene-client.consumer-key");
		if (sireneConsumerKey == null) {
			sireneConsumerKey = System.getenv("sirene-client.consumer-key");
		}
		assertNotNull(sireneConsumerKey);
		sireneConsumerSecret = System.getProperty("sirene-client.consumer-secret");
		if (sireneConsumerSecret == null) {
			sireneConsumerSecret = System.getenv("sirene-client.consumer-secret");
		}
		assertNotNull(sireneConsumerSecret);
		sireneClient = new SireneClientImpl(sireneUrl, sireneTimeout, sirenTokenRefreshUrl, sireneTokenValidity, sireneConsumerKey, sireneConsumerSecret);
	}
	
	@Test
	public void shouldFindSiret() throws SireneClientException {
		JsonNode rootNode = sireneClient.getBySiret(IMC_SIRET);
		assertTrue(rootNode != null && rootNode.has("header"));
		JsonNode header = rootNode.get("header");
		assertTrue(header.has("statut") && header.get("statut").asInt() == 200);
		assertTrue(rootNode.has("etablissement"));
		JsonNode etablissement = rootNode.get("etablissement");
		assertTrue(etablissement.has("siren") && etablissement.get("siren").asText().equals(IMC_SIREN));
	}
	
	@Test
	public void shouldFailBecauseOfMalformedSiret() {
		JsonNode rootNode = null;
		try {
			rootNode = sireneClient.getBySiret(null);
		} catch (SireneClientException e) {
			assertTrue(e.getMessage().equals(ERROR_MESSAGE_SIRET_NULL_OR_EMPTY));
		}
		assertTrue(rootNode == null);
		
		try {
			rootNode = sireneClient.getBySiret("");
		} catch (SireneClientException e) {
			assertTrue(e.getMessage().equals(ERROR_MESSAGE_SIRET_NULL_OR_EMPTY));
		}
		assertTrue(rootNode == null);
		
		String invalidSiret = "30305423300126";
		try {
			
			rootNode = sireneClient.getBySiret(invalidSiret);
		} catch (SireneClientException e) {
			assertTrue(e.getMessage().equals(String.format(ERROR_MESSAGE_INVALID_SIRET, invalidSiret)));
		}
		assertTrue(rootNode == null);
	}
	
	@Test
	public void shouldReturnNullWithUnknownSiret() throws SireneClientException {
		String siretNonAttribue = "20111973663512";
		assertNull(sireneClient.getBySiret(siretNonAttribue));
	}
	
}