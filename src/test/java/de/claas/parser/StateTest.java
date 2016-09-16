package de.claas.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * The JUnit test for class {@link State}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class StateTest {

	/**
	 * Returns an instantiated {@link State} class with the specified pattern.
	 * 
	 * @param pattern
	 *            the pattern
	 * @return an instantiated {@link State} class with the specified pattern
	 */
	protected State buildState(String pattern) {
		return new State(pattern);
	}

	@Test
	public void shouldProperlyInitializeEmptyState() {
		State state = buildState("");
		assertEquals("", state.getProcessedData());
		assertEquals("", state.getUnprocessedData());
		assertEquals(0, state.getGroups());
	}

	@Test
	public void shouldProperlyInitializeNonEmptyState() {
		State state = buildState("hello world");
		assertEquals("", state.getProcessedData());
		assertEquals("hello world", state.getUnprocessedData());
		assertEquals(0, state.getGroups());
	}

	@Test
	public void shouldRevertNothing() {
		State state = buildState("hello world");
		assertTrue(state.process("hello"));
		assertTrue(state.process(" "));
		assertEquals("hello ", state.getProcessedData());
		assertEquals("world", state.getUnprocessedData());
		state.beginGroup();
		state.revert();
		assertEquals("hello ", state.getProcessedData());
		assertEquals("world", state.getUnprocessedData());
	}

	@Test
	public void shouldRevertLastGroup() {
		State state = buildState("hello world");
		state.beginGroup();
		assertTrue(state.process("hello"));
		state.beginGroup();
		assertTrue(state.process(" "));
		assertTrue(state.process("world"));
		assertEquals("hello world", state.getProcessedData());
		assertEquals("", state.getUnprocessedData());
		state.revert();
		assertEquals("hello", state.getProcessedData());
		assertEquals(" world", state.getUnprocessedData());
	}

	@Test
	public void shouldRevertEverything() {
		State state = buildState("hello world");
		state.beginGroup();
		assertTrue(state.process("hello"));
		assertTrue(state.process(" "));
		assertEquals("hello ", state.getProcessedData());
		assertEquals("world", state.getUnprocessedData());
		state.revert();
		assertEquals("", state.getProcessedData());
		assertEquals("hello world", state.getUnprocessedData());
	}
}
