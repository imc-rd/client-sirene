package com.imcfr.imc.java.api.sirene.spring;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.imcfr.imc.java.api.sirene.SireneClient;
import com.imcfr.imc.java.api.sirene.spring.SireneClientAutoconfiguration;

public class SireneClientAutoconfigurationTest {

	private AnnotationConfigApplicationContext context;

	@Before
	public void init() {
		this.context = new AnnotationConfigApplicationContext();
	}

	@After
	public void closeContext() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void testWithSireneClientProperties() {
		EnvironmentTestUtils.addEnvironment(this.context, 
				"sirene-client.enable:true",
				"sirene-client.url:https://api.insee.fr/entreprises/sirene/V3", 
				"sirene-client.timeout:5000",
				"sirene-client.token-refresh-url:https://api.insee.fr", 
				"sirene-client.token-validity:86400");
		this.context.register(PropertyPlaceholderAutoConfiguration.class, SireneClientAutoconfiguration.class);
		this.context.refresh();
		assertEquals(1, this.context.getBeanNamesForType(SireneClient.class).length);
	}

}
