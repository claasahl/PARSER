package de.claas.parser;

/**
 * 
 * The class {@link Optional}. It is an implementation of the {@link Decorator}
 * class. It is intended to represent an optional rule within a grammar.
 * <p/>
 * This rule will successfully process a given state regardless of whether the
 * decorated rule can be successfully processed (or not). Making the decorated
 * rule optional.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Optional extends Decorator {

	/**
	 * Creates an instance with the given parameter.
	 * 
	 * @param rule
	 *            the optional rule
	 */
	public Optional(Rule rule) {
		super(rule);
	}

	@Override
	public Node process(State state) {
		state.beginGroup();
		try {
			Node node = null;
			if (getRule() != null) {
				node = new IntermediateNode();
				Node child = getRule().process(state);
				if (child != null) {
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
		visitor.visitOptional(this);
	}

}