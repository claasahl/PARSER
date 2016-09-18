package de.claas.parser.grammars.number;

import java.util.Iterator;
import java.util.function.Function;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.visitors.ConcatenateTerminals;
import de.claas.parser.visitors.Interpreter;

/**
 * 
 * The class {@link NumberInterpreter}. It is an implementation of the interface
 * {@link Interpreter}. It is intended to ...
 *
 * @author Claas Ahlrichs
 *
 */
public class NumberInterpreter extends Interpreter<java.lang.Number> {

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
			throw new InterpretingException("NonTerminal with name 'integer' is required.");
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

	private java.lang.Number visitSign(NonTerminalNode node) {
		String sign = ConcatenateTerminals.concat(node);
		switch (sign) {
		case "+":
			return new Integer(1);
		case "-":
			return new Integer(-1);
		default:
			return null;
		}
	}

	private java.lang.Number visitFraction(NonTerminalNode node) {
		if (!node.hasChildren())
			return null;

		String digits = ConcatenateTerminals.concat(node);
		return new Double("0" + digits);
	}

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

	private java.lang.Number visitDigits(NonTerminalNode node) {
		String digits = ConcatenateTerminals.concat(node);
		return new Integer(digits);
	}

	private java.lang.Number skip(NonTerminalNode node) {
		return null;
	}

}
