package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
 * The JUnit test for class {@link Optional}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class OptionalTest extends DecoratorTest {

	@Override
	protected Decorator build(Rule rule) {
		return new Optional(rule);
	}

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] { new Terminal("decorated") };
	}

	@Override
	protected State processibleState() {
		return buildState("decorated");
	}

	@Override
	protected State unprocessibleState() {
		return null;
	}

	@Test
	public void shouldProcessAtMostOneToken() {
		State state = buildState("decorateddecoratedinvalid");
		Rule rule = build(defaultChildren());
		assertNotNull(Parser.parse(state, rule));
		assertEquals("decorated", state.getProcessedData());
		assertEquals("decoratedinvalid", state.getUnprocessedData());

		assertNotNull(Parser.parse(state, rule));
		assertEquals("decorateddecorated", state.getProcessedData());
		assertEquals("invalid", state.getUnprocessedData());

		assertNotNull(Parser.parse(state, rule));
		assertEquals("decorateddecorated", state.getProcessedData());
		assertEquals("invalid", state.getUnprocessedData());
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
		TerminalNode child = (TerminalNode) children.next();
		assertEquals("decorated", child.getTerminal());
		assertFalse(children.hasNext());
	}

}
