package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The JUnit test for class {@link NodeToStringTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class NodeToStringTest {

	private NodeVisitor visitor;

	@Before
	public void before() {
		visitor = new NodeToString("  ", "\n");
	}

	@Test
	public void shouldHandleNoNode() {
		assertEquals("", visitor.toString());
	}

	@Test
	public void shouldHandleTerminalNode() {
		new TerminalNode("some terminal").visit(visitor);
		assertEquals("TerminalNode:some terminal\n", visitor.toString());
	}

	@Test
	public void shouldHandleIntermediateNode() {
		new IntermediateNode().visit(visitor);
		assertEquals("IntermediateNode\n", visitor.toString());
	}

	@Test
	public void shouldHandleNonTerminalNode() {
		new NonTerminalNode("some non-terminal").visit(visitor);
		assertEquals("NonTerminalNode:some non-terminal\n", visitor.toString());
	}

	@Test
	public void shouldHandleNodes() {
		Node t1 = new TerminalNode("t1");
		Node t2 = new TerminalNode("t2");
		Node t3 = new TerminalNode("t3");
		Node i1 = new IntermediateNode();
		i1.addChild(t1);
		i1.addChild(t2);
		Node i2 = new IntermediateNode();
		i2.addChild(t3);
		Node n1 = new NonTerminalNode("root");
		n1.addChild(i1);
		n1.addChild(i2);
		n1.visit(visitor);

		List<String> lines = new ArrayList<>();
		lines.add("NonTerminalNode:root");
		lines.add("  IntermediateNode");
		lines.add("    TerminalNode:t1");
		lines.add("    TerminalNode:t2");
		lines.add("  IntermediateNode");
		lines.add("    TerminalNode:t3");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

	@Test
	public void shouldHandleCyclicNonTerminalNode() {
		Node n1 = new NonTerminalNode("root");
		n1.addChild(n1);
		n1.visit(visitor);

		List<String> lines = new ArrayList<>();
		lines.add("NonTerminalNode:root");
		lines.add("  NonTerminalNode:root");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}
	
	@Test
	public void shouldHandleCyclicIntermediateNode() {
		Node n1 = new IntermediateNode();
		n1.addChild(n1);
		n1.visit(visitor);

		List<String> lines = new ArrayList<>();
		lines.add("IntermediateNode");
		lines.add("  IntermediateNode");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

}
