package ca.goudie.advisorinformationscraping.exceptions;

public class UrlParseException extends Exception {

	public UrlParseException() {
		super();
	}

	public UrlParseException(final String reason) {
		super(reason);
	}

	public UrlParseException(final Throwable cause) {
		super(cause);
	}

}
