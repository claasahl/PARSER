package de.claas.parser.results;

import static org.junit.Assert.*;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.NodeTest;

/**
 * 
 * The JUnit test for class {@link NonTerminalNodeTest}. It is intended to
 * collect and document a set of test cases for the tested class. Please refer
 * to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class NonTerminalNodeTest extends NodeTest {

	private static final String DEFAULT_NAME = "default-name";

	@Override
	protected Node build(Node... children) {
		NonTerminalNode node = new NonTerminalNode(DEFAULT_NAME);
		for (Node child : children) {
			node.addChild(child);
		}
		return node;
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
