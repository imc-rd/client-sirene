package com.imcfr.imc.java.api.sirene;

public class SireneClientException extends Exception {

	private static final long serialVersionUID = 5867720380487752731L;

	public SireneClientException(String message) {
		super(message);
	}

	public SireneClientException(String message, Throwable error) {
		super(message, error);
	}

}
