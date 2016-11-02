package de.claas.parser.builders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.grammars.AugmentedBackusNaur;
import de.claas.parser.grammars.AugmentedBackusNaurInterpreter;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.NumberValue;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;

/**
 * 
 * The class {@link AugmentedBackusNaurBuilder}. It is a support class for
 * building node-structures that encapsulate an ABNF-grammar as understood by
 * {@link AugmentedBackusNaur} and {@link AugmentedBackusNaurInterpreter}.
 * <p>
 * By default, the encapsulated ABNF-grammar is empty (i.e. it does not have any
 * rules).
 *
 * @author Claas Ahlrichs
 *
 */
public class AugmentedBackusNaurBuilder {

	private final List<NonTerminal> rules = new ArrayList<>();

	/**
	 * Constructs a new {@link AugmentedBackusNaurBuilder} with default
	 * parameters.
	 */
	public AugmentedBackusNaurBuilder() {
	}

	/**
	 * Adds the specified rule to this builder's ABNF-grammar.
	 * 
	 * @param rule
	 *            the rule
	 * @return this builder
	 */
	public AugmentedBackusNaurBuilder rule(NonTerminal rule) {
		this.rules.add(rule);
		return this;
	}

	/**
	 * Builds the node-structure that represent this builder's ABNF-grammar.
	 * 
	 * @return the node-structure that represent this builder's ABNF-grammar.
	 */
	public Node build() {
		Set<String> visitedRules = new HashSet<>();
		Node rulelist = new NonTerminalNode("rulelist");
		for (NonTerminal actualRule : this.rules) {
			String name = actualRule.getName();
			boolean isIncrementalAlternative = !visitedRules.add(name);

			Node rulename = new NonTerminalNode("rulename");
			append(rulename, "alpha", name);

			Node definedAs = new NonTerminalNode("defined-as");
			Node wsp = new NonTerminalNode("wsp");
			append(wsp, " ");
			Node cwsp = new NonTerminalNode("c-wsp");
			append(cwsp, wsp);
			append(definedAs, cwsp);
			if (isIncrementalAlternative)
				append(definedAs, "=/");
			else
				append(definedAs, "=");
			append(definedAs, cwsp);

			Node elements = new NonTerminalNode("elements");
			append(elements, generateAlternation(actualRule.getRule()));

			Node CRLF = new NonTerminalNode("crlf");
			append(CRLF, "\r\n");
			Node NL = new NonTerminalNode("c-nl");
			if (actualRule.getComment() != null) {
				String actualComment = actualRule.getComment();
				Node comment = new NonTerminalNode("comment");
				append(comment, ";");
				append(comment, wsp);
				appendChildren(comment, (terminal) -> {
					String childName = terminal.trim().isEmpty() ? "wsp" : "vchar";
					Node alpha = new NonTerminalNode(childName);
					append(alpha, terminal);
					return alpha;
				}, actualComment);
				append(comment, CRLF);
				append(NL, comment);
			} else {
				append(NL, CRLF);
			}

			Node rule = new NonTerminalNode("rule");
			append(rule, rulename);
			append(rule, definedAs);
			append(rule, elements);
			append(rule, NL);
			append(rulelist, rule);
		}
		return rulelist;
	}

	private static Node generateAlternation(Rule actualRule) {
		Node alternation = new NonTerminalNode("alternation");
		if (actualRule.getClass().isAssignableFrom(Disjunction.class)) {
			boolean firstChild = true;
			for (Rule child : actualRule) {
				if (!firstChild) {
					appendDelimiter(alternation, "/");
				}
				append(alternation, generateConcatenation(child));
				firstChild = false;
			}
		} else {
			append(alternation, generateConcatenation(actualRule));
		}
		return alternation;
	}

	private static Node generateConcatenation(Rule actualRule) {
		Node concatenation = new NonTerminalNode("concatenation");
		if (actualRule.getClass().isAssignableFrom(Conjunction.class)) {
			boolean firstChild = true;
			for (Rule child : actualRule) {
				if (!firstChild) {
					appendDelimiter(concatenation, null);
				}
				append(concatenation, generateRepetition(child));
				firstChild = false;
			}
		} else {
			append(concatenation, generateRepetition(actualRule));
		}
		return concatenation;
	}

	private static Node generateRepetition(Rule actualRule) {
		Node repetition = new NonTerminalNode("repetition");
		if (actualRule.getClass().isAssignableFrom(Repetition.class)) {
			Repetition rule = (Repetition) actualRule;

			Node repeat = new NonTerminalNode("repeat");
			int min = rule.getMinimumNumberOfRepetions();
			int max = rule.getMaximumNumberOfRepetions();
			if (min == max) {
				String number = Integer.toString(min);
				append(repeat, "digit", number);
			} else {
				if (min != 0) {
					String number = Integer.toString(min);
					append(repeat, "digit", number);
				}
				append(repeat, "*");
				if (max != Integer.MAX_VALUE) {
					String number = Integer.toString(max);
					append(repeat, "digit", number);
				}
			}
			append(repetition, repeat);
			append(repetition, generateElement(rule.getRule()));
		} else {
			append(repetition, generateElement(actualRule));
		}
		return repetition;
	}

