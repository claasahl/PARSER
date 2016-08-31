package de.claas.parser.grammars;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.GrammarTest;
import de.claas.parser.Node;
import de.claas.parser.exceptions.ParsingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The JUnit test for class {@link NumberTest}. It is intended to collect and
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
		assertEquals(generateTree(false, "23", null, null, null, null), grammar.parse("23"));
		assertEquals(generateTree(true, "42", null, null, null, null), grammar.parse("-42"));
	}

	@Test
	public void shouldHandleFractionalNumbers() {
		Grammar grammar = build();
		assertEquals(generateTree(false, "23", "43", null, null, null), grammar.parse("23.43"));
		assertEquals(generateTree(true, "42", "111111111111111111111111112", null, null, null),
				grammar.parse("-42.111111111111111111111111112"));
	}

	@Test
	public void shouldHandleExponentialNumbers() {
		Grammar grammar = build();
		assertEquals(generateTree(false, "23", null, "e", "-", "9"), grammar.parse("23e-9"));
		assertEquals(generateTree(true, "42", null, "E", "+", "8"), grammar.parse("-42E+8"));
		assertEquals(generateTree(false, "23", "43", "E", null, "777"), grammar.parse("23.43E777"));
		assertEquals(generateTree(true, "42", "111111111111111111111111112", "e", "-", "66"),
				grammar.parse("-42.111111111111111111111111112e-66"));
	}

	@Test
	public void shouldHandleZero() {
		Grammar grammar = build();
		assertEquals(generateTree(false, "0", null, null, null, null), grammar.parse("0"));
	}

	@Test(expected = ParsingException.class)
	public void shouldNotHandleZeroAsFirstDigit() {
		Grammar grammar = build();
		grammar.parse("01");
	}

	@Test(expected = ParsingException.class)
	public void shouldNotHandlePlusAsFirstDigit() {
		Grammar grammar = build();
		grammar.parse("+1");
	}

	/**
	 * Returns a tree of nodes for the specified number.
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
	 * @return
	 */
	private Node generateTree(boolean minus, String integer, String frac, String e, String sign, String exp) {
		Node expected = new NonTerminalNode("number");

		if (minus) {
			Node tMinus = new TerminalNode("-");
			Node nMinus = new NonTerminalNode("minus");
			nMinus.addChild(tMinus);
			expected.addChild(nMinus);
		}

		if (integer != null) {
			Node nInteger = new NonTerminalNode("integer");
			if (integer.equals("0")) {
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
			expected.addChild(nInteger);
		}

		if (frac != null) {
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
			expected.addChild(nFrac);
		}

		if (exp != null) {
			Node nExp = new NonTerminalNode("exp");

			Node tE = new TerminalNode(e);
			Node nE = new NonTerminalNode("e");
			nE.addChild(tE);
			nExp.addChild(nE);

			if (sign != null && (sign.equals("-") || sign.equals("+"))) {
				Node tSign = new TerminalNode(sign.equals("-") ? "-" : "+");
				Node nSign = new NonTerminalNode(sign.equals("-") ? "minus" : "plus");
				nSign.addChild(tSign);
				nExp.addChild(nSign);
			}

			for (int i = 0; i < exp.length(); i++) {
				Node tDigit = new TerminalNode(exp.substring(i, i + 1));
				Node nDigit = new NonTerminalNode("digit");
				nDigit.addChild(tDigit);
				nExp.addChild(nDigit);
			}
			expected.addChild(nExp);
		}
		return expected;
	}

}
