package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import de.claas.parser.Node;

/**
 * 
 * The JUnit test for class {@link RemoveIntermediateNodesTest}. It is intended
 * to collect and document a set of test cases for the tested class. Please
 * refer to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class RemoveIntermediateNodesTest extends NodeVisitorTest {

	private RemoveIntermediateNodes visitor;

	@Before
	public void before() {
		visitor = new RemoveIntermediateNodes();
	}

	@Test
	public void shouldNotRemoveAnything() {
		Node n1 = buildNonTerminalNode(false, "n1");
		Node n2 = buildTerminalNode("n2");
		n1.addChild(n2);
		n1.visit(visitor);

		assertTrue(n1.hasChildren());
		for (Node child : n1) {
			assertEquals(n2, child);
			assertFalse(n2.hasChildren());
		}
	}

	@Test
	public void shouldRemoveSingleNode() {
		Node n1 = buildNonTerminalNode(false, "n1");
		Node n2 = buildIntermediateNode(false);
		Node n3 = buildTerminalNode("n3");
		n1.addChild(n2);
		n2.addChild(n3);
		n1.visit(visitor);

		assertTrue(n1.hasChildren());
		for (Node child : n1) {
			assertEquals(n3, child);
			assertFalse(n3.hasChildren());
		}
	}

	@Test
	public void shouldRemoveMultipleNodes() {
		Node n1 = buildNonTerminalNode(false, "n1");
		Node n2 = buildIntermediateNode(false);
		Node n3 = buildIntermediateNode(false);
		Node n4 = buildTerminalNode("n4");
		n1.addChild(n2);
		n2.addChild(n3);
		n3.addChild(n4);
		Node n5 = buildTerminalNode("n5");
		n1.addChild(n5);
		n1.visit(visitor);

		Iterator<Node> iterator = n1.iterator();
		assertEquals(n4, iterator.next());
		assertFalse(n4.hasChildren());
		assertEquals(n5, iterator.next());
		assertFalse(n5.hasChildren());
	}

	@Override
	public void shouldHandleNoNode() {
		// nothing to be done
	}

	@Override
	public void shouldHandleTerminalNode() {
		Node node = buildTerminalNode("terminal");
		node.visit(visitor);

		assertFalse(node.hasChildren());
	}

	@Override
	public void shouldHandleIntermediateNode() {
		Node node = buildIntermediateNode(false);
		node.visit(visitor);

		assertFalse(node.hasChildren());
	}

	@Override
	public void shouldHandleNonTerminalNode() {
		Node node = buildNonTerminalNode(false, "root");
		node.visit(visitor);

		assertFalse(node.hasChildren());
	}

	@Override
	public void shouldHandleNodes() {
		Node t1 = buildTerminalNode("t1");
		Node t2 = buildTerminalNode("t2");
		Node t3 = buildTerminalNode("t3");
		Node i1 = buildIntermediateNode(false);
		i1.addChild(t1);
		i1.addChild(t2);
		Node i2 = buildIntermediateNode(false);
		i2.addChild(t3);
		Node node = buildNonTerminalNode(false, "root");
		node.addChild(i1);
		node.addChild(i2);
		node.visit(visitor);

		Iterator<Node> iterator = node.iterator();
		assertEquals(t1, iterator.next());
		assertEquals(t2, iterator.next());
		assertEquals(t3, iterator.next());
	}

	@Override
	public void shouldHandleCyclicNonTerminalNode() {
		Node node = buildNonTerminalNode(true, "root");
		node.visit(visitor);

		assertTrue(node.hasChildren());
		for (Node child : node) {
			assertEquals(node, child);
			assertTrue(node.hasChildren());
		}
	}

	@Override
	public void shouldHandleCyclicIntermediateNode() {
		Node node = buildNonTerminalNode(false, "root");
		node.addChild(buildIntermediateNode(true));
		node.visit(visitor);

		assertFalse(node.hasChildren());
	}

}
