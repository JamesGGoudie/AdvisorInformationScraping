package ca.goudie.advisorinformationscraping.exceptions;

public class ScrapeException extends Exception {

	public ScrapeException() {
		super();
	}

	public ScrapeException(final String reason) {
		super(reason);
	}

	public ScrapeException(final Throwable cause) {
		super(cause);
	}

}
