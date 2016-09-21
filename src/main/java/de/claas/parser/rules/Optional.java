package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;

/**
 * 
 * The class {@link Optional}. It is an implementation of the {@link Decorator}
 * class. It is intended to represent an optional rule within a grammar.
 * <p>
 * This rule will successfully process a given state regardless of whether the
 * decorated rule can be successfully processed (or not). Making the decorated
 * rule optional.
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

	@Override
	public boolean equals(Object obj) {
		if (obj != null && Optional.class.isAssignableFrom(obj.getClass())) {
			return super.equals(obj);
		}
		return false;
	}

}