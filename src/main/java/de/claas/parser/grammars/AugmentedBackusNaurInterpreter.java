package de.claas.parser.grammars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.exceptions.InterpreterException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.visitors.Interpreter;
import de.claas.parser.visitors.UpdateNonTerminalReferences;

/**
 * The class {@link AugmentedBackusNaurInterpreter}. It is an implementation of
 * the interface {@link Interpreter}. It is intended to interpret
 * {@link Node}-trees that correspond to the {@link AugmentedBackusNaur}
 * grammar.
 *
 * @author Claas Ahlrichs
 */
public class AugmentedBackusNaurInterpreter extends Interpreter<Rule> {

	private static final String BRACKET_LEFT = "[";
	private static final String BRACKET_RIGHT = "]";
	private static final String PARENTHESES_LEFT = "(";
	private static final String PARENTHESES_RIGHT = ")";
	private static final String POINT = ".";
	private static final String DASH = "-";
	private static final String SLASH = "/";
	private static final String PERCENT = "%";
	private static final String CASE_SENSITIVE = "%s";
	private static final String CASE_INSENSITIVE = "%i";
	private final Map<String, NonTerminal> rules = new HashMap<>();

	/**
	 * Constructs a new {@link AugmentedBackusNaurInterpreter} with default
	 * parameters.
	 */
	public AugmentedBackusNaurInterpreter() {
		expectNonTerminalNode("rulelist");
	}

	@Override
	public Function<TerminalNode, Rule> getTerminal(TerminalNode node) {
		// not expected to be called
		return null;
	}

	@Override
	public Function<IntermediateNode, Rule> getIntermediate(IntermediateNode node) {
		// not expected to be called
		return null;
	}

