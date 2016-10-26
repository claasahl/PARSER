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
			return new Function<NonTerminalNode, java.lang.Number>() {
				@Override
				public java.lang.Number apply(NonTerminalNode t) {
					return null;
				}
			};
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
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);

		java.lang.Number sign = handleSign(child);
		child = nextChild(sign != null, child, children);
		if (sign == null) {
			sign = new Integer(1);
		}

		java.lang.Number integer = handleInteger(child);
		child = nextChild(integer != null, child, children);

		java.lang.Number fraction = handleFraction(child);
		child = nextChild(fraction != null, child, children);

		java.lang.Number exponent = handleExponent(child);

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
	 * A support method that tries to process the specified node as minus sign.
	 * Returns the number negative one if the specified node can interpreted as
	 * minus sign. Otherwise <code>null</code> is returned.
	 * 
	 * @param child
	 *            the node
	 * @return the number negative one if the specified node can interpreted as
	 *         minus sign, otherwise <code>null</code>
	 */
	private java.lang.Number handleSign(Node child) {
		java.lang.Number sign = null;
		expectNonTerminalNode("minus");
		if (child != null && isExpected(child)) {
			child.visit(this);
			sign = getResult();
		}
		return sign;
	}

	/**
	 * A support method that tries to process the specified node as integer-part
	 * of a number. Returns the corresponding number if the specified node can
	 * be interpreted as integer-part. Otherwise <code>null</code> is returned.
	 * 
	 * @param child
	 *            the node
	 * @return the corresponding number if the specified node can interpreted as
	 *         integer-part, otherwise <code>null</code>
	 */
	private java.lang.Number handleInteger(Node child) {
		java.lang.Number integer = null;
		expectNonTerminalNode("integer");
		if (child != null) {
			child.visit(this);
			integer = getResult();
		}
		return integer;
	}

	/**
	 * A support method that tries to process the specified node as
	 * fractional-part of a number. Returns the fractional-part as integer if
	 * the specified node can be interpreted as fractional-part. Otherwise
	 * <code>null</code> is returned.
	 * 
	 * @param child
	 *            the node
	 * @return the fractional-part as integer if the specified node can
	 *         interpreted as fractional-part, otherwise <code>null</code>
	 */
	private java.lang.Number handleFraction(Node child) {
		java.lang.Number fraction = null;
		expectNonTerminalNode("frac");
		if (child != null && isExpected(child)) {
			child.visit(this);
			fraction = getResult();
		}
		return fraction;
	}

	/**
	 * A support method that tries to process the specified node as
	 * exponent-part of a number. Returns the exponent-part as integer if the
	 * specified node can be interpreted as exponent-part. Otherwise
	 * <code>null</code> is returned.
	 * 
	 * @param child
	 *            the node
	 * @return the exponent-part as integer if the specified node can
	 *         interpreted as exponent-part, otherwise <code>null</code>
	 */
	private java.lang.Number handleExponent(Node child) {
		java.lang.Number exponent = null;
		expectNonTerminalNode("exp");
		if (child != null && isExpected(child)) {
			child.visit(this);
			exponent = getResult();
		}
		return exponent;
	}

	/**
	 * A support method that tries to simplify the process of moving to the next
	 * child. Returns the next child if the condition is fulfilled and there is
	 * a next child. Otherwise, the current / most recent child is returned.
	 * 
	 * @param condition
	 *            whether the next child should be attempted to be retrieved
	 * @param child
	 *            the current / most recent child
	 * @param children
	 *            the "list" of children
	 * @return the next child if the condition is fulfilled and there is a next
	 *         child, otherwise the current / most recent child
	 */
	private static Node nextChild(boolean condition, Node child, Iterator<Node> children) {
		return condition && children.hasNext() ? children.next() : child;
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

}
