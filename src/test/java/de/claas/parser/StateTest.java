package de.claas.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * The JUnit test for class {@link StateTest}. It is intended to collect and
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
		State state = buildState("helloworld");
		assertEquals("", state.getProcessedData());
		assertEquals("helloworld", state.getUnprocessedData());
		assertEquals(0, state.getGroups());
	}
}
