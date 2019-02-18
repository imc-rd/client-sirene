package com.imcfr.imc.java.api.sirene.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = SireneClientConfigurationProperties.SIRENE_CLIENT_PREFIX)
public class SireneClientConfigurationProperties {

	public static final String SIRENE_CLIENT_PREFIX = "sirene-client";

	private Boolean enable = true;
	private String url;
	private Integer timeout;
	private String tokenRefreshUrl;
	private String tokenValidity;
	private String consumerKey;
	private String consumerSecret;

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public String getTokenRefreshUrl() {
		return tokenRefreshUrl;
	}

	public void setTokenRefreshUrl(String tokenRefreshUrl) {
		this.tokenRefreshUrl = tokenRefreshUrl;
	}

	public String getTokenValidity() {
		return tokenValidity;
	}

	public void setTokenValidity(String tokenValidity) {
		this.tokenValidity = tokenValidity;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public static String getSireneClientPrefix() {
		return SIRENE_CLIENT_PREFIX;
	}

}
