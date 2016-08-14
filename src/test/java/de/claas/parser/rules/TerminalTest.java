package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import de.claas.parser.Rule;
import de.claas.parser.State;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The JUnit test for class {@link TerminalTest}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class TerminalTest extends RuleTest {

	private static final String[] DEFAULT_TERMINALS = { "a", "b" };
	private static final char DEFAULT_RANGE_START = 'a';
	private static final char DEFAULT_RANGE_END = 'z';

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
		return buildState(DEFAULT_TERMINALS);
	}

	@Override
	protected State unprocessibleState() {
		return buildState("invlid", "token");
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

	/**
	 * Returns an {@link Terminal} class that was instantiated with the given
	 * parameters.
	 * 
	 * @param rangeStart
	 *            first character that this rule represents (inclusive)
	 * @param rangeEnd
	 *            last character that this rule represents (inclusive)
	 * @return an instantiated {@link Terminal} class
	 */
	protected Terminal build(char rangeStart, char rangeEnd) {
		return new Terminal(rangeStart, rangeEnd);
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

	@Test
	public void shouldProcessAnyOfTheTerminals() {
		State state = buildState("world", "hello", "world");
		Terminal rule = build("hello", "world", "b");

		TerminalNode node = (TerminalNode) rule.process(state);
		assertNotNull(node);
		assertEquals("world", node.getTerminal());

		node = (TerminalNode) rule.process(state);
		assertNotNull(node);
		assertEquals("hello", node.getTerminal());

		node = (TerminalNode) rule.process(state);
		assertNotNull(node);
		assertEquals("world", node.getTerminal());
	}

	@Test
	public void shouldNotProcessWithoutTerminals() {
		State state = buildState("world", "hello", "world");
		Terminal rule = build(new String[] {});
		assertNull(rule.process(state));
	}

	@Test
	public void shouldProcessRangeBasedTerminals() {
		State state = buildState("a", "b", "x", "z");
		Terminal rule = build(DEFAULT_RANGE_START, DEFAULT_RANGE_END);
		
		TerminalNode node = (TerminalNode) rule.process(state);
		assertNotNull(node);
		assertEquals("a", node.getTerminal());
		
		node = (TerminalNode) rule.process(state);
		assertNotNull(node);
		assertEquals("b", node.getTerminal());
		
		node = (TerminalNode) rule.process(state);
		assertNotNull(node);
		assertEquals("x", node.getTerminal());
		
		node = (TerminalNode) rule.process(state);
		assertNotNull(node);
		assertEquals("z", node.getTerminal());
	}
	
	@Test
	public void shouldNotProcessTerminalsOutsideOfRange() {
		State state = buildState("A", "0", "Z");
		Terminal rule = build(DEFAULT_RANGE_START, DEFAULT_RANGE_END);
		
		TerminalNode node = (TerminalNode) rule.process(state);
		assertNull(node);
	}

}
