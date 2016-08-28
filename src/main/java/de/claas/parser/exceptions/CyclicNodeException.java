package de.claas.parser.exceptions;

import de.claas.parser.Rule;

public class CyclicNodeException extends RuntimeException {

	private static final long serialVersionUID = -3896618031014844794L;

	public CyclicNodeException(Rule rule) {
		super(rule.toString());
	}

}
