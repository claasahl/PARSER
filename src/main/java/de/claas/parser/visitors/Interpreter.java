package de.claas.parser.visitors;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.exceptions.CyclicNodeException;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link Interpreter}. It is an implementation of the interface
 * {@link NodeVisitor}. It is intended to ...
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to visualize multiple trees.
 *
 * @author Claas Ahlrichs
 *
 */
public abstract class Interpreter<R> implements NodeVisitor {

	private final Set<Node> visitedPath = new HashSet<>();
	private R result;
	private Function<Node, Boolean> expected;

	/**
	 * Returns the result.
	 * 
	 * @return the result
	 */
	public R getResult() {
		return this.result;
	}

	/**
	 * Sets the result.
	 * 
	 * @param result
	 *            the result
	 */
	private void setResult(R result) {
		this.result = result;
	}

	/**
	 * Notifies this interpreter that the next node is expected to be a
	 * {@link NonTerminalNode} with the specified name.
	 * 
	 * @param name
	 *            the name
	 */
	protected void expectNonTerminalNode(String name) {
		this.expected = new Function<Node, Boolean>() {
			@Override
			public Boolean apply(Node node) {
				return Boolean.valueOf(node != null && node.getClass().isAssignableFrom(NonTerminalNode.class)
						&& name.equalsIgnoreCase(((NonTerminalNode) node).getName()));
			}

			@Override
			public String toString() {
				return String.format("Expected '%s' with name '%s'.", NonTerminalNode.class.getSimpleName(), name);
			}
		};
	}

	/**
	 * Notifies this interpreter that the next node is expected to be an
	 * {@link IntermediateNode}.
	 */
	protected void expectIntermediateNode() {
		this.expected = Interpreter.isExpectedClass(IntermediateNode.class);
	}

	/**
	 * Notifies this interpreter that the next node is expected to be a
	 * {@link TerminalNode}.
	 */
	protected void expectTerminalNode() {
		this.expected = Interpreter.isExpectedClass(TerminalNode.class);
	}

	/**
	 * Notifies this interpreter that no further nodes are expected.
	 */
	protected void expectNothing() {
		this.expected = new Function<Node, Boolean>() {
			@Override
			public Boolean apply(Node t) {
				return Boolean.FALSE;
			}

			@Override
			public String toString() {
				return "Expected nothing.";
			}
		};
	}

	/**
	 * Notifies this interpreter that the next node is expected to be identified
	 * by the specified {@link Function}.
	 * 
	 * @param isExpected
	 *            the function
	 */
	protected void expect(Function<Node, Boolean> isExpected) {
		this.expected = isExpected;
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
		return this.expected.apply(node).booleanValue();
	}

	@Override
	public void visitTerminalNode(TerminalNode node) {
		visitUnlessCyclicOrUnexpected(node, this::getTerminal);
	}

	@Override
	public void visitIntermediateNode(IntermediateNode node) {
		visitUnlessCyclicOrUnexpected(node, this::getIntermediate);
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		visitUnlessCyclicOrUnexpected(node, this::getNonTerminal);
	}

	public abstract Function<TerminalNode, R> getTerminal(TerminalNode node);

	public abstract Function<IntermediateNode, R> getIntermediate(IntermediateNode node);

	public abstract Function<NonTerminalNode, R> getNonTerminal(NonTerminalNode node);

	/**
	 * Consumes the specified node, if the node is expected and not cyclic. An
	 * exception is thrown if the node is unexpected or cyclic.
	 * 
	 * @param node
	 *            the node
	 * @param get
	 *            TODO
	 * 
	 * @throws InterpretingException
	 *             if the specified node is unexpected
	 * @throws CyclicNodeException
	 *             if the specified node is cyclic
	 */
	private <T extends Node> void visitUnlessCyclicOrUnexpected(T node, Function<T, Function<T, R>> get) {
		if (this.visitedPath.add(node)) {
			if (isExpected(node)) {
				try {
					setResult(get.apply(node).apply(node));
				} finally {
					this.visitedPath.remove(node);
				}
			} else {
				String msg = "Unexpected node '%s'. %s";
				throw new InterpretingException(String.format(msg, node, this.expected));
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
	private static Function<Node, Boolean> isExpectedClass(Class<?> expectedClass) {
		return new Function<Node, Boolean>() {
			@Override
			public Boolean apply(Node node) {
				return Boolean.valueOf(node != null && node.getClass().isAssignableFrom(expectedClass));
			}

			@Override
			public String toString() {
				return String.format("Expected node type '%s'.", expectedClass.getSimpleName());
			}
		};
	}

}
