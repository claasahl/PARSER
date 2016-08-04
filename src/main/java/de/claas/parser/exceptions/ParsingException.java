package de.claas.parser.exceptions;

/**
 * 
 * Superclass of all specialized exceptions in this package.
 * 
 * @author Claas Ahlrichs
 *
 */
public class ParsingException extends Exception {

	private static final long serialVersionUID = 7967586094665395837L;

	/**
	 * @see Exception#Exception()
	 */
	public ParsingException() {
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public ParsingException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public ParsingException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public ParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable, boolean, boolean)
	 */
	public ParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
