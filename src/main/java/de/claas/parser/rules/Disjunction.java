package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;

/**
 * The class {@link Disjunction}. It is an implementation of the {@link Rule}
 * class. It is intended to represent a disjunction of rules within a grammar.
 * 
 * @author Claas Ahlrichs
 */
public class Disjunction extends Rule {

	/**
	 * Constructs a new {@link Disjunction} with the specified parameters.
	 * 
	 * @param children
	 *            the children
	 */
	public Disjunction(Rule... children) {
		super(children);
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitDisjunction(this);
	}

}