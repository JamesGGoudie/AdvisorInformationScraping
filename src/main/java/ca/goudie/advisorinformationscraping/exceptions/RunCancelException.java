package ca.goudie.advisorinformationscraping.exceptions;

public class RunCancelException extends Exception {

	public RunCancelException() {
		super();
	}

	public RunCancelException(final String reason) {
		super(reason);
	}

	public RunCancelException(final Throwable cause) {
		super(cause);
	}

}
