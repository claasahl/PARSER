package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;

/**
 * 
 * The class {@link Optional}. It is an implementation of the {@link Decorator}
 * class. It is intended to represent an optional rule within a grammar.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Optional extends Decorator {

	/**
	 * Constructs a new {@link Optional} with the specified parameter.
	 * 
	 * @param rule
	 *            the optional rule
	 */
	public Optional(Rule rule) {
		super(rule);
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitOptional(this);
	}

}