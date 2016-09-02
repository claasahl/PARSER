package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Iterator;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.State;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The JUnit test for class {@link DisjunctionTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class DisjunctionTest extends RuleTest {

	@Override
	protected Rule build(Rule... children) {
		return new Disjunction(children);
	}

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] { buildTestRule("hello", new TerminalNode("hello")),
				buildTestRule("world", new TerminalNode("world")) };
	}

	@Override
	protected State processibleState() {
		return buildState("world");
	}

	@Override
	protected State unprocessibleState() {
		return buildState("invalid");
	}

	@Test
	public void shouldNotProcessWithoutChildren() {
		Rule rule = build();
		assertNull(rule.process(processibleState()));
		assertNull(rule.process(unprocessibleState()));
	}

	@Test
	public void shouldProcessIfAnyChildProcesses() {
		Rule rule = build(defaultChildren());
		assertNotNull(rule.process(buildState("hello")));
		assertNotNull(rule.process(buildState("world")));
	}
	
	@Test
	public void shouldReturnIntermediateNode() {
		Rule rule = build(defaultChildren());
		Node node = rule.process(processibleState());
		assertEquals(IntermediateNode.class, node.getClass());
	}
	
	@Test
	public void shouldReturnAppropriateNodeTree() {
		Rule rule = build(defaultChildren());
		Node node = rule.process(processibleState());
		
		Iterator<Node> children = node.iterator();
		TerminalNode child = (TerminalNode) children.next();
		assertEquals("world", child.getTerminal());
		assertFalse(children.hasNext());
	}

}
