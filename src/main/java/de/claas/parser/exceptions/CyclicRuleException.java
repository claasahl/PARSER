package de.claas.parser.exceptions;

import de.claas.parser.Rule;

/**
 * The exception {@link CyclicRuleException}. It is thrown to indicate that a
 * graph of {@link Rule}s contains a cyclic rule (i.e. a rule that directly or
 * indirectly references itself) and that the throwing entity cannot handle this
 * cyclic rule.
 * 
 * @author Claas Ahlrichs
 */
public class CyclicRuleException extends RuntimeException {

	private static final long serialVersionUID = 4821496443992456798L;

	/**
	 * Constructs a new {@link CyclicRuleException} with the specified
	 * parameter.
	 * 
	 * @param rule
	 *            the rule where the cycle was detected
	 */
	public CyclicRuleException(Rule rule) {
		super(rule.toString());
	}

}
