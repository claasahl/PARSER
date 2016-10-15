package de.claas.parser.visitors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 *
 * The JUnit test for class {@link NodeEquality}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class NodeEqualityTest extends NodeVisitorTest {

	/**
	 * Returns an instantiated {@link NodeEquality} class with the specified
	 * parameter.
	 * 
	 * @param obj
	 *            the reference object with which the visited {@link Node}s are
	 *            compared
	 * 
	 * @return an instantiated {@link NodeEquality} class with the specified
	 *         parameter
	 */
	@SuppressWarnings("static-method")
	private NodeEquality build(Object obj) {
		return new NodeEquality(obj);
	}

	@Override
	public void shouldHandleNoNode() {
		NodeEquality visitor = build(null);
		assertFalse(visitor.isEquality());
	}

	@Override
	public void shouldHandleTerminalNode() {
		Object obj = new TerminalNode("some terminal");

		NodeEquality visitor = build(obj);
		Node node = new TerminalNode("some terminal");
		node.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(obj);
		node = new TerminalNode("some other terminal");
		node.visit(visitor);
		assertFalse(visitor.isEquality());
	}

	@Override
	public void shouldHandleIntermediateNode() {
		Object obj = new IntermediateNode();

		NodeEquality visitor = build(obj);
		Node node = new IntermediateNode();
		node.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(obj);
		node = new TerminalNode("not an intermediate node");
		node.visit(visitor);
		assertFalse(visitor.isEquality());
	}

	@Override
	public void shouldHandleNonTerminalNode() {
		Object obj = new NonTerminalNode("some non-terminal");

		NodeEquality visitor = build(obj);
		Node node = new NonTerminalNode("some non-terminal");
		node.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(obj);
		node = new NonTerminalNode("some other non-terminal");
		node.visit(visitor);
		assertFalse(visitor.isEquality());
	}

	@Override
	public void shouldHandleNodes() {
		Node t = new TerminalNode("terminal");
		Node i = new IntermediateNode();
		i.addChild(t);
		Node obj = new NonTerminalNode("non-terminal");
		obj.addChild(i);

		NodeEquality visitor = build(obj);
		t = new TerminalNode("terminal");
		i = new IntermediateNode();
		i.addChild(t);
		Node node = new NonTerminalNode("non-terminal");
		node.addChild(i);
		node.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Override
	public void shouldHandleCyclicNonTerminalNode() {
		Node obj = new NonTerminalNode("non-terminal");
		obj.addChild(obj);

		NodeEquality visitor = build(obj);
		Node node = new NonTerminalNode("non-terminal");
		node.addChild(node);
		node.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Override
	public void shouldHandleCyclicIntermediateNode() {
		Node obj = new IntermediateNode();
		obj.addChild(obj);

		NodeEquality visitor = build(obj);
		Node node = new IntermediateNode();
		node.addChild(node);
		node.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Test
	public void shouldHandleNull() {
		NodeEquality visitor = build(null);
		Node node = new IntermediateNode();
		node.visit(visitor);
		assertFalse(visitor.isEquality());
	}

	@Test
	public void shouldBeReflexive() {
		Node node = new IntermediateNode();

		NodeEquality visitor = build(node);
		node.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Test
	public void shouldBeSymmetric() {
		Node nodeA = new IntermediateNode();
		Node nodeB = new IntermediateNode();

		NodeEquality visitor = build(nodeA);
		nodeB.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(nodeB);
		nodeA.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Test
	public void shouldBeTransitive() {
		Node nodeA = new IntermediateNode();
		Node nodeB = new IntermediateNode();
		Node nodeC = new IntermediateNode();

		NodeEquality visitor = build(nodeB);
		nodeA.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(nodeC);
		nodeB.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(nodeC);
		nodeA.visit(visitor);
		assertTrue(visitor.isEquality());
	}

}
