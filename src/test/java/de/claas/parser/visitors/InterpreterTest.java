package de.claas.parser.visitors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.exceptions.CyclicNodeException;
import de.claas.parser.exceptions.InterpreterException;
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

	/**
	 * Returns an instantiated {@link Interpreter} class. If appropriate, the
	 * instance is configured with default values.
	 *
	 * @return an instantiated {@link Interpreter} class
	 */
	protected abstract Interpreter<R> build();

	/**
	 * Returns an instantiated {@link TerminalNode} class. The node is expected
	 * to be initialized with an appropriate terminal symbol for the grammar
	 * being interpreted.
	 * <p>
	 * Most grammars will not need specialized {@link TerminalNode}s. Those that
	 * do can overwrite this method. In all other cases, the default
	 * implementation returns a {@link TerminalNode} with an arbitrary terminal
	 * symbol.
	 * 
	 * @return an instantiated {@link TerminalNode} class
	 */
	@SuppressWarnings("static-method")
	protected TerminalNode getTerminalNode() {
		return new TerminalNode("some terminal");
	}

	/**
	 * Returns an instantiated {@link IntermediateNode} class. The node is
	 * expected to be initialized for the grammar being interpreted.
	 * <p>
	 * Most grammars will not need specialized {@link IntermediateNode}s. Those
	 * that do can overwrite this method. In all other cases, the default
	 * implementation returns a blank {@link IntermediateNode} without any
	 * children.
	 * 
	 * @return an instantiated {@link TerminalNode} class
	 */
	@SuppressWarnings("static-method")
	protected IntermediateNode getIntermediateNode() {
		return new IntermediateNode();
	}

	/**
	 * Returns an instantiated {@link NonTerminalNode} class. The node is
	 * expected to be initialized with an appropriate name for the grammar being
	 * interpreted.
	 * <p>
	 * Most grammars, if not all grammars, will need specialized
	 * {@link NonTerminalNode}s. They are expected to overwrite this method.
	 * Nonetheless, the default implementation returns a blank
	 * {@link NonTerminalNode} with an arbitrary name and no children.
	 * 
	 * @return an instantiated {@link NonTerminalNode} class
	 */
	@SuppressWarnings("static-method")
	protected NonTerminalNode getNonTerminalNode() {
		return new NonTerminalNode("some non-terminal");
	}

	@Override
	public void shouldHandleNoNode() {
		Interpreter<R> interpreter = build();
		assertNull(interpreter.getResult());
	}

	@Override
	@Test(expected = InterpreterException.class)
	public void shouldHandleTerminalNode() {
		Interpreter<R> interpreter = build();
		Node node = getTerminalNode();
		node.visit(interpreter);
	}

	@Override
	@Test(expected = InterpreterException.class)
	public void shouldHandleIntermediateNode() {
		Interpreter<R> interpreter = build();
		Node node = getIntermediateNode();
		node.visit(interpreter);
	}

	@Override
	@Test(expected = InterpreterException.class)
	public void shouldHandleNonTerminalNode() {
		Interpreter<R> interpreter = build();
		Node node = getNonTerminalNode();
		node.visit(interpreter);
	}

	@Override
	@Test(expected = CyclicNodeException.class)
	public void shouldHandleCyclicNonTerminalNode() {
		Interpreter<R> interpreter = build();
		Node node = getNonTerminalNode();
		node.addChild(node);
		node.visit(interpreter);
	}

	@Override
	@Test(expected = InterpreterException.class)
	public void shouldHandleCyclicIntermediateNode() {
		Interpreter<R> interpreter = build();
		Node node = new IntermediateNode();
		node.addChild(node);
		node.visit(interpreter);
	}

	@Test
	public void shouldExpectNonTerminalNode() {
		Interpreter<R> interpreter = build();
		interpreter.expectNonTerminalNode("some non-terminal");

		assertFalse(interpreter.isExpected(getTerminalNode()));
		assertFalse(interpreter.isExpected(getIntermediateNode()));
		assertTrue(interpreter.isExpected(new NonTerminalNode("some non-terminal")));
	}

	@Test
	public void shouldExpectIntermediateNode() {
		Interpreter<R> interpreter = build();
		interpreter.expectIntermediateNode();

		assertFalse(interpreter.isExpected(getTerminalNode()));
		assertTrue(interpreter.isExpected(getIntermediateNode()));
		assertFalse(interpreter.isExpected(getNonTerminalNode()));
	}

	@Test
	public void shouldExpectTerminalNode() {
		Interpreter<R> interpreter = build();
		interpreter.expectTerminalNode();

		assertTrue(interpreter.isExpected(getTerminalNode()));
		assertFalse(interpreter.isExpected(getIntermediateNode()));
		assertFalse(interpreter.isExpected(getNonTerminalNode()));
	}

	@Test
	public void shouldExpectNothing() {
		Interpreter<R> interpreter = build();
		interpreter.expectNothing();

		assertFalse(interpreter.isExpected(getTerminalNode()));
		assertFalse(interpreter.isExpected(getIntermediateNode()));
		assertFalse(interpreter.isExpected(getNonTerminalNode()));
	}

}
