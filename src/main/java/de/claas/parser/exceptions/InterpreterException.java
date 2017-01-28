package de.claas.parser.exceptions;

import de.claas.parser.visitors.Interpreter;

/**
 * The class {@link InterpreterException}. It is thrown to indicate that an
 * {@link Interpreter} cannot (fully) interpret the data that was passed into
 * it. Consequently, the data is considered to be invalid, e.g. it contains
 * illegal tokens or the data is otherwise not in accordance with the grammar.
 * 
 * @author Claas Ahlrichs
 */
public class InterpreterException extends RuntimeException {

	private static final long serialVersionUID = 7967586094665395837L;

	/**
	 * Constructs a new {@link InterpreterException} with the specified
	 * parameter.
	 * 
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            Throwable.getMessage() method)
	 */
	public InterpreterException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link InterpreterException} with the specified
	 * parameter.
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            Throwable.getCause() method). (A null value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 */
	public InterpreterException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@link InterpreterException} with the specified
	 * parameters.
	 * 
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            Throwable.getMessage() method)
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            Throwable.getCause() method). (A null value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 */
	public InterpreterException(String message, Throwable cause) {
		super(message, cause);
	}

}
