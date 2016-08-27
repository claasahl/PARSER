package de.claas.parser.visitors;

import org.junit.Test;

import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;;

/**
 * 
 * The JUnit test for class {@link NodeVisitorTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public abstract class NodeVisitorTest {

	@Test
	public abstract void shouldHandleNoNode();

	@Test
	public abstract void shouldHandleTerminalNode();

	@Test
	public abstract void shouldHandleIntermediateNode();

	@Test
	public abstract void shouldHandleNonTerminalNode();

	@Test
	public abstract void shouldHandleNodes();

	@Test
	public abstract void shouldHandleCyclicNonTerminalNode();

	@Test
	public abstract void shouldHandleCyclicIntermediateNode();

	/**
	 * Returns a terminal node with the specified terminal symbol.
	 * 
	 * @param terminal
	 *            the terminal symbol
	 * @return a terminal node
	 */
	protected TerminalNode buildTerminalNode(String terminal) {
		return new TerminalNode(terminal);
	}

	/**
	 * Returns an intermediate node. Optionally, a cyclic node can be returned
	 * by setting the cyclic parameter to <code>true</code> (i.e. cyclic with
	 * itself; recursive).
	 * 
	 * @param cyclic
	 *            whether the node should be cyclic
	 * @return an intermediate node
	 */
	protected IntermediateNode buildIntermediateNode(boolean cyclic) {
		if (cyclic) {
			IntermediateNode node = new IntermediateNode();
			node.addChild(node);
			return node;
		} else {
			return new IntermediateNode();
		}
	}

	/**
	 * Returns a non terminal node with the specified name. Optionally, a cyclic
	 * node can be returned by setting the cyclic parameter to <code>true</code>
	 * (i.e. cyclic with itself; recursive).
	 * 
	 * @param cyclic
	 *            whether the node should be cyclic
	 * @param name
	 *            the name
	 * @return a non terminal node
	 */
	protected NonTerminalNode buildNonTerminalNode(boolean cyclic, String name) {
		if (cyclic) {
			NonTerminalNode node = new NonTerminalNode(name);
			node.addChild(node);
			return node;
		} else {
			return new NonTerminalNode(name);
		}
	}

}
