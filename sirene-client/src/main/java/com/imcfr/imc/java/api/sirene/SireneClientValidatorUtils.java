package com.imcfr.imc.java.api.sirene;

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
