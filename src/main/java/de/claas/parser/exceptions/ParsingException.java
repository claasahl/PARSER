package de.claas.parser.exceptions;

import de.claas.parser.visitors.Parser;

/**
 * 
 * The class {@link ParsingException}. It is thrown to indicate that a
 * {@link Parser} cannot (fully) interpret the data that was passed into it. The
 * data is considered invalid, e.g. it contains illegal tokens or the data is
 * otherwise not in accordance with the grammar.
 * 
 * @author Claas Ahlrichs
 *
 */
public class ParsingException extends RuntimeException {

	private static final long serialVersionUID = 7967586094665395837L;

	/**
	 * Constructs a new {@link ParsingException} with the specified parameter.
	 * 
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            Throwable.getMessage() method)
	 */
	public ParsingException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link ParsingException} with the specified parameter.
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            Throwable.getCause() method). (A null value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 */
	public ParsingException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@link ParsingException} with the specified parameters.
	 * 
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            Throwable.getMessage() method)
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            Throwable.getCause() method). (A null value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 */
	public ParsingException(String message, Throwable cause) {
		super(message, cause);
	}

}
