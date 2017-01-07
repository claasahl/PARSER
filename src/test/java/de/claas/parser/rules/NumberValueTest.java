package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.claas.parser.Rule;
import de.claas.parser.State;

/**
 * The JUnit test for class {@link NumberValue}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 */
public class NumberValueTest extends TerminalTest {

	private final int DEFAULT_RADIX = 16;
	private final String DEFAULT_TERMINAL = "hello WORLD";

	@Override
	protected NumberValue build(Rule... children) {
		return new NumberValue(this.DEFAULT_RADIX, this.DEFAULT_TERMINAL.toCharArray());
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
	public void shouldBeDefaultRadix() {
		NumberValue rule = build();
		assertEquals(this.DEFAULT_RADIX, rule.getRadix());
	}

	@Test
	public void shouldBeDefaultTerminal() {
		NumberValue rule = build();
		assertEquals(this.DEFAULT_TERMINAL, rule.getTerminal());
		assertNull(rule.getRangeStart());
		assertNull(rule.getRangeEnd());
	}

	@Test
	public void shouldBeRangeBasedTerminal() {
		NumberValue rule = new NumberValue(this.DEFAULT_RADIX, '0', '9');
		assertNull(rule.getTerminal());
		assertEquals(new Character('0'), rule.getRangeStart());
		assertEquals(new Character('9'), rule.getRangeEnd());
	}

}
