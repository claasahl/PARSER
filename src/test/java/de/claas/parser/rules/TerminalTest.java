package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.State;
import de.claas.parser.results.TerminalNode;

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
		return buildState(Arrays.asList(DEFAULT_TERMINALS).stream().collect(Collectors.joining()));
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

	@Test
	public void shouldProcessAnyOfTheTerminals() {
		State state = buildState("worldhelloworld");
		Terminal rule = build("hello", "world", "b");

		assertNotNull(rule.process(state));
		assertEquals("world", state.getProcessedData());
		assertEquals("helloworld", state.getUnprocessedData());

		assertNotNull(rule.process(state));
		assertEquals("worldhello", state.getProcessedData());
		assertEquals("world", state.getUnprocessedData());

		assertNotNull(rule.process(state));
		assertEquals("worldhelloworld", state.getProcessedData());
		assertEquals("", state.getUnprocessedData());
	}

	@Test
	public void shouldNotProcessWithoutTerminals() {
		State state = buildState("world hello world");
		Terminal rule = build(new String[] {});
		assertNull(rule.process(state));
	}

	@Test
	public void shouldProcessRangeBasedTerminals() {
		State state = buildState("abxz");
		Terminal rule = build(DEFAULT_RANGE_START, DEFAULT_RANGE_END);

		assertNotNull(rule.process(state));
		assertEquals("a", state.getProcessedData());
		assertEquals("bxz", state.getUnprocessedData());

		assertNotNull(rule.process(state));
		assertNotNull(rule.process(state));
		assertEquals("abx", state.getProcessedData());
		assertEquals("z", state.getUnprocessedData());

		assertNotNull(rule.process(state));
		assertEquals("abxz", state.getProcessedData());
		assertEquals("", state.getUnprocessedData());
	}

	@Test
	public void shouldNotProcessTerminalsOutsideOfRange() {
		State state = buildState("A 0 Z");
		Terminal rule = build(DEFAULT_RANGE_START, DEFAULT_RANGE_END);
		assertNull(rule.process(state));
	}

	@Test
	public void shouldReturnTerminalNode() {
		Rule rule = build(defaultChildren());
		Node node = rule.process(processibleState());
		assertEquals(TerminalNode.class, node.getClass());
	}

	@Test
	public void shouldReturnAppropriateNodeTree() {
		Rule rule = build(defaultChildren());
		TerminalNode node = (TerminalNode) rule.process(processibleState());
		assertEquals(DEFAULT_TERMINALS[0], node.getTerminal());
	}

}
