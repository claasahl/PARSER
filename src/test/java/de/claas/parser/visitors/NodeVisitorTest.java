package de.claas.parser.visitors;

import org.junit.Test;

/**
 * 
 * The JUnit test for class {@link NodeVisitorTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public abstract class NodeVisitorTest {

	@Test
	public abstract void shouldHandleNoNode();

	@Test
	public abstract void shouldHandleTerminalNode();

	@Test
	public abstract void shouldHandleIntermediateNode();

	@Test
	public abstract void shouldHandleNonTerminalNode();

	@Test
	public abstract void shouldHandleNodes();
	
	@Test
	public abstract void shouldHandleCyclicNonTerminalNode();
	
	@Test
	public abstract void shouldHandleCyclicIntermediateNode();

}
