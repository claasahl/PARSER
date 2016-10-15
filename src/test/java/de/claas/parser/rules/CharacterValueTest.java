package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.claas.parser.Rule;
import de.claas.parser.State;

/**
 *
 * The JUnit test for class {@link CharacterValue}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class CharacterValueTest extends TerminalTest {

	private final String DEFAULT_TERMINAL = "hello WORLD";

	@Override
	protected CharacterValue build(Rule... children) {
		return new CharacterValue(this.DEFAULT_TERMINAL);
	}

	@Override
	protected State processibleState() {
		return buildState(this.DEFAULT_TERMINAL);
	}

	@Override
	protected State unprocessibleState() {
		return buildState("invlid token");
	}

	@Test
	public void shouldBeCaseInsensitive() {
		CharacterValue rule = build();
		assertFalse(rule.isCaseSensitive());
	}

	@Test
	public void shouldBeDefaultTerminal() {
		CharacterValue rule = build();
		assertEquals(this.DEFAULT_TERMINAL, rule.getTerminal());
	}

	@Test
	public void shouldBeCaseSensitive() {
		CharacterValue rule = new CharacterValue(true, this.DEFAULT_TERMINAL);
		assertTrue(rule.isCaseSensitive());
	}

}
