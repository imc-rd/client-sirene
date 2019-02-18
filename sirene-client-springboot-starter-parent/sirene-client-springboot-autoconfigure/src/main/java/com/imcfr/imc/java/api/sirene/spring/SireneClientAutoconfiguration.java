package com.imcfr.imc.java.api.sirene.spring;

import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.imcfr.imc.java.api.sirene.SireneClient;
import com.imcfr.imc.java.api.sirene.SireneClientException;
import com.imcfr.imc.java.api.sirene.SireneClientImpl;

@Configuration
@ConditionalOnProperty(name = "sirene-client.enable", matchIfMissing = true)
@ConditionalOnClass({ SireneClient.class, SireneClientImpl.class })
@EnableConfigurationProperties(SireneClientConfigurationProperties.class)
public class SireneClientAutoconfiguration {

	@Autowired
	SireneClientConfigurationProperties configurationProperties;

	@Bean
	@ConditionalOnMissingBean
	public SireneClient sireneClient() throws MalformedURLException, SireneClientException {
		return new SireneClientImpl(configurationProperties.getUrl(), configurationProperties.getTimeout(),
				configurationProperties.getTokenRefreshUrl(), configurationProperties.getTokenValidity(),
				configurationProperties.getConsumerKey(), configurationProperties.getConsumerSecret());
	}

}
