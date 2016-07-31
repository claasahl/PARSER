package de.claas.parser;

import static org.junit.Assert.*;

import java.util.Stack;

import org.junit.Test;

/**
 * 
 * The JUnit test for class {@link ParserStateTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class ParserStateTest {

	/**
	 * Returns an instantiated {@link State} class with the specified
	 * tokens. The first token, that is past into this function, will also be
	 * the first token to be processed. Respectively, the last token will be
	 * processed last.
	 * 
	 * @param tokens
	 *            the (unprocessed) tokens
	 * @return an instantiated {@link State} class with the specified
	 *         tokens
	 */
	protected State buildState(String... tokens) {
		Stack<String> tmp = new Stack<>();
		for (int i = tokens.length - 1; i >= 0; i--) {
			tmp.push(tokens[i]);
		}
		return new State(tmp);
	}

	@Test
	public void shouldProperlyInitializeEmptyState() {
		State state = buildState();
		assertEquals(0, state.getProcessedTokens());
		assertEquals(0, state.getUnprocessedTokens());
		assertEquals(0, state.getGroups());
	}

	@Test
	public void shouldProperlyInitializeNonEmptyState() {
		State state = buildState("hello", "world");
		assertEquals(0, state.getProcessedTokens());
		assertEquals(2, state.getUnprocessedTokens());
		assertEquals(0, state.getGroups());
	}

	@Test
	public void shouldReturnNull() {
		State state = buildState();
		assertNull(state.processToken());

		state = buildState("hello", "world");
		assertNotNull(state.processToken());
		assertNotNull(state.processToken());
		assertNull(state.processToken());
	}

	@Test
	public void shouldProcessAndUnprocessTokens() {
		State state = buildState("hello", "world");
		assertEquals("hello", state.processToken());
		assertEquals("world", state.processToken());
		assertEquals(2, state.getProcessedTokens());
		assertEquals(0, state.getUnprocessedTokens());
		assertEquals(0, state.getGroups());

		assertEquals("world", state.unprocessToken());
		assertEquals("hello", state.unprocessToken());
		assertEquals(0, state.getProcessedTokens());
		assertEquals(2, state.getUnprocessedTokens());
		assertEquals(0, state.getGroups());
	}

	@Test
	public void shouldBeginAndEndGroups() {
		State state = buildState();
		assertEquals(0, state.getGroups());
		state.beginGroup();
		state.beginGroup();
		state.beginGroup();
		assertEquals(3, state.getGroups());
		state.endGroup();
		state.endGroup();
		assertEquals(1, state.getGroups());
		state.beginGroup();
		state.endGroup();
		state.endGroup();
		assertEquals(0, state.getGroups());
	}

	@Test
	public void shouldRevertAndUnprocess() {
		State state = buildState("a", "b", "c1", "c2", "c3");
		state.beginGroup();
		assertEquals("a", state.processToken());
		assertEquals("b", state.processToken());
		state.beginGroup();
		assertEquals("c1", state.processToken());
		assertEquals("c2", state.processToken());
		assertEquals("c3", state.processToken());
		assertEquals(5, state.getProcessedTokens());
		assertEquals(0, state.getUnprocessedTokens());
		assertEquals(2, state.getGroups());

		assertEquals("c3", state.unprocessToken());
		assertEquals(4, state.getProcessedTokens());
		assertEquals(1, state.getUnprocessedTokens());
		assertEquals(2, state.getGroups());

		state.revert();
		assertEquals(2, state.getProcessedTokens());
		assertEquals(3, state.getUnprocessedTokens());
		assertEquals(2, state.getGroups());

		state.revert();
		assertEquals(2, state.getProcessedTokens());
		assertEquals(3, state.getUnprocessedTokens());
		assertEquals(2, state.getGroups());

		state.endGroup();
		assertEquals(2, state.getProcessedTokens());
		assertEquals(3, state.getUnprocessedTokens());
		assertEquals(1, state.getGroups());

		state.revert();
		state.endGroup();
		assertEquals(0, state.getProcessedTokens());
		assertEquals(5, state.getUnprocessedTokens());
		assertEquals(0, state.getGroups());
	}
}
