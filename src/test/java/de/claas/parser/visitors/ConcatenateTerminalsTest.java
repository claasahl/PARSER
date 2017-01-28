package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.exceptions.CyclicNodeException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * The JUnit test for class {@link ConcatenateTerminals}. It is intended to
 * collect and document a set of test cases for the tested class. Please refer
 * to the individual tests for more detailed information.
 * <p>
 * The tested class is abstract or an interface. Consequently, this JUnit test
 * provides a set of test cases that apply to all concrete implementations of
 * the tested class.
 *
 * @author Claas Ahlrichs
 */
public class ConcatenateTerminalsTest extends NodeVisitorTest {

	private static final String HELLO = "hello";
	private static final String WORLD = "world";
	private NodeVisitor visitor;

	@Before
	public void before() {
		this.visitor = new ConcatenateTerminals();
	}

	@Override
	public void shouldHandleNoNode() {
		assertEquals("", this.visitor.toString());
	}

	@Override
	public void shouldHandleTerminalNode() {
		Node node = new TerminalNode(HELLO);
		node.visit(this.visitor);
		assertEquals(HELLO, this.visitor.toString());
	}

	@Override
	public void shouldHandleIntermediateNode() {
		Node node = new IntermediateNode();
		node.addChild(new TerminalNode(HELLO));
		node.addChild(new TerminalNode(WORLD));
		node.visit(this.visitor);
		assertEquals(HELLO + WORLD, this.visitor.toString());
	}

	@Override
	public void shouldHandleNonTerminalNode() {
		Node node = new NonTerminalNode("some name");
		node.addChild(new TerminalNode(WORLD));
		node.addChild(new TerminalNode(HELLO));
		node.visit(this.visitor);
		assertEquals(WORLD + HELLO, this.visitor.toString());
	}

	@Override
	public void shouldHandleNodes() {
		Node t1 = new TerminalNode("Hello ");
		Node t2 = new TerminalNode("World");
		Node t3 = new TerminalNode("!");
		Node i1 = new IntermediateNode();
		i1.addChild(t1);
		i1.addChild(t2);
		Node i2 = new IntermediateNode();
		i2.addChild(t3);
		Node n1 = new NonTerminalNode("root");
		n1.addChild(i1);
		n1.addChild(i2);
		n1.visit(this.visitor);
		assertEquals("Hello World!", this.visitor.toString());
	}

	@Override
	@Test(expected = CyclicNodeException.class)
	public void shouldHandleCyclicNonTerminalNode() {
		Node node = new NonTerminalNode("root");
		node.addChild(node);
		node.visit(this.visitor);
	}

	@Override
	@Test(expected = CyclicNodeException.class)
	public void shouldHandleCyclicIntermediateNode() {
		Node node = new IntermediateNode();
		node.addChild(node);
		node.visit(this.visitor);
	}

}
