package de.claas.parser.visitors;

import org.junit.Test;

/**
 * 
 * The JUnit test for class {@link RuleVisitorTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public abstract class RuleVisitorTest {

	@Test
	public abstract void shouldHandleNoRule();

	@Test
	public abstract void shouldHandleConjunctionRule();

	@Test
	public abstract void shouldHandleDisjunctionRule();

	@Test
	public abstract void shouldHandleNonTerminalRule();

	@Test
	public abstract void shouldHandleOptionalRule();

	@Test
	public abstract void shouldHandleRepetitionRule();

	@Test
	public abstract void shouldHandleTerminalRule();

	@Test
	public abstract void shouldHandleRules();
	
	@Test
	public abstract void shouldHandleCyclicRepetitionRule();
	
	@Test
	public abstract void shouldHandleCyclicOptionalRule();
	
	@Test
	public abstract void shouldHandleCyclicNonTerminalRule();
	
	@Test
	public abstract void shouldHandleCyclicDisjunctionRule();
	
	@Test
	public abstract void shouldHandleCyclicConjunctionRule();

}
