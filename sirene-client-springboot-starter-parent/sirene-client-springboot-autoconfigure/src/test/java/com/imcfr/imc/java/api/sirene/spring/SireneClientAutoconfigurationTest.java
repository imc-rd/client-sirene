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
				"sirene-client.initialize-on-startup:true",
				"sirene-client.url:https://api.insee.fr/entreprises/sirene/V3", 
				"sirene-client.timeout:5000",
				"sirene-client.token-refresh-url:https://api.insee.fr", 
				"sirene-client.token-validity:86400");
		this.context.register(PropertyPlaceholderAutoConfiguration.class, SireneClientAutoconfiguration.class);
		this.context.refresh();
		assertEquals(1, this.context.getBeanNamesForType(SireneClient.class).length);
	}

}
