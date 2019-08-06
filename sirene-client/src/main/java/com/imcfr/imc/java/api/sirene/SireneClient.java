package com.imcfr.imc.java.api.sirene;

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

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface with SIRENE API V3.
 */
public interface SireneClient {

	/**
	 * Get a company information from a SIRET number.
	 * 
	 * @param siret the company SIRET
	 * @return json response of SIRENE API, null if the SIRET is unknown
	 * @throws SireneClientException if an exception occurs during the process
	 */
	public JsonNode getBySiret(String siret) throws SireneClientException;

}
