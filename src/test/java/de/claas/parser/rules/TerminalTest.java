package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

import de.claas.parser.Rule;
import de.claas.parser.RuleTest;
import de.claas.parser.State;

/**
 * 
 * The JUnit test for class {@link Terminal}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class TerminalTest extends RuleTest {

	private static final String[] DEFAULT_TERMINALS = { "a", "b" };

	@Override
	protected Rule build(Rule... children) {
		return build(DEFAULT_TERMINALS);
	}

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] {};
	}

	@Override
	protected State processibleState() {
		return buildState(String.join("", Arrays.asList(DEFAULT_TERMINALS)));
	}

	@Override
	protected State unprocessibleState() {
		return buildState("invlid token");
	}

	/**
	 * Returns an {@link Terminal} class that was instantiated with the given
	 * parameters.
	 * 
	 * @param terminals
	 *            the terminal symbols
	 *
	 * @return an instantiated {@link Terminal} class
	 */
	protected Terminal build(String... terminals) {
		return new Terminal(terminals);
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

	@Test
	public void shouldNotHaveTerminals() {
		Terminal rule = build(new String[] {});
		assertFalse(rule.getTerminals().hasNext());
	}

	@Test
	public void shouldHaveTerminals() {
		Terminal rule = build("hello", "world");
		Iterator<String> iterator = rule.getTerminals();
		assertTrue(iterator.hasNext());
		assertEquals("hello", iterator.next());
		assertEquals("world", iterator.next());
		assertFalse(iterator.hasNext());
	}

}
