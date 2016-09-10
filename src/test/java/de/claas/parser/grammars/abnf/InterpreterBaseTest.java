package de.claas.parser.grammars.abnf;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.claas.parser.exceptions.CyclicNodeException;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.visitors.NodeVisitorTest;

/**
 * 
 * The JUnit test for class {@link InterpreterBaseTest}. It is intended to
 * collect and document a set of test cases for the tested class. Please refer
 * to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public abstract class InterpreterBaseTest<T extends InterpreterBase> extends NodeVisitorTest {

	/**
	 * Returns an instantiated interpreter. If appropriate, the instance is
	 * configured with default values.
	 *
	 * @return an instantiated interpreter
	 */
	protected abstract T build();

	@Override
	public void shouldHandleNoNode() {
		T visitor = build();
		assertNull(visitor.getRule());
	}

	@Test(expected = InterpretingException.class)
	public void shouldHandleTerminalNode() {
		T visitor = build();
		new TerminalNode("some terminal").visit(visitor);
	}

	@Test(expected = InterpretingException.class)
	public void shouldHandleIntermediateNode() {
		T visitor = build();
		new IntermediateNode().visit(visitor);
	}

	@Test(expected = InterpretingException.class)
	public void shouldHandleNonTerminalNode() {
		T visitor = build();
		new NonTerminalNode(visitor.getExpectedNonTerminal()).visit(visitor);
	}

	@Test
	public void shouldHandleCyclicNonTerminalNode() {
		try {
			T visitor = build();
			NonTerminalNode node = new NonTerminalNode(visitor.getExpectedNonTerminal());
			node.addChild(node);
			node.visit(visitor);
			fail("expected InterpretingException or CyclicNodeException exception");
		} catch (InterpretingException | CyclicNodeException e) {
			// either of these exceptions are expected
		}
	}

	@Test
	public void shouldHandleCyclicIntermediateNode() {
		try {
			T visitor = build();
			IntermediateNode node = new IntermediateNode();
			node.addChild(node);
			node.visit(visitor);
			fail("expected InterpretingException or CyclicNodeException exception");
		} catch (InterpretingException | CyclicNodeException e) {
			// either of these exceptions are expected
		}
	}

	@Test
	public void shouldHaveExpectedNonTerminal() {
		T visitor = build();
		assertNotNull(visitor.getExpectedNonTerminal());
	}

	@Test
	public void shouldBeExpectedNonTerminal() {
		T visitor = build();
		visitor.setExpectedNonTerminal("expected");
		NonTerminalNode node = new NonTerminalNode("expected");
		assertTrue(visitor.isNonTerminalExpected(node));
	}

	@Test
	public void shouldNotBeExpectedNonTerminal() {
		T visitor = build();
		visitor.setExpectedNonTerminal("expected");
		NonTerminalNode node = new NonTerminalNode("unexpected");
		assertFalse(visitor.isNonTerminalExpected(node));
	}

	@Test(expected = NullPointerException.class)
	public void shouldRaiseNullPointerException() {
		T visitor = build();
		visitor.setExpectedNonTerminal("expected");
		NonTerminalNode node = new NonTerminalNode(null);
		assertFalse(visitor.isNonTerminalExpected(node));
	}

}
