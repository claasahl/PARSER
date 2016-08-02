package de.claas.parser;

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
			for (Rule rule : this) {
				Node child = rule.process(state);
				if (child != null) {
					Node node = new IntermediateNode();
					node.addChild(child);
					return node;
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