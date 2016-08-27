package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;

import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;

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
		buildTerminalNode("terminal").visit(visitor);
		assertEquals("TerminalNode:terminal\n", visitor.toString());
	}

	@Override
	public void shouldHandleIntermediateNode() {
		buildIntermediateNode(false).visit(visitor);
		assertEquals("IntermediateNode\n", visitor.toString());
	}

	@Override
	public void shouldHandleNonTerminalNode() {
		buildNonTerminalNode(false, "root").visit(visitor);
		assertEquals("NonTerminalNode:root\n", visitor.toString());
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
		Node n1 = buildNonTerminalNode(false, "root");
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
		buildNonTerminalNode(true, "root").visit(visitor);

		List<String> lines = new ArrayList<>();
		lines.add("NonTerminalNode:root");
		lines.add("  NonTerminalNode:root");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}
	
	@Override
	public void shouldHandleCyclicIntermediateNode() {
		buildIntermediateNode(true).visit(visitor);

		// TODO simplify!
		List<String> lines = new ArrayList<>();
		lines.add("IntermediateNode");
		lines.add("  IntermediateNode");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

}
