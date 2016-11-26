package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;

/**
 * The class {@link Conjunction}. It is an implementation of the {@link Rule}
 * class. It is intended to represent a conjunction of rules within a grammar.
 * 
 * @author Claas Ahlrichs
 */
public class Conjunction extends Rule {

	/**
	 * Constructs a new {@link Conjunction} with the specified parameters.
	 * 
	 * @param children
	 *            the children
	 */
	public Conjunction(Rule... children) {
		super(children);
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitConjunction(this);
	}

}