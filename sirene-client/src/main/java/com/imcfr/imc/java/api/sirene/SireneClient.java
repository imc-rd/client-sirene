package com.imcfr.imc.java.api.sirene;

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
