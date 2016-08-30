package de.claas.parser.rules;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.State;

/**
 * 
 * The class {@link TestRule}. It is an implementation of the {@link Rule}
 * class. It successfully parses any token that equals this rule's name. It is
 * intended for testing purposes, only.
 * <p>
 * This rule will only successfully process a given state if the processed token
 * equals {@link #getName()}.
 * 
 * @author Claas Ahlrichs
 *
 */
public class TestRule extends Rule {
	private final String name;
	private final Node output;

	/**
	 * Constructs a new {@link TestRule} with the specified parameters. The
	 * specified node is returned by {@link #process(State)} if the processed
	 * token equals {@link #getName()}.
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
		if (state.process(name)) {
			return this.output;
		} else {
			return null;
		}
	}

	@Override
	public void visit(RuleVisitor visitor) {
		throw new IllegalStateException("should not be called");
	}
}
