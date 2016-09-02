package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
 * The JUnit test for class {@link ConjunctionTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class ConjunctionTest extends RuleTest {

	@Override
	protected Rule build(Rule... children) {
		return new Conjunction(children);
	}

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] { buildTestRule("hello", new TerminalNode("hello")),
				buildTestRule("world", new TerminalNode("world")) };
	}

	@Override
	protected State processibleState() {
		return buildState("helloworld");
	}

	@Override
	protected State unprocessibleState() {
		return buildState("hello");
	}

	@Test
	public void shouldNotProcessWithoutChildren() {
		Rule rule = build();
		assertNull(rule.process(processibleState()));
		assertNull(rule.process(unprocessibleState()));
	}

	@Test
	public void shouldNotProcessIfAnyChildFailsToProcess() {
		Rule rule = build(defaultChildren());
		assertNull(rule.process(buildState("helloinvalid")));
		assertNull(rule.process(buildState("invalidworld")));
		assertNull(rule.process(buildState("hello")));
		assertNull(rule.process(buildState("world")));
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
		assertEquals("hello", ((TerminalNode) children.next()).getTerminal());
		assertEquals("world", ((TerminalNode) children.next()).getTerminal());
		assertFalse(children.hasNext());
	}

}
