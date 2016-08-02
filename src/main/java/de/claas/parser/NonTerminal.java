package de.claas.parser;

/**
 * 
 * The class {@link NonTerminal}. It is an implementation of the
 * {@link Decorator} class. It is intended to represent a non-terminal rule
 * within a grammar. Non-terminal rules are named rules which largely make up a
 * grammar.
 * <p>
 * This rule acts like any other rule. The only difference is that it has a
 * name.
 * 
 * @author Claas Ahlrichs
 *
 */
public class NonTerminal extends Decorator {

	private final String name;

	/**
	 * Creates an instance with the given parameter.
	 * 
	 * @param name
	 *            the name
	 */
	public NonTerminal(String name) {
		this(name, null);
	}

	/**
	 * Creates an instance with the given parameters.
	 * 
	 * @param name
	 *            the name
	 * @param rule
	 *            the decorated rule
	 */
	public NonTerminal(String name, Rule rule) {
		super(rule);
		this.name = name;
	}

	/**
	 * Returns the name of this (non-terminal) rule.
	 * 
	 * @return the name of this (non-terminal) rule
	 */
	public String getName() {
		return name;
	}

	@Override
	public Node process(State state) {
		Node node = null;
		if (getRule() != null) {
			Node child = getRule().process(state);
			if (child != null) {
				node = new NonTerminalNode(getName());
				node.addChild(child);
			}
		}
		return node;
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitNonTerminal(this);
	}

}