package ca.goudie.advisorinformationscraping.exceptions;

public class ScrapingFailedException extends Exception {

	public ScrapingFailedException() {
		super();
	}

	public ScrapingFailedException(final String reason) {
		super(reason);
	}

	public ScrapingFailedException(final Throwable cause) {
		super(cause);
	}

}
