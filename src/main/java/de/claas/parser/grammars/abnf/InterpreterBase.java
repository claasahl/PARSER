package de.claas.parser.grammars.abnf;

import de.claas.parser.NodeVisitor;
import de.claas.parser.Rule;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link InterpreterBase}. It is an implementation of the interface
 * {@link NodeVisitor}. It is intended to provide ...
 *
 * 
 * @author Claas Ahlrichs
 *
 */
public abstract class InterpreterBase implements NodeVisitor {

	private Rule rule;
	private String expectedNonTerminal;

	/**
	 * 
	 * Constructs a new {@link InterpreterBase} with the specified parameters.
	 * The first non-terminal's name can be via
	 * {@link #getExpectedNonTerminal()}.
	 * 
	 * @param firstNonTerminal
	 *            the name of the first non-terminal
	 */
	public InterpreterBase(String firstNonTerminal) {
		this.expectedNonTerminal = firstNonTerminal;
	}

	/**
	 * Returns the rule.
	 * 
	 * @return the rule
	 */
	public Rule getRule() {
		return rule;
	}

	/**
	 * Sets the rule.
	 * 
	 * @param rule
	 *            the rule
	 */
	protected void setRule(Rule rule) {
		this.rule = rule;
	}

	/**
	 * Tests if the specified node is expected.
	 * 
	 * @param node
	 *            the non-terminal being tested
	 * @return <code>true</code>, if the non-terminal's name corresponds to the
	 *         value returned by {@link #getExpectedNonTerminal()}. Otherwise,
	 *         <code>false</code>
	 */
	protected boolean isExpectedNonTerminal(NonTerminalNode node) {
		return node.getName().equalsIgnoreCase(expectedNonTerminal);
	}

	// TODO
	protected boolean isExpectedTerminal() {
		return expectedNonTerminal == null;
	}

	/**
	 * Returns the name of the next expected non-terminal.
	 * 
	 * @return the name of the next expected non-terminal
	 */
	protected String getExpectedNonTerminal() {
		return expectedNonTerminal;
	}

	/**
	 * Sets the name of the next expected non-terminal. Setting the name to
	 * <code>null</code> implies that the next node is expected to be a
	 * {@link TerminalNode}.
	 * 
	 * @param expectedNonTerminal
	 *            the name of the next expected non-terminal
	 */
	protected void setExpectedNonTerminal(String expectedNonTerminal) {
		this.expectedNonTerminal = expectedNonTerminal;
	}

}