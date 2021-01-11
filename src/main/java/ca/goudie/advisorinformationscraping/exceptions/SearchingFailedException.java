package ca.goudie.advisorinformationscraping.exceptions;

public class SearchingFailedException extends Exception {

	public SearchingFailedException() {
		super();
	}

	public SearchingFailedException(final String reason) {
		super(reason);
	}

	public SearchingFailedException(final Throwable cause) {
		super(cause);
	}

}
