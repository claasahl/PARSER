package de.claas.parser.grammars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.grammars.NumberInterpreter;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.visitors.InterpreterTest;

/**
 *
 * The JUnit test for class {@link NumberInterpreter}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class NumberInterpreterTest extends InterpreterTest<java.lang.Number> {

	@Override
	protected NumberInterpreter build() {
		return new NumberInterpreter();
	}
	
	@Override
	protected NonTerminalNode getNonTerminalNode() {
		return new NonTerminalNode("number");
	}

	@Override
	public void shouldHandleNodes() {
		NumberInterpreter interpreter = build();
		Node number = NumberTest.generateTree(true, "42", "5", null, null, null);
		number.visit(interpreter);
		assertEquals(new Double(-42.5), interpreter.getResult());
	}

	@Test
	public void shouldBePositveInteger() {
		NumberInterpreter interpreter = build();
		Node number = NumberTest.generateTree(false, "23", null, null, null, null);
		number.visit(interpreter);
		assertEquals(new Integer(23), interpreter.getResult());
	}

	@Test
	public void shouldBeNegativeInteger() {
		NumberInterpreter interpreter = build();
		Node number = NumberTest.generateTree(true, "23", null, null, null, null);
		number.visit(interpreter);
		assertEquals(new Integer(-23), interpreter.getResult());
	}

	@Test
	public void shouldBePositveFractionalNumber() {
		NumberInterpreter interpreter = build();
		Node number = NumberTest.generateTree(false, "12", "01", null, null, null);
		number.visit(interpreter);
		assertEquals(new Double(12.01), interpreter.getResult());
	}

	@Test
	public void shouldBeNegativeFractionalNumber() {
		NumberInterpreter interpreter = build();
		Node number = NumberTest.generateTree(true, "12", "01", null, null, null);
		number.visit(interpreter);
		assertEquals(new Double(-12.01), interpreter.getResult());
	}

	@Test
	public void shouldHavePositveExponent() {
		NumberInterpreter interpreter = build();
		Node number = NumberTest.generateTree(false, "1", null, "e", "+", "2");
		number.visit(interpreter);
		assertEquals(new Double(100), interpreter.getResult());
	}

	@Test
	public void shouldHaveNegativeExponent() {
		NumberInterpreter interpreter = build();
		Node number = NumberTest.generateTree(false, "1", null, "e", "-", "2");
		number.visit(interpreter);
		assertEquals(new Double(0.01), interpreter.getResult());
	}

}
