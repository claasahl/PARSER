package de.claas.parser.builders;

import de.claas.parser.Node;
import de.claas.parser.grammars.Number;
import de.claas.parser.grammars.NumberInterpreter;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * The class {@link NumberBuilder}. It is a support class for building
 * node-structures that encapsulate a number as understood by {@link Number} and
 * {@link NumberInterpreter}.
 * <p>
 * By default, the encapsulated number is positive, does not have a
 * fractional-part and does not have an exponent-part.
 *
 * @author Claas Ahlrichs
 */
public class NumberBuilder {

	private final String integer;
	private boolean minus;
	private String fraction;
	private String e;
	private String sign;
	private String exp;

	/**
	 * Constructs a new {@link NumberBuilder} with the specified parameters.
	 * 
	 * @param integer
	 *            the number's integer part
	 */
	public NumberBuilder(String integer) {
		this.integer = integer;
	}

	/**
	 * Turns this builder's number into a negative number.
	 * 
	 * @return this builder
	 */
	public NumberBuilder negative() {
		this.minus = true;
		return this;
	}

	/**
	 * Updates the fractional-part of this builder's number.
	 * 
	 * @param fraction
	 *            the number's fractional part (if any)
	 * @return this builder
	 */
	public NumberBuilder fraction(String fraction) {
		this.fraction = fraction;
		return this;
	}

	/**
	 * Updates the exponent-part of this builder's number.
	 * 
	 * @param e
	 *            the 'e'-character of the number's exponential part (if any)
	 * @param sign
	 *            the sign of the number's exponential part (if any)
	 * @param exp
	 *            the number's exponential part
	 * @return this builder
	 */
	public NumberBuilder exponent(String e, String sign, String exp) {
		this.e = e;
		this.sign = sign;
		this.exp = exp;
		return this;
	}

	/**
	 * Builds the node-structure that represent this builder's number.
	 * 
	 * @return the node-structure that represent this builder's number.
	 */
	public Node build() {
		Node expected = new NonTerminalNode("number");
		if (this.minus) {
			expected.addChild(generateMinus());
		}
		if (this.integer != null) {
			expected.addChild(generateIntegerPart(this.integer));
		}
		if (this.fraction != null) {
			expected.addChild(generateFractionalPart(this.fraction));
		}
		if (this.exp != null) {
			expected.addChild(generateExponentPart(this.e, this.sign, this.exp));
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
