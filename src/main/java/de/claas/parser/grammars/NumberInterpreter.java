package de.claas.parser.grammars;

import java.util.Iterator;
import java.util.function.Function;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpreterException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.visitors.Interpreter;

/**
 * 
 * The class {@link NumberInterpreter}. It is an implementation of the interface
 * {@link Interpreter}. It is intended to interpret {@link Node}-trees that
 * correspond to the {@link Number} grammar.
 *
 * @author Claas Ahlrichs
 *
 */
public class NumberInterpreter extends Interpreter<java.lang.Number> {

	/**
	 * Constructs a new {@link NumberInterpreter} with default parameters.
	 */
	public NumberInterpreter() {
		expectNonTerminalNode("number");
	}

	@Override
	public Function<TerminalNode, java.lang.Number> getTerminal(TerminalNode node) {
		// not expected to be called
		return null;
	}

	@Override
	public Function<IntermediateNode, java.lang.Number> getIntermediate(IntermediateNode node) {
		// not expected to be called
		return null;
	}

	@Override
	public Function<NonTerminalNode, java.lang.Number> getNonTerminal(NonTerminalNode node) {
		switch (node.getName()) {
		case "number":
			return this::visitNumber;
		case "minus":
		case "plus":
			return this::visitSign;
		case "frac":
			return this::visitFraction;
		case "exp":
			return this::visitExponent;
		case "integer":
		case "digit":
			return this::visitDigits;
		case "e":
			return this::skip;
		default:
			return null;
		}
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "number".
	 * 
	 * @param node
	 *            the node
	 * @return the number that the specified node represents
	 */
	private java.lang.Number visitNumber(NonTerminalNode node) {
		java.lang.Number sign = new Integer(1);
		java.lang.Number integer = null;
		java.lang.Number exponent = null;
		java.lang.Number fraction = null;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectNonTerminalNode("minus");
		if (child != null && isExpected(child)) {
			child.visit(this);
			sign = getResult();
			child = children.next();
		}

		expectNonTerminalNode("integer");
		if (child != null) {
			child.visit(this);
			integer = getResult();
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("frac");
		if (child != null && isExpected(child)) {
			child.visit(this);
			fraction = getResult();
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("exp");
		if (child != null && isExpected(child)) {
			child.visit(this);
			exponent = getResult();
		}

		if (integer == null)
			throw new InterpreterException("NonTerminal with name 'integer' is required.");
		else if (fraction != null && exponent != null)
			return new Double(
					(integer.doubleValue() + fraction.doubleValue()) * exponent.doubleValue() * sign.doubleValue());
		else if (fraction != null && exponent == null)
			return new Double((integer.doubleValue() + fraction.doubleValue()) * sign.doubleValue());
		else if (fraction == null && exponent != null)
			return new Double(integer.doubleValue() * exponent.doubleValue() * sign.doubleValue());
		else
			return new Integer(integer.intValue() * sign.intValue());
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "plus" or "minus".
	 * 
	 * @param node
	 *            the node
	 * @return the sign of a number (i.e. plus is represented by <code>+1</code>
	 *         and minus is represented by <code>-1</code>)
	 */
	private java.lang.Number visitSign(NonTerminalNode node) {
		String sign = concatTerminals(node);
		switch (sign) {
		case "+":
			return new Integer(1);
		case "-":
			return new Integer(-1);
		default:
			return null;
		}
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "frac".
	 * 
	 * @param node
	 *            the node
	 * @return the fractional part of a number
	 */
	private java.lang.Number visitFraction(NonTerminalNode node) {
		if (!node.hasChildren())
			return null;

		String digits = concatTerminals(node);
		return new Double("0" + digits);
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "exp".
	 * 
	 * @param node
	 *            the node
	 * @return the exponential part of a number
	 */
	private java.lang.Number visitExponent(NonTerminalNode node) {
		if (!node.hasChildren())
			return null;

		Iterator<Node> children = node.iterator();
		expectNonTerminalNode("e");
		children.next().visit(this);

		expectNonTerminalNode("minus", "plus");
		children.next().visit(this);
		java.lang.Number sign = getResult();

		java.lang.Number number = new Integer(0);
		while (children.hasNext()) {
			expectNonTerminalNode("digit");
			children.next().visit(this);
			java.lang.Number digit = getResult();
			number = new Integer(number.intValue() * 10 + digit.intValue());
		}

		return new Double(Math.pow(10.0, new Integer(number.intValue() * sign.intValue()).doubleValue()));
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "integer" or "digit".
	 * 
	 * @param node
	 *            the node
	 * @return the integer part of a number
	 */
	private java.lang.Number visitDigits(NonTerminalNode node) {
		String digits = concatTerminals(node);
		return new Integer(digits);
	}

	/**
	 * Called by this interpreter with the intention of skipping the next
	 * specified {@link NonTerminalNode}-nodes.
	 * 
	 * @param node
	 *            the node
	 * @return <code>null</code>
	 */
	private java.lang.Number skip(NonTerminalNode node) {
		return null;
	}

}
