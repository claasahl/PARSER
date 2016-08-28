package de.claas.parser.exceptions;

import de.claas.parser.Rule;

public class CyclicRuleException extends RuntimeException {

	private static final long serialVersionUID = 4821496443992456798L;

	public CyclicRuleException(Rule rule) {
		super(rule.toString());
	}

}
