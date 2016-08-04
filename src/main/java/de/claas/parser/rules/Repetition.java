package de.claas.parser.rules;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.State;
import de.claas.parser.results.IntermediateNode;

/**
 * 
 * The class {@link Repetition}. It is an implementation of the
 * {@link Decorator} class. It is intended to represent a repeatable rule within
 * a grammar.
 * <p>
 * This rule will successfully process a given state regardless of how often the
 * decorated rule can be processed. Making the decorated rule optional and
 * repeatable at the same time.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Repetition extends Decorator {

	/**
	 * Creates an instance with the given parameter.
	 * 
	 * @param rule
	 *            the repeatable rule
	 */
	public Repetition(Rule rule) {
		super(rule);
	}

	@Override
	public Node process(State state) {
		state.beginGroup();
		try {
			Node node = null;
			Node child = null;
			if (getRule() != null) {
				node = new IntermediateNode();
				while ((child = getRule().process(state)) != null) {
					node.addChild(child);
				}
			}
			return node;
		} finally {
			state.endGroup();
		}
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitRepetition(this);
	}

}