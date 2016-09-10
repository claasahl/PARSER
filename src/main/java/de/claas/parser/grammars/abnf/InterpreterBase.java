package de.claas.parser.grammars.abnf;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

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
	private Function<Node, Boolean> expected;
	private Class<? extends Node> expectedClass;
	private String expectedName;

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
		expectNonTerminalNode(firstNonTerminal);
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
	 * Notifies this interpreter that the next node is expected to be a
	 * {@link NonTerminalNode} with the specified name.
	 * 
	 * @param name
	 *            the name
	 */
	protected void expectNonTerminalNode(String name) {
		this.expectedName = name;
		this.expectedClass = NonTerminalNode.class;
		this.expected = this::isExpectedNonTerminal;
	}

	/**
	 * Notifies this interpreter that the next node is expected to be an
	 * {@link IntermediateNode}.
	 */
	protected void expectIntermediateNode() {
		this.expectedName = null;
		this.expectedClass = IntermediateNode.class;
		this.expected = this::isExpectedClass;
	}

	/**
	 * Notifies this interpreter that the next node is expected to be a
	 * {@link TerminalNode}.
	 */
	protected void expectTerminalNode() {
		this.expectedName = null;
		this.expectedClass = TerminalNode.class;
		this.expected = this::isExpectedClass;
	}

	/**
	 * Tests if the specified node is expected.
	 * 
	 * @param node
	 *            the node
	 * @return <code>true</code> if the specified node is expected,
	 *         <code>false</code> otherwise
	 */
	protected boolean isExpected(Node node) {
		return expected.apply(node);
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
			if (isExpected(node)) {
				try {
					consumer.accept(node);
				} finally {
					visitedPath.remove(node);
				}
			} else {
				String msg = "Expected node '%s', but got node '%s'.";
				String expectedNode = expectedClass.getSimpleName();
				String unexpectedNode = node.getClass().getSimpleName();
				throw new InterpretingException(String.format(msg, expectedNode, unexpectedNode));
			}
		} else {
			throw new CyclicNodeException(node);
		}
	}

	/**
	 * Tests if the specified node's type is expected.
	 * 
	 * @param node
	 *            the node
	 * @return <code>true</code> if the specified node's type is expected,
	 *         <code>false</code> otherwise
	 */
	private boolean isExpectedClass(Node node) {
		return node != null && node.getClass().isAssignableFrom(expectedClass);
	}

	/**
	 * Tests if the specified node is a {@link NonTerminalNode} with the
	 * expected name.
	 * 
	 * @param node
	 *            the node
	 * @return <code>true</code> if the specified node is a
	 *         {@link NonTerminalNode} with the expected name,
	 *         <code>false</code> otherwise
	 */
	private boolean isExpectedNonTerminal(Node node) {
		return isExpectedClass(node) && expectedName.equalsIgnoreCase(((NonTerminalNode) node).getName());
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