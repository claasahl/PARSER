package de.claas.parser.grammars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;
import de.claas.parser.visitors.ConcatenateTerminals;
import de.claas.parser.visitors.Interpreter;
import de.claas.parser.visitors.UpdateNonTerminalReferences;

public class AugmentedBackusNaurInterpreter extends Interpreter<Rule> {

	private final Map<String, NonTerminal> rules = new HashMap<>();

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
			return this::skip;
		default:
			return null;
		}
	}

	private Rule visitRulelist(NonTerminalNode node) {
		if (!node.hasChildren()) {
			String msg = "At least one rule is required!";
			throw new InterpretingException(msg);
		}

		Rule firstRule = null;
		Iterator<Node> children = node.iterator();
		while (children.hasNext()) {
			Node child = children.next();
			expectNonTerminalNode("rule");
			if (isExpected(child)) {
				child.visit(this);
				if (firstRule == null)
					firstRule = getResult();
				continue;
			}

			// skip whitespace
			expectNonTerminalNode("c-wsp");
			while (isExpected(child) && children.hasNext()) {
				child.visit(this);
				child = children.next();
			}
			expectNonTerminalNode("c-nl");
			child.visit(this);
		}

		// update references to NonTerminals
		if (firstRule != null)
			firstRule.visit(new UpdateNonTerminalReferences(this.rules.values()));
		return firstRule;
	}

	private Rule visitRule(NonTerminalNode node) {
		NonTerminal rule = null;
		String ruleName = null;
		boolean alternative = false;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectNonTerminalNode("rulename");
		if (child != null) {
			child.visit(this);
			ruleName = ConcatenateTerminals.concat(child);
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("defined-as");
		if (child != null) {
			child.visit(this);
			String definedAs = ConcatenateTerminals.concat(child);
			alternative = "/=".equalsIgnoreCase(definedAs);
			child = children.hasNext() ? children.next() : null;
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
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("c-nl");
		if (child != null) {
			child.visit(this);
		}
		return rule;
	}

	private Rule visitElements(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectNonTerminalNode("alternation");
		if (child != null) {
			child.visit(this);
			rule = getResult();
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("c-wsp");
		while (child != null && isExpected(child) && children.hasNext()) {
			child.visit(this);
			child = children.next();
		}
		return rule;
	}

	private Rule visitAlternation(NonTerminalNode node) {
		boolean createdDisjunction = false;
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectNonTerminalNode("concatenation");
		if (child != null) {
			child.visit(this);
			rule = getResult();
			child = children.hasNext() ? children.next() : null;
		}

		while (child != null && rule != null) {
			expectNonTerminalNode("c-wsp");
			while (child != null && isExpected(child) && children.hasNext()) {
				child.visit(this);
				child = children.next();
			}

			expectTerminalNode();
			if (child != null) {
				String slash = ConcatenateTerminals.concat(child);
				if (!"/".equals(slash)) {
					String msg = String.format("Expected forward slash '/', but got '%s'", slash);
					throw new InterpretingException(msg);
				}
				child = children.hasNext() ? children.next() : null;
			}

			expectNonTerminalNode("c-wsp");
			while (child != null && isExpected(child) && children.hasNext()) {
				child.visit(this);
				child = children.next();
			}

			expectNonTerminalNode("concatenation");
			if (child != null) {
				child.visit(this);
				if (!createdDisjunction) {
					rule = new Disjunction(rule);
					createdDisjunction = true;
				}
				rule.addChild(getResult());
				child = children.hasNext() ? children.next() : null;
			}
		}

		return rule;
	}

	private Rule visitConcatenation(NonTerminalNode node) {
		boolean createdConjunction = false;
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectNonTerminalNode("repetition");
		if (child != null) {
			child.visit(this);
			rule = getResult();
			child = children.hasNext() ? children.next() : null;
		}

		while (child != null && rule != null) {
			expectNonTerminalNode("c-wsp");
			do {
				child.visit(this);
				child = children.hasNext() ? children.next() : null;
			} while (child != null && isExpected(child));

			expectNonTerminalNode("repetition");
			if (child != null) {
				child.visit(this);
				if (!createdConjunction) {
					rule = new Conjunction(rule);
					createdConjunction = true;
				}
				rule.addChild(getResult());
				child = children.hasNext() ? children.next() : null;
			}

		}

		return rule;
	}

	private Rule visitRepetition(NonTerminalNode node) {
		Rule rule = null;
		int minRepetitions = 1;
		int maxRepetitions = 1;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectNonTerminalNode("repeat");
		if (child != null && isExpected(child)) {
			child.visit(this);
			Repetition dummy = (Repetition) getResult();
			minRepetitions = dummy.getMinimumNumberOfRepetions();
			maxRepetitions = dummy.getMaximumNumberOfRepetions();
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("element");
		if (child != null) {
			child.visit(this);
			if (minRepetitions == 1 && maxRepetitions == 1)
				rule = getResult();
			else
				rule = new Repetition(getResult(), minRepetitions, maxRepetitions);
			child = children.hasNext() ? children.next() : null;
		}
		return rule;
	}

	private Rule visitRepeat(NonTerminalNode node) {
		Rule rule = null;
		String repeat = ConcatenateTerminals.concat(node);
		int starIndex = repeat.indexOf('*');
		if (starIndex >= 0) {
			String min = repeat.substring(0, starIndex);
			String max = repeat.substring(starIndex+1);
			int minRepetitions = !min.isEmpty() ? new Integer(min).intValue() : 0;
			int maxRepetitions = !max.isEmpty() ? new Integer(max).intValue() : Integer.MAX_VALUE;
			rule = new Repetition(null, minRepetitions, maxRepetitions);
		} else if(!repeat.isEmpty()){
			int repetitions = new Integer(repeat).intValue();
			rule = new Repetition(null, repetitions, repetitions);
		} else {
			throw new InterpretingException("Invalid 'repeat'-rule: " + repeat);
		}
		return rule;
	}

	private Rule visitElement(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectNonTerminalNode("rulename");
		if (child != null && isExpected(child)) {
			String ruleName = ConcatenateTerminals.concat(child);
			if (!this.rules.containsKey(ruleName)) {
				this.rules.put(ruleName, new NonTerminal(ruleName));
			}
			rule = this.rules.get(ruleName);
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("group", "option", "char-val", "num-val", "prose-val");
		if (child != null && isExpected(child)) {
			child.visit(this);
			rule = getResult();
			child = children.hasNext() ? children.next() : null;
		}
		return rule;
	}

	private Rule visitGroup(NonTerminalNode node) {
		// "(" *c-wsp alternation *c-wsp ")"
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectTerminalNode();
		if (child != null) {
			String bracket = ConcatenateTerminals.concat(child);
			if (!"(".equals(bracket)) {
				String msg = String.format("Expected opening bracket '(', but got '%s'", bracket);
				throw new InterpretingException(msg);
			}
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("c-wsp");
		while (child != null && isExpected(child) && children.hasNext()) {
			child.visit(this);
			child = children.next();
		}

		expectNonTerminalNode("alternation");
		if (child != null) {
			child.visit(this);
			rule = getResult();
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("c-wsp");
		while (child != null && isExpected(child) && children.hasNext()) {
			child.visit(this);
			child = children.next();
		}

		expectTerminalNode();
		if (child != null) {
			String bracket = ConcatenateTerminals.concat(child);
			if (!")".equals(bracket)) {
				String msg = String.format("Expected closing bracket ')', but got '%s'", bracket);
				throw new InterpretingException(msg);
			}
			child = children.hasNext() ? children.next() : null;
		}

		return rule;
	}

	private Rule visitOption(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectTerminalNode();
		if (child != null) {
			String bracket = ConcatenateTerminals.concat(child);
			if (!"[".equals(bracket)) {
				String msg = String.format("Expected opening bracket '[', but got '%s'", bracket);
				throw new InterpretingException(msg);
			}
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("c-wsp");
		while (child != null && isExpected(child) && children.hasNext()) {
			child.visit(this);
			child = children.next();
		}

		expectNonTerminalNode("alternation");
		if (child != null) {
			child.visit(this);
			rule = new Optional(getResult());
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("c-wsp");
		while (child != null && isExpected(child) && children.hasNext()) {
			child.visit(this);
			child = children.next();
		}

		expectTerminalNode();
		if (child != null) {
			String bracket = ConcatenateTerminals.concat(child);
			if (!"]".equals(bracket)) {
				String msg = String.format("Expected closing bracket ']', but got '%s'", bracket);
				throw new InterpretingException(msg);
			}
			child = children.hasNext() ? children.next() : null;
		}

		return rule;
	}

	private Rule visitCharVal(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectNonTerminalNode("case-insensitive-string", "case-sensitive-string");
		if (child != null) {
			child.visit(this);
			rule = getResult();
			child = children.hasNext() ? children.next() : null;
		}
		return rule;
	}

	private Rule visitCaseInsensitiveString(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectTerminalNode();
		if (child != null && isExpected(child)) {
			String marker = ConcatenateTerminals.concat(child);
			if (!"%i".equals(marker)) {
				String msg = String.format("Expected case insensitivity marker '%i', but got '%s'", marker);
				throw new InterpretingException(msg);
			}
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("quoted-string");
		if (child != null) {
			String quotedString = ConcatenateTerminals.concat(child);
			if (quotedString.startsWith("\"") && quotedString.endsWith("\"")) {
				int length = quotedString.length();
				String quote = quotedString.substring(1, length - 1);
				rule = new Terminal(false, quote);
			} else {
				throw new InterpretingException(
						"Expected 'quoted-string' to start and end with double quote, but it did not.");
			}
		}
		return rule;
	}

	private Rule visitCaseSensitiveString(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectTerminalNode();
		if (child != null) {
			String marker = ConcatenateTerminals.concat(child);
			if (!"%s".equals(marker)) {
				String msg = String.format("Expected case sensitivity marker '%s', but got '%s'", marker);
				throw new InterpretingException(msg);
			}
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("quoted-string");
		if (child != null) {
			String quotedString = ConcatenateTerminals.concat(child);
			if (quotedString.startsWith("\"") && quotedString.endsWith("\"")) {
				int length = quotedString.length();
				String quote = quotedString.substring(1, length - 1);
				rule = new Terminal(true, quote);
			} else {
				throw new InterpretingException(
						"Expected 'quoted-string' to start and end with double quote, but it did not.");
			}
		}
		return rule;
	}

	private Rule visitNumVal(NonTerminalNode node) {
		Rule rule = null;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectTerminalNode();
		if (child != null) {
			String marker = ConcatenateTerminals.concat(child);
			if (!"%".equals(marker)) {
				String msg = String.format("Expected number marker '%%', but got '%s'", marker);
				throw new InterpretingException(msg);
			}
			child = children.hasNext() ? children.next() : null;
		}

		expectNonTerminalNode("bin-val", "dec-val", "hex-val");
		if (child != null && isExpected(child)) {
			child.visit(this);
			rule = getResult();
			child = children.hasNext() ? children.next() : null;
		}
		return rule;
	}

	private Rule visitBinVal(NonTerminalNode node) {
		return visitNumericVal(node, "b", "bit", 2);
	}

	private Rule visitDecVal(NonTerminalNode node) {
		return visitNumericVal(node, "d", "digit", 10);
	}

	private Rule visitHexVal(NonTerminalNode node) {
		return visitNumericVal(node, "x", "hexdig", 16);
	}

	private Rule visitNumericVal(NonTerminalNode node, String expectedMarker, String expectedNonTerminal, int radix) {
		List<String> terminals = new ArrayList<>();
		int rangeStart = -1;
		int rangeEnd = -1;
		Iterator<Node> children = node.iterator();
		Node child = children.hasNext() ? children.next() : null;

		expectTerminalNode();
		if (child != null) {
			String marker = ConcatenateTerminals.concat(child);
			if (!expectedMarker.equals(marker)) {
				String msg = String.format("Expected number marker '%s', but got '%s'", expectedMarker, marker);
				throw new InterpretingException(msg);
			}
			child = children.hasNext() ? children.next() : null;
		}

		StringBuilder digits = new StringBuilder();
		expectNonTerminalNode(expectedNonTerminal);
		while (child != null && isExpected(child)) {
			child.visit(this);
			String bit = ConcatenateTerminals.concat(child);
			digits.append(bit);
			child = children.hasNext() ? children.next() : null;
		}
		rangeStart = Integer.parseInt(digits.toString(), radix);
		terminals.add("" + (char) rangeStart);

		expectTerminalNode();
		if (child != null) {
			String marker = ConcatenateTerminals.concat(child);
			child = children.hasNext() ? children.next() : null;
			if (".".equals(marker)) {
				rangeStart = -1;
				do {
					digits = new StringBuilder();
					expectNonTerminalNode(expectedNonTerminal);
					while (child != null && isExpected(child)) {
						child.visit(this);
						String bit = ConcatenateTerminals.concat(child);
						digits.append(bit);
						child = children.hasNext() ? children.next() : null;
					}
					terminals.add("" + (char) Integer.parseInt(digits.toString(), radix));

					expectTerminalNode();
					if (child != null) {
						String point = ConcatenateTerminals.concat(child);
						if (!".".equals(point)) {
							String msg = String.format("Expected '.', but got '%s'", point);
							throw new InterpretingException(msg);
						}
						child = children.hasNext() ? children.next() : null;
					}
				} while (child != null);
			} else if ("-".equals(marker)) {
				terminals.clear();
				digits = new StringBuilder();
				expectNonTerminalNode(expectedNonTerminal);
				while (child != null && isExpected(child)) {
					child.visit(this);
					String bit = ConcatenateTerminals.concat(child);
					digits.append(bit);
					child = children.hasNext() ? children.next() : null;
				}
				rangeEnd = Integer.parseInt(digits.toString(), radix);
			} else {
				String msg = String.format("Expected either '.' or '-', but got '%s'", marker);
				throw new InterpretingException(msg);
			}
			child = children.hasNext() ? children.next() : null;
		}

		if (rangeStart >= 0 && rangeEnd >= rangeStart) {
			for (int number = rangeStart; number <= rangeEnd; number++)
				terminals.add("" + (char) number);
		}
		return new Terminal(true, terminals.toArray(new String[0]));
	}

	private Rule visitProseVal(NonTerminalNode node) {
		Rule rule = null;

		String quotedString = ConcatenateTerminals.concat(node);
		if (quotedString.startsWith("<") && quotedString.endsWith(">")) {
			int length = quotedString.length();
			String quote = quotedString.substring(1, length - 1);
			rule = new Terminal(true, quote);
		} else {
			throw new InterpretingException("Expected 'prose-val' to start with '<' and end with '>', but it did not.");
		}
		return rule;
	}

	/**
	 * Called by this interpreter with the intention of skipping the next
	 * specified {@link NonTerminalNode}-nodes.
	 * 
	 * @param node
	 *            the node
	 * @return <code>null</code>
	 */
	private Rule skip(NonTerminalNode node) {
		return null;
	}

}
