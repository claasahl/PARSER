package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * The JUnit test for class {@link RemoveIntermediateNodes}. It is intended to
 * collect and document a set of test cases for the tested class. Please refer
 * to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 */
public class RemoveIntermediateNodesTest extends NodeVisitorTest {

	private RemoveIntermediateNodes visitor;

	@Before
	public void before() {
		this.visitor = new RemoveIntermediateNodes();
	}

	@Test
	public void shouldNotRemoveAnything() {
		Node n1 = new NonTerminalNode("n1");
		Node n2 = new TerminalNode("n2");
		n1.addChild(n2);
		n1.visit(this.visitor);

		assertTrue(n1.hasChildren());
		for (Node child : n1) {
			assertEquals(n2, child);
			assertFalse(n2.hasChildren());
		}
	}

	@Test
	public void shouldRemoveSingleNode() {
		Node n1 = new NonTerminalNode("n1");
		Node n2 = new IntermediateNode();
		Node n3 = new TerminalNode("n3");
		n1.addChild(n2);
		n2.addChild(n3);
		n1.visit(this.visitor);

		assertTrue(n1.hasChildren());
		for (Node child : n1) {
			assertEquals(n3, child);
			assertFalse(n3.hasChildren());
		}
	}

	@Test
	public void shouldRemoveMultipleNodes() {
		Node n1 = new NonTerminalNode("n1");
		Node n2 = new IntermediateNode();
		Node n3 = new IntermediateNode();
		Node n4 = new TerminalNode("n4");
		n1.addChild(n2);
		n2.addChild(n3);
		n3.addChild(n4);
		Node n5 = new TerminalNode("n5");
		n1.addChild(n5);
		n1.visit(this.visitor);

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
		Node node = new TerminalNode("terminal");
		node.visit(this.visitor);

		assertFalse(node.hasChildren());
	}

	@Override
	public void shouldHandleIntermediateNode() {
		Node node = new IntermediateNode();
		node.visit(this.visitor);

		assertFalse(node.hasChildren());
	}

	@Override
	public void shouldHandleNonTerminalNode() {
		Node node = new NonTerminalNode("root");
		node.visit(this.visitor);

		assertFalse(node.hasChildren());
	}

	@Override
	public void shouldHandleNodes() {
		Node t1 = new TerminalNode("t1");
		Node t2 = new TerminalNode("t2");
		Node t3 = new TerminalNode("t3");
		Node i1 = new IntermediateNode();
		i1.addChild(t1);
		i1.addChild(t2);
		Node i2 = new IntermediateNode();
		i2.addChild(t3);
		Node node = new NonTerminalNode("root");
		node.addChild(i1);
		node.addChild(i2);
		node.visit(this.visitor);

		Iterator<Node> iterator = node.iterator();
		assertEquals(t1, iterator.next());
		assertEquals(t2, iterator.next());
		assertEquals(t3, iterator.next());
	}

	@Override
	public void shouldHandleCyclicNonTerminalNode() {
		Node node = new NonTerminalNode("root");
		node.addChild(node);
		node.visit(this.visitor);

		assertTrue(node.hasChildren());
		for (Node child : node) {
			assertEquals(node, child);
			assertTrue(node.hasChildren());
		}
	}

	@Override
	public void shouldHandleCyclicIntermediateNode() {
		Node root = new NonTerminalNode("root");
		Node node = new IntermediateNode();
		node.addChild(node);
		root.addChild(node);
		root.visit(this.visitor);

		assertFalse(root.hasChildren());
	}

}
