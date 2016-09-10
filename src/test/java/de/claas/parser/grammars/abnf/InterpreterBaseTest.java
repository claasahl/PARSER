package de.claas.parser.grammars.abnf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.claas.parser.Node;
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
		new NonTerminalNode("some non-terminal").visit(visitor);
	}

	@Test
	public void shouldHandleCyclicNonTerminalNode() {
		try {
			T visitor = build();
			visitor.expectNonTerminalNode("expected");
			NonTerminalNode node = new NonTerminalNode("expected");
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
			visitor.expectIntermediateNode();
			IntermediateNode node = new IntermediateNode();
			node.addChild(node);
			node.visit(visitor);
			fail("expected InterpretingException or CyclicNodeException exception");
		} catch (InterpretingException | CyclicNodeException e) {
			// either of these exceptions are expected
		}
	}

	@Test
	public void shouldExpectTerminalNode() {
		T visitor = build();
		visitor.expectTerminalNode();
		Node node = new TerminalNode("some terminal");
		assertTrue(visitor.isExpected(node));
	}

	@Test
	public void shouldNotExpectTerminalNode() {
		T visitor = build();
		visitor.expectIntermediateNode();
		Node node = new TerminalNode("some terminal");
		assertFalse(visitor.isExpected(node));
	}

	@Test
	public void shouldExpectIntermediateNode() {
		T visitor = build();
		visitor.expectIntermediateNode();
		Node node = new IntermediateNode();
		assertTrue(visitor.isExpected(node));
	}

	@Test
	public void shouldNotExpectIntermediateNode() {
		T visitor = build();
		visitor.expectNonTerminalNode("expected");
		Node node = new IntermediateNode();
		assertFalse(visitor.isExpected(node));
	}

	@Test
	public void shouldExpectNonTerminalNode() {
		T visitor = build();
		visitor.expectNonTerminalNode("expected");
		NonTerminalNode node = new NonTerminalNode("expected");
		assertTrue(visitor.isExpected(node));
	}

	@Test
	public void shouldNotExpectNonTerminalNode() {
		T visitor = build();
		visitor.expectTerminalNode();
		NonTerminalNode node = new NonTerminalNode("expected");
		assertFalse(visitor.isExpected(node));
	}

}
