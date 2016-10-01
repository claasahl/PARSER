package de.claas.parser.exceptions;

import de.claas.parser.Grammar;

/**
 * 
 * The class {@link ParsingException}. It is thrown to indicate that a
 * {@link Grammar} cannot parse the data that was passed into it. The data is
 * considered invalid, e.g. it contains illegal tokens or the data is otherwise
 * not in accordance with the grammar.
 * 
 * @author Claas Ahlrichs
 *
 */
public class ParsingException extends RuntimeException {

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
