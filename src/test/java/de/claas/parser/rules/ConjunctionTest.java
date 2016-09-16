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
import de.claas.parser.visitors.Parser;

/**
 * 
 * The JUnit test for class {@link Conjunction}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
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
		return new Rule[] { new Terminal("hello"), new Terminal("world") };
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
		assertNull(Parser.parse(processibleState(), rule));
		assertNull(Parser.parse(unprocessibleState(), rule));
	}

	@Test
	public void shouldNotProcessIfAnyChildFailsToProcess() {
		Rule rule = build(defaultChildren());
		assertNull(Parser.parse(buildState("helloinvalid"), rule));
		assertNull(Parser.parse(buildState("invalidworld"), rule));
		assertNull(Parser.parse(buildState("hello"), rule));
		assertNull(Parser.parse(buildState("world"), rule));
	}

	@Test
	public void shouldReturnIntermediateNode() {
		Rule rule = build(defaultChildren());
		Node node = Parser.parse(processibleState(), rule);
		assertEquals(IntermediateNode.class, node.getClass());
	}

	@Test
	public void shouldReturnAppropriateNodeTree() {
		Rule rule = build(defaultChildren());
		Node node = Parser.parse(processibleState(), rule);

		Iterator<Node> children = node.iterator();
		assertEquals("hello", ((TerminalNode) children.next()).getTerminal());
		assertEquals("world", ((TerminalNode) children.next()).getTerminal());
		assertFalse(children.hasNext());
	}

}
