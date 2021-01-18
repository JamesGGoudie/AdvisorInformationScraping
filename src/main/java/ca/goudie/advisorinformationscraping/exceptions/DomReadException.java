package ca.goudie.advisorinformationscraping.exceptions;

public class DomReadException extends Exception {

	public DomReadException() {
		super();
	}

	public DomReadException(final String reason) {
		super(reason);
	}

	public DomReadException(final Throwable cause) {
		super(cause);
	}

}
