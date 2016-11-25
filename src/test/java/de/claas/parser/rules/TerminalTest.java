package de.claas.parser.rules;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import de.claas.parser.Rule;
import de.claas.parser.RuleTest;

/**
 * The JUnit test for class {@link Terminal}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 * <p>
 * The tested class is abstract or an interface. Consequently, this JUnit test
 * provides a set of test cases that apply to all concrete implementations of
 * the tested class.
 *
 * @author Claas Ahlrichs
 */
public abstract class TerminalTest extends RuleTest {

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] {};
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
		Rule rule = build(new Rule[] {});
		Rule child = build(new Rule[] {});
		assertFalse(rule.addChild(child));
	}

}
