package de.claas.parser.visitors;

import org.junit.Test;

import de.claas.parser.NodeVisitor;

/**
 * 
 * The JUnit test for class {@link NodeVisitor}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 * <p>
 * The tested class is abstract or an interface. Consequently, this JUnit test
 * provides a set of test cases that apply to all concrete implementations of
 * the tested class.
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
