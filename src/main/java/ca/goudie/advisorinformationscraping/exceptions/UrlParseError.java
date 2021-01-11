package ca.goudie.advisorinformationscraping.exceptions;

public class UrlParseError extends Exception {

	public UrlParseError() {
		super();
	}

	public UrlParseError(final String reason) {
		super(reason);
	}

	public UrlParseError(final Throwable cause) {
		super(cause);
	}

}
