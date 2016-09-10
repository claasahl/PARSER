package de.claas.parser.exceptions;

import de.claas.parser.Grammar;

/**
 * 
 * The class {@link InvalidNonTerminalException}. It is thrown to indicate that a
 * {@link Grammar} cannot parse the data that was passed into it. The data is
 * considered invalid, e.g. it contains illegal tokens or the data is otherwise
 * not in accordance with the grammar.
 * TODO
 * 
 * @author Claas Ahlrichs
 *
 */
public class InvalidNonTerminalException extends RuntimeException {

	private static final long serialVersionUID = 7967586094665395837L;

	/**
	 * @see Exception#Exception()
	 */
	public InvalidNonTerminalException() {
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public InvalidNonTerminalException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public InvalidNonTerminalException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public InvalidNonTerminalException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable, boolean, boolean)
	 */
	public InvalidNonTerminalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
