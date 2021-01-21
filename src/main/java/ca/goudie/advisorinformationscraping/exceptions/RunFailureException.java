package ca.goudie.advisorinformationscraping.exceptions;

public class RunFailureException extends Exception {

	public RunFailureException() {
		super();
	}

	public RunFailureException(final String reason) {
		super(reason);
	}

	public RunFailureException(final Throwable cause) {
		super(cause);
	}

}
