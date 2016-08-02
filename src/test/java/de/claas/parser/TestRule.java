package de.claas.parser;

/**
 * 
 * The class {@link TestRule}. It is an implementation of the {@link Rule}
 * class. It successfully parses any token that equals this rule's name. It is
 * intended for testing purposes, only.
 * <p>
 * This rule will only successfully process a given state if the processed token
 * equals {@link #getName()}. successfully been processed.
 * 
 * @author Claas Ahlrichs
 *
 */
public class TestRule extends Rule {
	private final String name;
	private final Node output;

	/**
	 * Creates an instance with the given parameters. The specified node is
	 * returned by {@link #process(State)} if the processed token equals
	 * {@link #getName()}.
	 * 
	 * @param name
	 *            the name
	 * @param output
	 *            the node returned by {@link #process(State)}
	 * @param children
	 *            the children
	 */
	public TestRule(String name, Node output, Rule... children) {
		super(children);
		this.name = name;
		this.output = output;
	}

	/**
	 * Returns the name of this rule.
	 * 
	 * @return the name of this rule
	 */
	public String getName() {
		return name;
	}

	@Override
	public Node process(State state) {
		String token = state.processToken();
		if (token == null)
			return null;

		if (token.equalsIgnoreCase(name)) {
			return this.output;
		} else {
			state.unprocessToken();
			return null;
		}
	}

	@Override
	public void visit(RuleVisitor visitor) {
		throw new IllegalStateException("should not be called");
	}
}
