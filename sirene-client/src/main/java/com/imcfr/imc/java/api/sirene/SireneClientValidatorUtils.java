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

public class SireneClientValidatorUtils {

	private static final String DEFAULT_EMAIL_REGEX = "^[A-Za-z0-9][A-Za-z0-9-\\+_]*(\\.[_A-Za-z0-9-\\+]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private SireneClientValidatorUtils() {
	}

	public static boolean validEmail(String value) {
		return stringValidate(DEFAULT_EMAIL_REGEX, value);
	}

	private static boolean stringValidate(String regex, String value) {
		return value.matches(regex);
	}
	
	public static boolean validateSiret(String siret) {
		int total = 0;
		int digit = 0;
		
		if (siret.length() != 14) 
			return false;
		
		for (int i = 0; i < siret.length() ; i++) {
			if ((i % 2) == 0) {
				digit = Integer.parseInt(String.valueOf(siret.charAt(i))) * 2;
				if (digit > 9) digit -= 9;
			}
			else digit = Integer.parseInt(String.valueOf(siret.charAt(i)));
			total += digit;
		}
		return ((total % 10) == 0);
	}
	
}