	private static Node generateElement(Rule actualRule) {
		Node element = new NonTerminalNode("element");
		if (actualRule.getClass().isAssignableFrom(NonTerminal.class)) {
			NonTerminal rule = (NonTerminal) actualRule;
			Node rulename = new NonTerminalNode("rulename");
			append(rulename, "alpha", rule.getName());
			append(element, rulename);
		} else if (actualRule.getClass().isAssignableFrom(CharacterValue.class)) {
			CharacterValue rule = (CharacterValue) actualRule;
			String terminal = rule.getTerminal();

			Node charVal = new NonTerminalNode("char-val");
			String name = rule.isCaseSensitive() ? "case-sensitive-string" : "case-insensitive-string";
			Node caseString = new NonTerminalNode(name);
			append(charVal, caseString);
			if (rule.isCaseSensitive())
				append(caseString, "%s");
			Node quotedString = new NonTerminalNode("quoted-string");
			append(caseString, quotedString);

			Node dQuote = new NonTerminalNode("dQuote");
			append(dQuote, "\"");
			append(quotedString, dQuote);
			appendChildren(quotedString, terminal);
			append(quotedString, dQuote);
			append(element, charVal);
		} else if (actualRule.getClass().isAssignableFrom(NumberValue.class)) {
			NumberValue rule = (NumberValue) actualRule;
			int radix = rule.getRadix();
			String name = nameForRadix(radix);
			String marker = markerForRadix(radix);
			String numType = typeForRadix(radix);

			Node value = new NonTerminalNode(name);
			append(value, marker);
			if (rule.getTerminal() != null) {
				String terminal = rule.getTerminal();
				for (int index = 0; index < terminal.length(); index++) {
					String number = Integer.toString(terminal.charAt(index), radix);
					append(value, numType, number);
					if (index + 1 < terminal.length())
						append(value, ".");
				}
			} else {
				String start = Integer.toString(rule.getRangeStart().charValue(), radix);
				String end = Integer.toString(rule.getRangeEnd().charValue(), radix);

				append(value, numType, start);
				append(value, "-");
				append(value, numType, end);
			}

			Node numVal = new NonTerminalNode("num-val");
			append(numVal, "%");
			append(numVal, value);
			append(element, numVal);
		} else if (actualRule.getClass().isAssignableFrom(Optional.class)) {
			Optional rule = (Optional) actualRule;
			Node child = generateAlternation(rule.getRule());
			appendChildren(element, "option", "[", child, "]");
		} else {
			Node child = generateAlternation(actualRule);
			appendChildren(element, "group", "(", child, ")");
		}
		return element;
	}

	private static String typeForRadix(int radix) {
		String numType = "";
		numType = radix == 16 ? "hexdig" : numType;
		numType = radix == 10 ? "digit" : numType;
		numType = radix == 2 ? "bit" : numType;
		return numType;
	}

	private static String markerForRadix(int radix) {
		String marker = "";
		marker = radix == 16 ? "x" : marker;
		marker = radix == 10 ? "d" : marker;
		marker = radix == 2 ? "b" : marker;
		return marker;
	}

	private static String nameForRadix(int radix) {
		String name = "";
		name = radix == 16 ? "hex-val" : name;
		name = radix == 10 ? "dec-val" : name;
		name = radix == 2 ? "bin-val" : name;
		return name;
	}

	private static void append(Node parent, String childName, String childContent) {
		appendChildren(parent, (terminal) -> {
			Node alpha = new NonTerminalNode(childName);
			append(alpha, terminal);
			return alpha;
		}, childContent);
	}

	private static void appendChildren(Node parent, String childContent) {
		appendChildren(parent, (terminal) -> new TerminalNode(terminal), childContent);
	}

	private static void appendChildren(Node parent, Function<String, Node> mapper, String childContent) {
		for (int index = 0; index < childContent.length(); index++) {
			String terminal = childContent.substring(index, index + 1);
			append(parent, mapper.apply(terminal));
		}
	}

	private static void appendDelimiter(Node parent, String delimiter) {
		Node wsp = new NonTerminalNode("wsp");
		append(wsp, " ");

		Node cwsp = new NonTerminalNode("c-wsp");
		append(cwsp, wsp);

		append(parent, cwsp);
		if (delimiter != null) {
			append(parent, delimiter);
			append(parent, cwsp);
		}
	}

	private static void appendChildren(Node parent, String childName, String prefix, Node child, String suffix) {
		Node intermediate = new NonTerminalNode(childName);
		append(intermediate, prefix);
		append(intermediate, child);
		append(intermediate, suffix);
		append(parent, intermediate);
	}

	private static void append(Node parent, String terminal) {
		parent.addChild(new TerminalNode(terminal));
	}

	private static void append(Node parent, Node child) {
		parent.addChild(child);
	}
}
