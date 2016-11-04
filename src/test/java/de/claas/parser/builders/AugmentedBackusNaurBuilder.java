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
			boolean incremental = !visitedRules.add(name);

			Node elements = new NonTerminalNode("elements");
			append(elements, generateAlternation(actualRule.getRule()));

			Node newLine = new NonTerminalNode("c-nl");
			append(newLine, generateNewLine(actualRule.getComment()));

			Node rule = new NonTerminalNode("rule");
			append(rule, generate(actualRule));
			append(rule, generateDefinedAs(incremental));
			append(rule, elements);
			append(rule, newLine);
			append(rulelist, rule);
		}
		return rulelist;
	}

	/**
	 * A support function for generating a node that encapsulates an alternation
	 * within an ABNF-grammar.
	 * 
	 * @param rule
	 *            the rule that might represent an alternation
	 * @return a node that encapsulates an alternation within an ABNF-grammar
	 */
	private static Node generateAlternation(Rule rule) {
		Node alternation = new NonTerminalNode("alternation");
		if (rule.getClass().isAssignableFrom(Disjunction.class)) {
			boolean firstChild = true;
			for (Rule child : rule) {
				if (!firstChild) {
					appendDelimiter(alternation, "/");
				}
				append(alternation, generateConcatenation(child));
				firstChild = false;
			}
		} else {
			append(alternation, generateConcatenation(rule));
		}
		return alternation;
	}

	/**
	 * A support function for generating a node that encapsulates a
	 * concatenation within an ABNF-grammar.
	 * 
	 * @param rule
	 *            the rule that might represent a concatenation
	 * @return a node that encapsulates a concatenation within an ABNF-grammar
	 */
	private static Node generateConcatenation(Rule rule) {
		Node concatenation = new NonTerminalNode("concatenation");
		if (rule.getClass().isAssignableFrom(Conjunction.class)) {
			boolean firstChild = true;
			for (Rule child : rule) {
				if (!firstChild) {
					appendDelimiter(concatenation, null);
				}
				append(concatenation, generateRepetition(child));
				firstChild = false;
			}
		} else {
			append(concatenation, generateRepetition(rule));
		}
		return concatenation;
	}

	/**
	 * A support function for generating a node that encapsulates a repetition
	 * within an ABNF-grammar.
	 * 
	 * @param rule
	 *            the rule that might represent a repetition
	 * @return a node that encapsulates a repetition within an ABNF-grammar
	 */
	private static Node generateRepetition(Rule rule) {
		Node repetition = new NonTerminalNode("repetition");
		if (rule.getClass().isAssignableFrom(Repetition.class)) {
			Repetition actuakRule = (Repetition) rule;

			Node repeat = new NonTerminalNode("repeat");
			int min = actuakRule.getMinimumNumberOfRepetions();
			int max = actuakRule.getMaximumNumberOfRepetions();
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
			append(repetition, generateElement(actuakRule.getRule()));
		} else {
			append(repetition, generateElement(rule));
		}
		return repetition;
	}

	/**
	 * A support function for generating a node that encapsulates an element
	 * within an ABNF-grammar.
	 * 
	 * @param rule
	 *            the rule that might represent an element
	 * @return a node that encapsulates an element within an ABNF-grammar
	 */
	private static Node generateElement(Rule rule) {
		Node element = new NonTerminalNode("element");
		if (rule.getClass().isAssignableFrom(NonTerminal.class)) {
			NonTerminal actualRule = (NonTerminal) rule;
			append(element, generate(actualRule));
		} else if (rule.getClass().isAssignableFrom(CharacterValue.class)) {
			CharacterValue actualRule = (CharacterValue) rule;
			append(element, generate(actualRule));
		} else if (rule.getClass().isAssignableFrom(NumberValue.class)) {
			NumberValue actualRule = (NumberValue) rule;
			append(element, generate(actualRule));
		} else if (rule.getClass().isAssignableFrom(Optional.class)) {
			Optional actualRule = (Optional) rule;
			append(element, generate(actualRule));
		} else {
			Node intermediate = new NonTerminalNode("group");
			append(intermediate, "(");
			append(intermediate, generateAlternation(rule));
			append(intermediate, ")");
			append(element, intermediate);
		}
		return element;
	}

	/**
	 * A support function for determining the ABNF number type for the specified
	 * radix.
	 * 
	 * @param radix
	 *            the radix
	 * @return the ABNF number type for the specified radix
	 */
	private static String typeForRadix(int radix) {
		String numType = "";
		numType = radix == 16 ? "hexdig" : numType;
		numType = radix == 10 ? "digit" : numType;
		numType = radix == 2 ? "bit" : numType;
		return numType;
	}

	/**
	 * A support function for determining the ABNF-marker for the specified
	 * radix.
	 * 
	 * @param radix
	 *            the radix
	 * @return the ABNF-marker for the specified radix
	 */
	private static String markerForRadix(int radix) {
		String marker = "";
		marker = radix == 16 ? "x" : marker;
		marker = radix == 10 ? "d" : marker;
		marker = radix == 2 ? "b" : marker;
		return marker;
	}

	/**
	 * A support function for determining the ABNF rule name for the specified
	 * radix.
	 * 
	 * @param radix
	 *            the radix
	 * @return the ABNF rule name for the specified radix
	 */
	private static String nameForRadix(int radix) {
		String name = "";
		name = radix == 16 ? "hex-val" : name;
		name = radix == 10 ? "dec-val" : name;
		name = radix == 2 ? "bin-val" : name;
		return name;
	}

	/**
	 * A support function for appending the specified content to the parent. The
	 * content is split into individual characters, mapped to a
	 * {@link NonTerminalNode} (with the specified name) and then appended to
	 * the parent.
	 * 
	 * @param parent
	 *            the parent
	 * @param childName
	 *            the name of the {@link NonTerminalNode}
	 * @param content
	 *            then content
	 */
	private static void append(Node parent, String childName, String content) {
		appendChildren(parent, (terminal) -> {
			Node alpha = new NonTerminalNode(childName);
			append(alpha, terminal);
			return alpha;
		}, content);
	}

	/**
	 * A support function for appending the specified content to the parent. The
	 * content is split into individual characters, mapped to a
	 * {@link TerminalNode} and then appended to the parent.
	 * 
	 * @param parent
	 *            the parent
	 * @param content
	 *            then content
	 */
	private static void appendChildren(Node parent, String content) {
		appendChildren(parent, (terminal) -> new TerminalNode(terminal), content);
	}

	/**
	 * A support function for appending the specified content to the parent. The
	 * content is split into individual characters, mapped to a {@link Node} and
	 * then appended to the parent.
	 * 
	 * @param parent
	 *            the parent
	 * @param mapper
	 *            the function that maps characters from the content to
	 *            {@link Node}
	 * @param content
	 *            then content
	 */
	private static void appendChildren(Node parent, Function<String, Node> mapper, String content) {
		for (int index = 0; index < content.length(); index++) {
			String terminal = content.substring(index, index + 1);
			append(parent, mapper.apply(terminal));
		}
	}

	/**
	 * A support function for appending a "white space"-node and optionally a
	 * delimiter-node to the parent.
	 * 
	 * @param parent
	 *            the parent
	 * @param delimiter
	 *            the optional delimiter
	 */
	private static void appendDelimiter(Node parent, String delimiter) {
		Node wsp = generateWSP();

		Node cwsp = new NonTerminalNode("c-wsp");
		append(cwsp, wsp);

		append(parent, cwsp);
		if (delimiter != null) {
			append(parent, delimiter);
			append(parent, cwsp);
		}
	}

	/**
	 * A support function for appending a {@link TerminalNode} to the parent.
	 * 
	 * @param parent
	 *            the parent
	 * @param terminal
	 *            the {@link TerminalNode}'s terminal symbol
	 */
	private static void append(Node parent, String terminal) {
		parent.addChild(new TerminalNode(terminal));
	}

	/**
	 * A support function for appending the child to the parent.
	 * 
	 * @param parent
	 *            the parent
	 * @param child
	 *            the child
	 */
	private static void append(Node parent, Node child) {
		parent.addChild(child);
	}

	/**
	 * A support function for generating a node that encapsulates a single white
	 * space character.
	 * 
	 * @return a node that encapsulates a single white space character
	 */
	private static Node generateWSP() {
		Node wsp = new NonTerminalNode("wsp");
		append(wsp, " ");
		return wsp;
	}

	/**
	 * A support function for generating a node that encapsulates a rule
	 * definition.
	 * 
	 * @param incremental
	 *            whether the definition is incremental (i.e. "=/") or not (i.e.
	 *            "=")
	 * @return a node that encapsulates a rule definition
	 */
	private static Node generateDefinedAs(boolean incremental) {
		Node definedAs = new NonTerminalNode("defined-as");
		Node cwsp = new NonTerminalNode("c-wsp");
		append(cwsp, generateWSP());
		append(definedAs, cwsp);
		append(definedAs, incremental ? "=/" : "=");
		append(definedAs, cwsp);
		return definedAs;
	}

	/**
	 * A support function for generating a node that encapsulates a new line (at
	 * the end of a rule definition) and optionally includes a comment.
	 * 
	 * @param actualComment
	 *            the comment
	 * @return a node that encapsulates a new line (at the end of a rule
	 *         definition) and optionally includes a comment
	 */
	private static Node generateNewLine(String actualComment) {
		Node crlf = new NonTerminalNode("crlf");
		append(crlf, "\r\n");
		if (actualComment != null) {
			Node comment = new NonTerminalNode("comment");
			append(comment, ";");
			append(comment, generateWSP());
			appendChildren(comment, (terminal) -> {
				String childName = terminal.trim().isEmpty() ? "wsp" : "vchar";
				Node alpha = new NonTerminalNode(childName);
				append(alpha, terminal);
				return alpha;
			}, actualComment);
			append(comment, crlf);
			return comment;
		}
		return crlf;
	}

	/**
	 * A support function for generating a node that encapsulates a
	 * {@link NonTerminal}-rule of a grammar.
	 * 
	 * @param rule
	 *            the rule
	 * @return a node that encapsulates a {@link NonTerminal}-rule of a grammar
	 */
	private static Node generate(NonTerminal rule) {
		Node rulename = new NonTerminalNode("rulename");
		append(rulename, "alpha", rule.getName());
		return rulename;
	}

	/**
	 * A support function for generating a node that encapsulates a
	 * {@link CharacterValue}-rule of a grammar.
	 * 
	 * @param rule
	 *            the rule
	 * @return a node that encapsulates a {@link CharacterValue}-rule of a
	 *         grammar
	 */
	private static Node generate(CharacterValue rule) {
		String terminal = rule.getTerminal();
		Node charVal = new NonTerminalNode("char-val");
		String name = rule.isCaseSensitive() ? "case-sensitive-string" : "case-insensitive-string";
		Node caseString = new NonTerminalNode(name);
		append(charVal, caseString);
		if (rule.isCaseSensitive()) {
			append(caseString, "%s");
		}
		Node quotedString = new NonTerminalNode("quoted-string");
		append(caseString, quotedString);

		Node dQuote = new NonTerminalNode("dQuote");
		append(dQuote, "\"");
		append(quotedString, dQuote);
		appendChildren(quotedString, terminal);
		append(quotedString, dQuote);
		return charVal;
	}

	/**
	 * A support function for generating a node that encapsulates a
	 * {@link NumberValue}-rule of a grammar.
	 * 
	 * @param rule
	 *            the rule
	 * @return a node that encapsulates a {@link NumberValue}-rule of a grammar
	 */
	private static Node generate(NumberValue rule) {
		int radix = rule.getRadix();
		String name = nameForRadix(radix);
		String marker = markerForRadix(radix);
		String numType = typeForRadix(radix);

		Node value = new NonTerminalNode(name);
		append(value, marker);
		if (rule.getTerminal() == null) {
			String start = Integer.toString(rule.getRangeStart().charValue(), radix);
			String end = Integer.toString(rule.getRangeEnd().charValue(), radix);

			append(value, numType, start);
			append(value, "-");
			append(value, numType, end);
		} else {
			String terminal = rule.getTerminal();
			for (int index = 0; index < terminal.length(); index++) {
				String number = Integer.toString(terminal.charAt(index), radix);
				append(value, numType, number);
				if (index + 1 < terminal.length()) {
					append(value, ".");
				}
			}
		}

		Node numVal = new NonTerminalNode("num-val");
		append(numVal, "%");
		append(numVal, value);
		return numVal;
	}

	/**
	 * A support function for generating a node that encapsulates an
	 * {@link Optional}-rule of a grammar.
	 * 
	 * @param rule
	 *            the rule
	 * @return a node that encapsulates an {@link Optional}-rule of a grammar
	 */
	private static Node generate(Optional rule) {
		Node intermediate = new NonTerminalNode("option");
		append(intermediate, "[");
		append(intermediate, generateAlternation(rule.getRule()));
		append(intermediate, "]");
		return intermediate;
	}

}
