package de.claas.parser.rules;

import de.claas.parser.Node;
import de.claas.parser.Result;
import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.State;
import de.claas.parser.results.IntermediateNode;

/**
 * 
 * The class {@link Disjunction}. It is an implementation of the {@link Rule}
 * class. It is intended to represent a disjunction of rules within a grammar.
 * <p>
 * This rule will successfully process a given state as long as any child can
 * successfully be processed.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Disjunction extends Rule {

	/**
	 * Creates an instance with the given parameters.
	 * 
	 * @param children
	 *            the children
	 */
	public Disjunction(Rule... children) {
		super(children);
	}

	@Override
	public Node process(State state) {
		state.beginGroup();
		try {
			Node node = new IntermediateNode();
			for (Rule rule : this) {
				Node result = Result.get(rule, state, node, null);
				if(result != null) {
					return result;
				}
			}
			state.revert();
			return null;
		} finally {
			state.endGroup();
		}
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitDisjunction(this);
	}

}