package de.claas.parser.grammars.abnf;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.Rule;
import de.claas.parser.exceptions.CyclicNodeException;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link InterpreterBase}. It is an implementation of the interface
 * {@link NodeVisitor}. It is intended to provide common functionality for
 * interpreters (i.e. detection of cycles and unexpected nodes). Thus, concrete
 * interpreters can focus on their main task.
 *
 * 
 * @author Claas Ahlrichs
 *
 */
public abstract class InterpreterBase implements NodeVisitor {

	private final Set<Node> visitedPath = new HashSet<>();
	private Rule rule;
	private Class<? extends Node> expected;
	private String expectedNonTerminal;

	/**
	 * 
	 * Constructs a new {@link InterpreterBase} with the specified parameters.
	 * The first non-terminal's name can be retrieved via
	 * {@link #getExpectedNonTerminal()}.
	 * 
	 * @param firstNonTerminal
	 *            the name of the first non-terminal
	 */
	public InterpreterBase(String firstNonTerminal) {
		this.expectedNonTerminal = firstNonTerminal;
		expect(NonTerminalNode.class);
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
	 * Tests if the specified non-terminal is expected.
	 * 
	 * @param node
	 *            the non-terminal being tested
	 * @return <code>true</code>, if the non-terminal's name corresponds to the
	 *         value returned by {@link #getExpectedNonTerminal()}. Otherwise,
	 *         <code>false</code>
	 */
	protected boolean isNonTerminalExpected(NonTerminalNode node) {
		return node.getName().equalsIgnoreCase(expectedNonTerminal);
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

	/**
	 * Sets the class of the node that is expected to be visited next.
	 * 
	 * @param expected
	 *            the class of the next node
	 */
	protected void expect(Class<? extends Node> expected) {
		this.expected = expected;
	}

	@Override
	public void visitTerminalNode(TerminalNode node) {
		visitUnlessCyclicOrUnexpected(this::visitingTerminalNode, node);
	}

	@Override
	public void visitIntermediateNode(IntermediateNode node) {
		visitUnlessCyclicOrUnexpected(this::visitingIntermediateNode, node);
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		visitUnlessCyclicOrUnexpected(this::visitingNonTerminalNode, node);
	}

	/**
	 * Consumes the specified node, if the node is expected and not cyclic. An
	 * exception is thrown if the node is unexpected or cyclic.
	 * 
	 * @param consumer
	 *            the node's consumer
	 * @param node
	 *            the node
	 * @throws InterpretingException
	 *             if the specified node is unexpected
	 * @throws CyclicNodeException
	 *             if the specified node is cyclic
	 */
	private <T extends Node> void visitUnlessCyclicOrUnexpected(Consumer<T> consumer, T node) {
		if (visitedPath.add(node)) {
			if (node != null && node.getClass().isAssignableFrom(expected)) {
				try {
					consumer.accept(node);
				} finally {
					visitedPath.remove(node);
				}
			} else {
				String msg = "Expected node '%s', but got node '%s'.";
				String expectedNode = expected.getSimpleName();
				String unexpectedNode = node.getClass().getSimpleName();
				throw new InterpretingException(String.format(msg, expectedNode, unexpectedNode));
			}
		} else {
			throw new CyclicNodeException(node);
		}
	}

	/**
	 * Called by {@link TerminalNode}-nodes, if the node is expected and not
	 * cyclic.
	 * 
	 * @param node
	 *            the node
	 */
	protected abstract void visitingTerminalNode(TerminalNode node);

	/**
	 * Called by {@link IntermediateNode}-nodes, if the node is expected and not
	 * cyclic. The default implementation will simply visit all children of the
	 * given node.
	 * 
	 * @param node
	 *            the node
	 */
	protected void visitingIntermediateNode(IntermediateNode node) {
		for (Node n : node) {
			n.visit(this);
		}
	}

	/**
	 * Called by {@link NonTerminalNode}-nodes, if the node is expected and not
	 * cyclic.
	 * 
	 * @param node
	 *            the node
	 */
	protected abstract void visitingNonTerminalNode(NonTerminalNode node);

}