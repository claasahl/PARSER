package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.claas.parser.Rule;
import de.claas.parser.State;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The JUnit test for class {@link NonTerminalTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
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
		return new Rule[] { buildTestRule("nonTerminal", new TerminalNode("nonTerminal")) };
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

		NonTerminalNode node = (NonTerminalNode) rule.process(state);
		assertEquals(DEFAULT_NAME, node.getName());
	}

}
