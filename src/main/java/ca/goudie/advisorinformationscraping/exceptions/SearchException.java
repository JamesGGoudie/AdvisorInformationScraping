package ca.goudie.advisorinformationscraping.exceptions;

public class SearchException extends Exception {

	public SearchException() {
		super();
	}

	public SearchException(final String reason) {
		super(reason);
	}

	public SearchException(final Throwable cause) {
		super(cause);
	}

}
