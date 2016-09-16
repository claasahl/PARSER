package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.State;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.visitors.Parser;

/**
 * 
 * The JUnit test for class {@link NonTerminal}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class NonTerminalTest extends DecoratorTest {

	private static final String DEFAULT_NAME = "hello world!";

	@Override
	protected Decorator build(Rule rule) {
		return new NonTerminal(DEFAULT_NAME, rule);
	}

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] { new Terminal("nonTerminal") };
	}

	@Override
	protected State processibleState() {
		return buildState("nonTerminal");
	}

	@Override
	protected State unprocessibleState() {
		return buildState("invlid");
	}

	@Test
	public void shouldReturnNonTerminalNodes() {
		State state = processibleState();
		Rule rule = build(defaultChildren());

		NonTerminalNode node = (NonTerminalNode) Parser.parse(state, rule);
		assertEquals(DEFAULT_NAME, node.getName());
	}

	@Test
	public void shouldReturnNonTerminalNode() {
		Rule rule = build(defaultChildren());
		Node node = Parser.parse(processibleState(), rule);
		assertEquals(NonTerminalNode.class, node.getClass());
	}

	@Test
	public void shouldReturnAppropriateNodeTree() {
		Rule rule = build(defaultChildren());
		NonTerminalNode node = (NonTerminalNode) Parser.parse(processibleState(), rule);

		assertEquals(DEFAULT_NAME, node.getName());
		Iterator<Node> children = node.iterator();
		TerminalNode child = (TerminalNode) children.next();
		assertEquals("nonTerminal", child.getTerminal());
		assertFalse(children.hasNext());
	}

}
