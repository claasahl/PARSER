package de.claas.parser.grammars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.GrammarTest;
import de.claas.parser.Node;
import de.claas.parser.exceptions.ParserException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The JUnit test for class {@link Number}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class NumberTest extends GrammarTest<Number> {

	@Override
	protected Number build() {
		return new Number();
	}

	@Test
	public void shouldHandleIntegers() {
		Grammar grammar = build();
		assertEquals(generate(false, "23", null, null, null, null), grammar.parse("23"));
		assertEquals(generate(true, "42", null, null, null, null), grammar.parse("-42"));
	}

	@Test
	public void shouldHandleFractionalNumbers() {
		Grammar grammar = build();
		assertEquals(generate(false, "23", "43", null, null, null), grammar.parse("23.43"));
		assertEquals(generate(true, "42", "111111111111111111111111112", null, null, null),
				grammar.parse("-42.111111111111111111111111112"));
	}

	@Test
	public void shouldHandleExponentialNumbers() {
		Grammar grammar = build();
		assertEquals(generate(false, "23", null, "e", "-", "9"), grammar.parse("23e-9"));
		assertEquals(generate(true, "42", null, "E", "+", "8"), grammar.parse("-42E+8"));
		assertEquals(generate(false, "23", "43", "E", null, "777"), grammar.parse("23.43E777"));
		assertEquals(generate(true, "42", "111111111111111111111111112", "e", "-", "66"),
				grammar.parse("-42.111111111111111111111111112e-66"));
	}

	@Test
	public void shouldHandleZero() {
		Grammar grammar = build();
		assertEquals(generate(false, "0", null, null, null, null), grammar.parse("0"));
	}

	@Test(expected = ParserException.class)
	public void shouldNotHandleZeroAsFirstDigit() {
		Grammar grammar = build();
		grammar.parse("01");
	}

	@Test(expected = ParserException.class)
	public void shouldNotHandlePlusAsFirstDigit() {
		Grammar grammar = build();
		grammar.parse("+1");
	}

	/**
	 * A support function for generating a node that encapsulates a complete
	 * number as represented by the {@link Number}-grammar.
	 * 
	 * @param minus
	 *            whether (or not) a minus sign needs to be prefixed
	 * @param integer
	 *            the number's integer part
	 * @param frac
	 *            the number's fractional part (if any)
	 * @param e
	 *            the 'e'-sign of the number's exponential part (if any)
	 * @param sign
	 *            the sign of the number's exponential part (if any)
	 * @param exp
	 *            the number's exponential part
	 * @return a node that encapsulates a complete number as represented by the
	 *         {@link Number}-grammar
	 */
	protected static Node generate(boolean minus, String integer, String frac, String e, String sign, String exp) {
		Node expected = new NonTerminalNode("number");
		if (minus) {
			expected.addChild(generateMinus());
		}
		if (integer != null) {
			expected.addChild(generateIntegerPart(integer));
		}
		if (frac != null) {
			expected.addChild(generateFractionalPart(frac));
		}
		if (exp != null) {
			expected.addChild(generateExponentPart(e, sign, exp));
		}
		return expected;
	}

	/**
	 * A support function for generating a node that encapsulates a minus sign.
	 * 
	 * @return a node that encapsulates a minus sign
	 */
	private static Node generateMinus() {
		Node tMinus = new TerminalNode("-");
		Node nMinus = new NonTerminalNode("minus");
		nMinus.addChild(tMinus);
		return nMinus;
	}

	/**
	 * A support function for generating a node that encapsulates the
	 * integer-part of a number.
	 * 
	 * @param integer
	 *            the integer-part
	 * @return a node that encapsulates the integer-part of a number
	 */
	private static Node generateIntegerPart(String integer) {
		Node nInteger = new NonTerminalNode("integer");
		if ("0".equals(integer)) {
			Node tZero = new TerminalNode("0");
			Node nZero = new NonTerminalNode("zero");
			nZero.addChild(tZero);
			nInteger.addChild(nZero);
		} else {
			for (int i = 0; i < integer.length(); i++) {
				Node tDigit = new TerminalNode(integer.substring(i, i + 1));
				Node nDigit = new NonTerminalNode(i == 0 ? "digit1-9" : "digit");
				nDigit.addChild(tDigit);
				nInteger.addChild(nDigit);
			}
		}
		return nInteger;
	}

	/**
	 * A support function for generating a node that encapsulates the
	 * fractional-part of a number.
	 * 
	 * @param frac
	 *            the fractional-part
	 * @return a node that encapsulates the fraction-part of a number
	 */
	private static Node generateFractionalPart(String frac) {
		Node nFrac = new NonTerminalNode("frac");

		Node tPoint = new TerminalNode(".");
		Node nPoint = new NonTerminalNode("decimal-point");
		nPoint.addChild(tPoint);
		nFrac.addChild(nPoint);

		for (int i = 0; i < frac.length(); i++) {
			Node tDigit = new TerminalNode(frac.substring(i, i + 1));
			Node nDigit = new NonTerminalNode("digit");
			nDigit.addChild(tDigit);
			nFrac.addChild(nDigit);
		}
		return nFrac;
	}

	/**
	 * A support function for generating a node that encapsulates the
	 * exponent-part of a number.
	 * 
	 * @param e
	 *            the 'e'-sign of the number's exponential part (if any)
	 * @param sign
	 *            the sign of the number's exponential part (if any)
	 * @param exp
	 *            the number's exponential part
	 * @return a node that encapsulates the exponent-part of a number
	 */
	private static Node generateExponentPart(String e, String sign, String exp) {
		Node nExp = new NonTerminalNode("exp");

		Node tE = new TerminalNode(e);
		Node nE = new NonTerminalNode("e");
		nE.addChild(tE);
		nExp.addChild(nE);

		if (sign != null && ("-".equals(sign) || "+".equals(sign))) {
			Node tSign = new TerminalNode("-".equals(sign) ? "-" : "+");
			Node nSign = new NonTerminalNode("-".equals(sign) ? "minus" : "plus");
			nSign.addChild(tSign);
			nExp.addChild(nSign);
		}

		for (int i = 0; i < exp.length(); i++) {
			Node tDigit = new TerminalNode(exp.substring(i, i + 1));
			Node nDigit = new NonTerminalNode("digit");
			nDigit.addChild(tDigit);
			nExp.addChild(nDigit);
		}
		return nExp;
	}

}
