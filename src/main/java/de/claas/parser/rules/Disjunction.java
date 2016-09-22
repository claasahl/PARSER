package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;

/**
 * 
 * The class {@link Disjunction}. It is an implementation of the {@link Rule}
 * class. It is intended to represent a disjunction of rules within a grammar.
 * <p>
 * This rule will successfully process a given state as long as any child can
 * successfully be processed. This rule is greedy and thus it gives preference
 * to the child that processes most data.
 * 
 * @author Claas Ahlrichs
 *
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