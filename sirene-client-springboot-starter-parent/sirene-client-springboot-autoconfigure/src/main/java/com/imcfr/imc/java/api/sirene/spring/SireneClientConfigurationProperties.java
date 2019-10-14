package com.imcfr.imc.java.api.sirene.spring;

/*-
 * #%L
 * Client-Sirene
 * %%
 * Copyright (C) 2019 Items Média Concept
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = SireneClientConfigurationProperties.SIRENE_CLIENT_PREFIX)
public class SireneClientConfigurationProperties {

	public static final String SIRENE_CLIENT_PREFIX = "sirene-client";

	private Boolean enable = true;
	private boolean initializeTokenOnStartup = true;
	private String url;
	private Integer timeout;
	private String tokenRefreshUrl;
	private Integer tokenValidity;
	private String consumerKey;
	private String consumerSecret;

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public boolean getInitializeTokenOnStartup() {
		return initializeTokenOnStartup;
	}

	public void setInitializeTokenOnStartup(boolean initializeTokenOnStartup) {
		this.initializeTokenOnStartup = initializeTokenOnStartup;
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

	public Integer getTokenValidity() {
		return tokenValidity;
	}

	public void setTokenValidity(Integer tokenValidity) {
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
