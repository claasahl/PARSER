package de.claas.parser.grammars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.builders.NumberBuilder;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.visitors.InterpreterTest;

/**
 * The JUnit test for class {@link NumberInterpreter}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
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
		Node number = new NumberBuilder("42").fraction("5").negative().build();
		number.visit(interpreter);
		assertEquals(new Double(-42.5), interpreter.getResult());
	}

	@Test
	public void shouldBePositveInteger() {
		NumberInterpreter interpreter = build();
		Node number = new NumberBuilder("23").build();
		number.visit(interpreter);
		assertEquals(new Integer(23), interpreter.getResult());
	}

	@Test
	public void shouldBeNegativeInteger() {
		NumberInterpreter interpreter = build();
		Node number = new NumberBuilder("23").negative().build();
		number.visit(interpreter);
		assertEquals(new Integer(-23), interpreter.getResult());
	}

	@Test
	public void shouldBePositveFractionalNumber() {
		NumberInterpreter interpreter = build();
		Node number = new NumberBuilder("12").fraction("01").build();
		number.visit(interpreter);
		assertEquals(new Double(12.01), interpreter.getResult());
	}

	@Test
	public void shouldBeNegativeFractionalNumber() {
		NumberInterpreter interpreter = build();
		Node number = new NumberBuilder("12").fraction("01").negative().build();
		number.visit(interpreter);
		assertEquals(new Double(-12.01), interpreter.getResult());
	}

	@Test
	public void shouldHavePositveExponent() {
		NumberInterpreter interpreter = build();
		Node number = new NumberBuilder("1").exponent("e", "+", "2").build();
		number.visit(interpreter);
		assertEquals(new Double(100), interpreter.getResult());
	}

	@Test
	public void shouldHaveNegativeExponent() {
		NumberInterpreter interpreter = build();
		Node number = new NumberBuilder("1").exponent("e", "-", "2").build();
		number.visit(interpreter);
		assertEquals(new Double(0.01), interpreter.getResult());
	}

}