	@Override
	public Function<NonTerminalNode, Rule> getNonTerminal(NonTerminalNode node) {
		switch (node.getName()) {
		case "rulelist":
			return this::visitRulelist;
		case "rule":
			return this::visitRule;
		case "elements":
			return this::visitElements;
		case "alternation":
			return this::visitAlternation;
		case "concatenation":
			return this::visitConcatenation;
		case "repetition":
			return this::visitRepetition;
		case "repeat":
			return this::visitRepeat;
		case "element":
			return this::visitElement;
		case "group":
			return this::visitGroup;
		case "option":
			return this::visitOption;
		case "char-val":
			return this::visitCharVal;
		case "case-insensitive-string":
			return this::visitCaseInsensitiveString;
		case "case-sensitive-string":
			return this::visitCaseSensitiveString;
		case "num-val":
			return this::visitNumVal;
		case "bin-val":
			return this::visitBinVal;
		case "dec-val":
			return this::visitDecVal;
		case "hex-val":
			return this::visitHexVal;
		case "prose-val":
			return this::visitProseVal;
		case "rulename":
		case "defined-as":
		case "c-wsp":
		case "c-nl":
		case "bit":
		case "digit":
		case "hexdig":
			return new Function<NonTerminalNode, Rule>() {
				@Override
				public Rule apply(NonTerminalNode node) {
					return null;
				}
			};
		default:
			return null;
		}
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "rulelist".
	 * 
	 * @param node
	 *            the node
	 * @return the rule(s) that the specified node represents
	 */
	private Rule visitRulelist(NonTerminalNode node) {
		if (!node.hasChildren()) {
			String msg = "At least one rule is required!";
			throw new InterpreterException(msg);
		}

		Rule firstRule = null;
		Iterator<Node> children = node.iterator();
		while (children.hasNext()) {
			Node child = nextChild(true, null, children);
			expectNonTerminalNode("rule");
			if (isExpected(child)) {
				child.visit(this);
				if (firstRule == null) {
					firstRule = getResult();
				}
				continue;
			}

			// skip whitespace
			child = skipWhitespace(child, children);
			expectNonTerminalNode("c-nl");
			child.visit(this);
		}

		// update references to NonTerminals
		if (firstRule != null) {
			firstRule.visit(new UpdateNonTerminalReferences(this.rules.values()));
		}
		return firstRule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "rule".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitRule(NonTerminalNode node) {
		NonTerminal rule = null;
		String ruleName = null;
		boolean alternative = false;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);

		expectNonTerminalNode("rulename");
		if (child != null) {
			child.visit(this);
			ruleName = concatTerminals(child);
			child = nextChild(true, null, children);
		}

		expectNonTerminalNode("defined-as");
		if (child != null) {
			child.visit(this);
			String definedAs = concatTerminals(child);
			alternative = "/=".equalsIgnoreCase(definedAs);
			child = nextChild(true, null, children);
		}

		expectNonTerminalNode("elements");
		if (child != null) {
			child.visit(this);
			if (alternative && this.rules.containsKey(ruleName)) {
				rule = this.rules.get(ruleName);
				rule = new NonTerminal(ruleName, new Disjunction(rule.getRule(), getResult()));
			} else {
				rule = new NonTerminal(ruleName, getResult());
			}
			this.rules.put(ruleName, rule);
			child = nextChild(true, null, children);
		}

		expectNonTerminalNode("c-nl");
		if (child != null) {
			child.visit(this);
		}
		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "elements".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitElements(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);

		expectNonTerminalNode("alternation");
		if (child != null) {
			child.visit(this);
			rule = getResult();
			child = nextChild(true, null, children);
		}

		child = skipWhitespace(child, children);
		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "alternation".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitAlternation(NonTerminalNode node) {
		boolean createdDisjunction = false;
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);

		expectNonTerminalNode("concatenation");
		if (child != null) {
			child.visit(this);
			rule = getResult();
			child = nextChild(true, null, children);
		}

		while (child != null && rule != null) {
			child = skipWhitespace(child, children);
			child = testTerminal(child, children, SLASH, false);

			child = skipWhitespace(child, children);
			expectNonTerminalNode("concatenation");
			if (child != null) {
				child.visit(this);
				if (!createdDisjunction) {
					rule = new Disjunction(rule);
					createdDisjunction = true;
				}
				rule.addChild(getResult());
				child = nextChild(true, null, children);
			}
		}

		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "concatenation".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitConcatenation(NonTerminalNode node) {
		boolean createdConjunction = false;
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);

		expectNonTerminalNode("repetition");
		if (child != null) {
			child.visit(this);
			rule = getResult();
			child = nextChild(true, null, children);
		}

		while (child != null && rule != null) {
			expectNonTerminalNode("c-wsp");
			do {
				child.visit(this);
				child = nextChild(true, null, children);
			} while (child != null && isExpected(child));

			expectNonTerminalNode("repetition");
			if (child != null) {
				child.visit(this);
				if (!createdConjunction) {
					rule = new Conjunction(rule);
					createdConjunction = true;
				}
				rule.addChild(getResult());
				child = nextChild(true, null, children);
			}
		}

		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "repetition".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitRepetition(NonTerminalNode node) {
		Rule rule = null;
		int minRepetitions = 1;
		int maxRepetitions = 1;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);

		expectNonTerminalNode("repeat");
		if (child != null && isExpected(child)) {
			child.visit(this);
			Repetition dummy = (Repetition) getResult();
			minRepetitions = dummy.getMinimumNumberOfRepetions();
			maxRepetitions = dummy.getMaximumNumberOfRepetions();
			child = nextChild(true, null, children);
		}

		expectNonTerminalNode("element");
		if (child != null) {
			child.visit(this);
			if (minRepetitions == 1 && maxRepetitions == 1) {
				rule = getResult();
			} else {
				rule = new Repetition(getResult(), minRepetitions, maxRepetitions);
			}
			child = nextChild(true, null, children);
		}
		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "repeat".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitRepeat(NonTerminalNode node) {
		Rule rule = null;
		String repeat = concatTerminals(node);
		int starIndex = repeat.indexOf('*');
		if (starIndex >= 0) {
			String min = repeat.substring(0, starIndex);
			String max = repeat.substring(starIndex + 1);
			int minRepetitions = min.isEmpty() ? 0 : Integer.valueOf(min).intValue();
			int maxRepetitions = max.isEmpty() ? Integer.MAX_VALUE : Integer.valueOf(max).intValue();
			rule = new Repetition(null, minRepetitions, maxRepetitions);
		} else if (!repeat.isEmpty()) {
			int repetitions = Integer.valueOf(repeat).intValue();
			rule = new Repetition(null, repetitions, repetitions);
		} else {
			throw new InterpreterException("Invalid 'repeat'-rule: " + repeat);
		}
		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "element".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitElement(NonTerminalNode node) {
		Rule rule = null;
		String ruleName = null;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);

		expectNonTerminalNode("rulename");
		if (child != null && isExpected(child)) {
			ruleName = concatTerminals(node);
			if (!this.rules.containsKey(ruleName)) {
				this.rules.put(ruleName, new NonTerminal(ruleName));
			}
			rule = this.rules.get(ruleName);
			child = nextChild(true, null, children);
		}

		expectNonTerminalNode("group", "option", "char-val", "num-val", "prose-val");
		if (child != null && isExpected(child)) {
			child.visit(this);
			rule = getResult();
			child = nextChild(true, null, children);
		}
		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "group".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitGroup(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);
		child = testTerminal(child, children, PARENTHESES_LEFT, false);

		child = skipWhitespace(child, children);
		expectNonTerminalNode("alternation");
		if (child != null) {
			child.visit(this);
			rule = getResult();
			child = nextChild(true, null, children);
		}

		child = skipWhitespace(child, children);
		child = testTerminal(child, children, PARENTHESES_RIGHT, false);

		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "option".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitOption(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);
		child = testTerminal(child, children, BRACKET_LEFT, false);

		child = skipWhitespace(child, children);
		expectNonTerminalNode("alternation");
		if (child != null) {
			child.visit(this);
			rule = new Optional(getResult());
			child = nextChild(true, null, children);
		}

		child = skipWhitespace(child, children);
		child = testTerminal(child, children, BRACKET_RIGHT, false);
		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "char-val".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitCharVal(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);

		expectNonTerminalNode("case-insensitive-string", "case-sensitive-string");
		if (child != null) {
			child.visit(this);
			rule = getResult();
			child = nextChild(true, null, children);
		}
		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "case-insensitive-string".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitCaseInsensitiveString(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);
		child = testTerminal(child, children, CASE_INSENSITIVE, true);

		expectNonTerminalNode("quoted-string");
		if (child != null) {
			String quotedString = concatTerminals(child);
			if (quotedString.startsWith("\"") && quotedString.endsWith("\"")) {
				int length = quotedString.length();
				String quote = quotedString.substring(1, length - 1);
				rule = new CharacterValue(false, quote);
			} else {
				throw new InterpreterException(
						"Expected 'quoted-string' to start and end with double quote, but it did not.");
			}
		}
		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "case-sensitive-string".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitCaseSensitiveString(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);
		child = testTerminal(child, children, CASE_SENSITIVE, false);

		expectNonTerminalNode("quoted-string");
		if (child != null) {
			String quotedString = concatTerminals(child);
			if (quotedString.startsWith("\"") && quotedString.endsWith("\"")) {
				int length = quotedString.length();
				String quote = quotedString.substring(1, length - 1);
				rule = new CharacterValue(true, quote);
			} else {
				throw new InterpreterException(
						"Expected 'quoted-string' to start and end with double quote, but it did not.");
			}
		}
		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "num-val".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitNumVal(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);
		child = testTerminal(child, children, PERCENT, false);

		expectNonTerminalNode("bin-val", "dec-val", "hex-val");
		if (child != null && isExpected(child)) {
			child.visit(this);
			rule = getResult();
			child = nextChild(true, null, children);
		}
		return rule;
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "bin-val".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitBinVal(NonTerminalNode node) {
		return visitNumericVal(node, "b", "bit", 2);
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "dec-val".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitDecVal(NonTerminalNode node) {
		return visitNumericVal(node, "d", "digit", 10);
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "hex-val".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitHexVal(NonTerminalNode node) {
		return visitNumericVal(node, "x", "hexdig", 16);
	}

	/**
	 * Called by this interpreter with the intention of interpreting
	 * {@link NonTerminalNode}-nodes of type "prose-val".
	 * 
	 * @param node
	 *            the node
	 * @return the rule that the specified node represents
	 */
	private Rule visitProseVal(NonTerminalNode node) {
		Rule rule = null;

		String quotedString = concatTerminals(node);
		if (quotedString.startsWith("<") && quotedString.endsWith(">")) {
			int length = quotedString.length();
			String quote = quotedString.substring(1, length - 1);
			rule = new CharacterValue(true, quote);
		} else {
			throw new InterpreterException("Expected 'prose-val' to start with '<' and end with '>', but it did not.");
		}
		return rule;
	}

	/**
	 * A support method that tries to process the specified node as numeric
	 * value of the specified type (i.e. radix, marker, etc.).
	 * 
	 * @param node
	 *            the node
	 * @param expectedMarker
	 *            the expected marker (e.g. "b" for binary values)
	 * @param expectedNonTerminal
	 *            the expected non-terminal (e.g. "bit" for binary values)
	 * @param radix
	 *            the radix of the numeric value (e.g. 2 for binary values)
	 * @return the rule that the specified node represents
	 */
	private Rule visitNumericVal(NonTerminalNode node, String expectedMarker, String expectedNonTerminal, int radix) {
		List<String> terminals = new ArrayList<>();
		int rangeStart = -1;
		int rangeEnd = -1;
		Iterator<Node> children = node.iterator();
		Node child = nextChild(true, null, children);

		child = testTerminal(child, children, expectedMarker, false);

		StringBuilder digits = new StringBuilder();
		expectNonTerminalNode(expectedNonTerminal);
		while (child != null && isExpected(child)) {
			child.visit(this);
			String bit = concatTerminals(child);
			digits.append(bit);
			child = nextChild(true, null, children);
		}
		rangeStart = Integer.parseInt(digits.toString(), radix);
		terminals.add("" + (char) rangeStart);

		expectTerminalNode();
		if (child != null) {
			String marker = concatTerminals(child);
			child = nextChild(true, null, children);
			if (POINT.equals(marker)) {
				rangeStart = -1;
				do {
					digits = new StringBuilder();
					expectNonTerminalNode(expectedNonTerminal);
					while (child != null && isExpected(child)) {
						child.visit(this);
						String bit = concatTerminals(child);
						digits.append(bit);
						child = nextChild(true, null, children);
					}
					terminals.add("" + (char) Integer.parseInt(digits.toString(), radix));

					child = testTerminal(child, children, POINT, false);
				} while (child != null);
			} else if (DASH.equals(marker)) {
				terminals.clear();
				digits = new StringBuilder();
				expectNonTerminalNode(expectedNonTerminal);
				while (child != null && isExpected(child)) {
					child.visit(this);
					String bit = concatTerminals(child);
					digits.append(bit);
					child = nextChild(true, null, children);
				}
				rangeEnd = Integer.parseInt(digits.toString(), radix);
			} else {
				String msg = String.format("Expected either '.' or '-', but got '%s'", marker);
				throw new InterpreterException(msg);
			}
			child = nextChild(true, null, children);
		}

		if (rangeStart >= 0 && rangeEnd >= rangeStart) {
			for (int number = rangeStart; number <= rangeEnd; number++) {
				terminals.add("" + (char) number);
			}
		}
		return CharacterValue.alternatives(true, terminals.toArray(new String[0]));
	}

	/**
	 * A support function that tries to simplify the process of moving to the
	 * next child. Returns the next child if the condition is fulfilled and
	 * there is a next child. Otherwise, the current / most recent child is
	 * returned.
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
	 * A support function that skips any number of "c-wsp"-nodes. Returns the
	 * first child is not a "c-wsp"-node.
	 * 
	 * @param child
	 *            the current / most recent child
	 * @param children
	 *            the "list" of children
	 * @return the first child that is not a "c-wsp"-node
	 */
	private Node skipWhitespace(Node child, Iterator<Node> children) {
		Node localChild = child;
		expectNonTerminalNode("c-wsp");
		while (localChild != null && isExpected(localChild) && children.hasNext()) {
			localChild.visit(this);
			localChild = children.next();
		}
		return localChild;
	}

	/**
	 * A support function that test if the given Node corresponds to an expected
	 * terminal symbol.
	 * 
	 * @param child
	 *            the current / most recent child
	 * @param children
	 *            the "list" of children
	 * @param expected
	 *            the expected terminal symbol
	 * @param optional
	 *            whether the expected terminal symbol is optional
	 * @return the next child
	 */
	private Node testTerminal(Node child, Iterator<Node> children, String expected, boolean optional) {
		Node localChild = child;
		expectTerminalNode();
		if (localChild != null && (!optional || isExpected(localChild))) {
			String actual = concatTerminals(localChild);
			if (!expected.equals(actual)) {
				String msg = String.format("Expected '%s', but got '%s'", expected, actual);
				throw new InterpreterException(msg);
			}
			localChild = nextChild(true, null, children);
		}
		return localChild;
	}

}
