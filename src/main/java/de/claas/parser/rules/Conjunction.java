package de.claas.parser.rules;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.State;
import de.claas.parser.results.IntermediateNode;

/**
 * 
 * The class {@link Conjunction}. It is an implementation of the {@link Rule}
 * class. It is intended to represent a conjunction of rules within a grammar.
 * <p>
 * This rule will only successfully process a given state if all children have
 * successfully been processed.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Conjunction extends Rule {

	/**
	 * Creates an instance with the given parameters.
	 * 
	 * @param children
	 *            the children
	 */
	public Conjunction(Rule... children) {
		super(children);
	}

	@Override
	public Node process(State state) {
		state.beginGroup();
		try {
			Node node = hasChildren() ? new IntermediateNode() : null;
			for (Rule rule : this) {
				Node child = rule.process(state);
				if (child != null) {
					node.addChild(child);
				} else {
					state.revert();
					return null;
				}
			}
			return node;
		} finally {
			state.endGroup();
		}
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitConjunction(this);
	}

}