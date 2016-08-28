package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;

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
public class NodeToStringTest extends NodeVisitorTest {

	private NodeVisitor visitor;

	@Before
	public void before() {
		visitor = new NodeToString("  ", "\n");
	}

	@Override
	public void shouldHandleNoNode() {
		assertEquals("", visitor.toString());
	}

	@Override
	public void shouldHandleTerminalNode() {
		new TerminalNode("terminal").visit(visitor);
		assertEquals("TerminalNode:terminal\n", visitor.toString());
	}

	@Override
	public void shouldHandleIntermediateNode() {
		new IntermediateNode().visit(visitor);
		assertEquals("IntermediateNode\n", visitor.toString());
	}

	@Override
	public void shouldHandleNonTerminalNode() {
		new NonTerminalNode("root").visit(visitor);
		assertEquals("NonTerminalNode:root\n", visitor.toString());
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

	@Override
	public void shouldHandleCyclicNonTerminalNode() {
		NonTerminalNode node = new NonTerminalNode("root");
		node.addChild(node);
		node.visit(visitor);

		List<String> lines = new ArrayList<>();
		lines.add("NonTerminalNode:root");
		lines.add("  NonTerminalNode:root");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

	@Override
	public void shouldHandleCyclicIntermediateNode() {
		IntermediateNode node = new IntermediateNode();
		node.addChild(node);
		node.visit(visitor);

		// TODO simplify!
		List<String> lines = new ArrayList<>();
		lines.add("IntermediateNode");
		lines.add("  IntermediateNode");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

}
