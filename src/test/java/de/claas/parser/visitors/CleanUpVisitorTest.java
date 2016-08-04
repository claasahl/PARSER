package de.claas.parser.visitors;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.visitors.CleanUpVisitor;

/**
 * 
 * The JUnit test for class {@link CleanUpVisitorTest}. It is intended to
 * collect and document a set of test cases for the tested class. Please refer
 * to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class CleanUpVisitorTest {

	private CleanUpVisitor visitor;

	@Before
	public void before() {
		visitor = new CleanUpVisitor();
	}

	@Test
	public void shouldNotRemoveAnything() {
		Node n1 = new NonTerminalNode("n1");
		Node n2 = new TerminalNode("n2");
		n1.addChild(n2);
		
		n1.visit(visitor);
		Iterator<Node> iterator = n1.iterator();
		assertEquals(n2, iterator.next());
		assertFalse(n2.hasChildren());
	}

	@Test
	public void shouldRemoveSingleNode() {
		Node n1 = new NonTerminalNode("n1");
		Node n2 = new IntermediateNode();
		Node n3 = new TerminalNode("n3");
		n1.addChild(n2);
		n2.addChild(n3);
		
		n1.visit(visitor);
		Iterator<Node> iterator = n1.iterator();
		assertEquals(n3, iterator.next());
		assertFalse(n3.hasChildren());
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
		
		n1.visit(visitor);
		Iterator<Node> iterator = n1.iterator();
		assertEquals(n4, iterator.next());
		assertFalse(n4.hasChildren());
		assertEquals(n5, iterator.next());
		assertFalse(n5.hasChildren());
	}

}
