package ca.goudie.advisorinformationscraping.exceptions;

public class ResultMissingException extends Exception {

	public ResultMissingException() {
		super();
	}

	public ResultMissingException(final String reason) {
		super(reason);
	}

	public ResultMissingException(final Throwable cause) {
		super(cause);
	}

}
