package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;

import de.claas.parser.Node;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 *
 * The JUnit test for class {@link NodeHashCode}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class NodeHashCodeTest extends NodeVisitorTest {

	/**
	 * Returns an instantiated {@link NodeHashCode} class with default values.
	 * 
	 * @return an instantiated {@link NodeHashCode} class with default values
	 */
	@SuppressWarnings("static-method")
	private NodeHashCode build() {
		return new NodeHashCode();
	}

	@Override
	public void shouldHandleNoNode() {
		NodeHashCode visitor = build();
		assertEquals(0, visitor.getHashCode());
	}

	@Override
	public void shouldHandleTerminalNode() {
		NodeHashCode visitor = build();
		Node node = new TerminalNode("some terminal");
		node.visit(visitor);

		int expected = node.getClass().hashCode();
		expected += "some terminal".hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleIntermediateNode() {
		NodeHashCode visitor = build();
		Node node = new IntermediateNode();
		node.visit(visitor);

		int expected = node.getClass().hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleNonTerminalNode() {
		NodeHashCode visitor = build();
		Node node = new NonTerminalNode("some non-terminal");
		node.visit(visitor);

		int expected = node.getClass().hashCode();
		expected += "some non-terminal".hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleNodes() {
		NodeHashCode visitor = build();
		Node t = new TerminalNode("terminal");
		Node i = new IntermediateNode();
		i.addChild(t);
		Node node = new NonTerminalNode("non-terminal");
		node.addChild(i);
		node.visit(visitor);

		int expected = node.getClass().hashCode();
		expected += "non-terminal".hashCode();
		expected += i.getClass().hashCode();
		expected += t.getClass().hashCode();
		expected += "terminal".hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleCyclicNonTerminalNode() {
		NodeHashCode visitor = build();
		Node node = new NonTerminalNode("non-terminal");
		node.addChild(node);
		node.visit(visitor);

		int expected = node.getClass().hashCode();
		expected += "non-terminal".hashCode();
		expected += node.getClass().hashCode();
		expected += "non-terminal".hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleCyclicIntermediateNode() {
		NodeHashCode visitor = build();
		Node node = new IntermediateNode();
		node.addChild(node);
		node.visit(visitor);

		int expected = node.getClass().hashCode();
		expected += node.getClass().hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

}
