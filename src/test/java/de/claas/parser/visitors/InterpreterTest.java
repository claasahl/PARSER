package de.claas.parser.visitors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.exceptions.CyclicNodeException;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 *
 * The JUnit test for class {@link Interpreter}. It is intended to collect and
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
public abstract class InterpreterTest<R> extends NodeVisitorTest {

	private static final String TERMINAL = "some terminal";
	private static final String NON_TERMINAL = "some non-terminal";

	/**
	 * Returns an instantiated {@link Interpreter} class. If appropriate, the
	 * instance is configured with default values.
	 *
	 * @return an instantiated {@link Interpreter} class
	 */
	protected abstract Interpreter<R> build();

	@Override
	public void shouldHandleNoNode() {
		Interpreter<R> interpreter = build();
		assertNull(interpreter.getResult());
	}

	@Override
	@Test(expected = InterpretingException.class)
	public void shouldHandleTerminalNode() {
		Interpreter<R> interpreter = build();
		Node node = new TerminalNode(TERMINAL);
		node.visit(interpreter);
	}

	@Override
	@Test(expected = InterpretingException.class)
	public void shouldHandleIntermediateNode() {
		Interpreter<R> interpreter = build();
		Node node = new IntermediateNode();
		node.visit(interpreter);
	}

	@Override
	@Test(expected = InterpretingException.class)
	public void shouldHandleNonTerminalNode() {
		Interpreter<R> interpreter = build();
		Node node = new NonTerminalNode(NON_TERMINAL);
		node.visit(interpreter);
	}

	@Override
	@Test(expected = CyclicNodeException.class)
	public void shouldHandleCyclicNonTerminalNode() {
		Interpreter<R> interpreter = build();
		NonTerminalNode node = new NonTerminalNode(NON_TERMINAL);
		node.addChild(node);
		node.visit(interpreter);
	}

	@Override
	@Test(expected = InterpretingException.class)
	public void shouldHandleCyclicIntermediateNode() {
		Interpreter<R> interpreter = build();
		IntermediateNode node = new IntermediateNode();
		node.addChild(node);
		node.visit(interpreter);
	}

	@Test
	public void shouldExpectNonTerminalNode() {
		Interpreter<R> interpreter = build();
		interpreter.expectNonTerminalNode(NON_TERMINAL);
		
		assertFalse(interpreter.isExpected(new TerminalNode(TERMINAL)));
		assertFalse(interpreter.isExpected(new IntermediateNode()));
		assertTrue(interpreter.isExpected(new NonTerminalNode(NON_TERMINAL)));
	}
	
	@Test
	public void shouldExpectIntermediateNode() {
		Interpreter<R> interpreter = build();
		interpreter.expectIntermediateNode();
		
		assertFalse(interpreter.isExpected(new TerminalNode(TERMINAL)));
		assertTrue(interpreter.isExpected(new IntermediateNode()));
		assertFalse(interpreter.isExpected(new NonTerminalNode(NON_TERMINAL)));
	}
	
	@Test
	public void shouldExpectTerminalNode() {
		Interpreter<R> interpreter = build();
		interpreter.expectTerminalNode();
		
		assertTrue(interpreter.isExpected(new TerminalNode(TERMINAL)));
		assertFalse(interpreter.isExpected(new IntermediateNode()));
		assertFalse(interpreter.isExpected(new NonTerminalNode(NON_TERMINAL)));
	}
	
	@Test
	public void shouldExpectNothing() {
		Interpreter<R> interpreter = build();
		interpreter.expectNothing();
		
		assertFalse(interpreter.isExpected(new TerminalNode(TERMINAL)));
		assertFalse(interpreter.isExpected(new IntermediateNode()));
		assertFalse(interpreter.isExpected(new NonTerminalNode(NON_TERMINAL)));
	}

}
