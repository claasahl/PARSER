package de.claas.parser.results;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.NodeTest;

/**
 * 
 * The JUnit test for class {@link TerminalNodeTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class TerminalNodeTest extends NodeTest {

	private static final String DEFAULT_TERMINAL = "terminal";

	@Override
	protected Node build(Node... children) {
		return new TerminalNode(DEFAULT_TERMINAL);
	}

	@Override
	public void shouldHaveChildren() {
		// terminal nodes do not have children!
		shouldNotAddChildren();
	}

	@Override
	public void shouldHaveNonEmptyIterator() {
		// terminal nodes do not have children!
		shouldNotAddChildren();
	}
	
	@Override
	public void shouldManageChildren() {
		// terminal nodes do not have children!
		shouldNotAddChildren();
	}

	@Test
	public void shouldNotAddChildren() {
		Node node = build();
		Node child = build();
		assertFalse(node.addChild(child));
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
