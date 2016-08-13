package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.claas.parser.Rule;
import de.claas.parser.State;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Decorator;
import de.claas.parser.rules.Optional;

/**
 * 
 * The JUnit test for class {@link OptionalTest}. It is intended to collect and
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
		return new Rule[] { buildTestRule("decorated", new TerminalNode("decorated")) };
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
		State state = buildState("decorated", "decorated", "invalid");
		Rule rule = build(defaultChildren());
		assertNotNull(rule.process(state));
		assertEquals(1, state.getProcessedTokens());
		assertEquals(2, state.getUnprocessedTokens());

		assertNotNull(rule.process(state));
		assertEquals(2, state.getProcessedTokens());
		assertEquals(1, state.getUnprocessedTokens());

		assertNotNull(rule.process(state));
		assertEquals(2, state.getProcessedTokens());
		assertEquals(1, state.getUnprocessedTokens());
	}

}