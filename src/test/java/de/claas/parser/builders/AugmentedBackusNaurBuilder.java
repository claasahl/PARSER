package de.claas.parser.builders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	 * Constructs a new {@link AugmentedBackusNaurBuilder} with default parameters.
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
			for (int index = 0; index < name.length(); index++) {
				Node alpha = new NonTerminalNode("alpha");
				String terminal = name.substring(index, index + 1);
				alpha.addChild(new TerminalNode(terminal));
				rulename.addChild(alpha);
			}

			Node definedAs = new NonTerminalNode("defined-as");
			Node wsp = new NonTerminalNode("wsp");
			wsp.addChild(new TerminalNode(" "));
			Node cwsp = new NonTerminalNode("c-wsp");
			cwsp.addChild(wsp);
			definedAs.addChild(cwsp);
			if (isIncrementalAlternative)
				definedAs.addChild(new TerminalNode("=/"));
			else
				definedAs.addChild(new TerminalNode("="));
			definedAs.addChild(cwsp);

			Node elements = new NonTerminalNode("elements");
			elements.addChild(generateAlternation(actualRule.getRule()));

			Node CRLF = new NonTerminalNode("crlf");
			CRLF.addChild(new TerminalNode("\r\n"));
			Node NL = new NonTerminalNode("c-nl");
			if (actualRule.getComment() != null) {
				Node comment = new NonTerminalNode("comment");
				comment.addChild(new TerminalNode(";"));
				comment.addChild(wsp);

				String actualComment = actualRule.getComment();
				for (int index = 0; index < actualComment.length(); index++) {
					String terminal = actualComment.substring(index, index + 1);
					boolean isWhitespace = terminal.trim().isEmpty();
					Node vchar = new NonTerminalNode(isWhitespace ? "wsp" : "vchar");
					vchar.addChild(new TerminalNode(terminal));
					comment.addChild(vchar);
				}
				comment.addChild(CRLF);
				NL.addChild(comment);
			} else {
				NL.addChild(CRLF);
			}

			Node rule = new NonTerminalNode("rule");
			rule.addChild(rulename);
			rule.addChild(definedAs);
			rule.addChild(elements);
			rule.addChild(NL);
			rulelist.addChild(rule);
		}
		return rulelist;
	}

	private static Node generateAlternation(Rule actualRule) {
		Node alternation = new NonTerminalNode("alternation");
		if (actualRule.getClass().isAssignableFrom(Disjunction.class)) {
			boolean firstChild = true;
			for (Rule child : actualRule) {
				if (!firstChild) {
					Node wsp = new NonTerminalNode("wsp");
					wsp.addChild(new TerminalNode(" "));
					Node cwsp = new NonTerminalNode("c-wsp");
					cwsp.addChild(wsp);
					alternation.addChild(cwsp);
					alternation.addChild(new TerminalNode("/"));
					alternation.addChild(cwsp);
				}
				Node concatenation = generateConcatenation(child);
				alternation.addChild(concatenation);
				firstChild = false;
			}
		} else {
			alternation.addChild(generateConcatenation(actualRule));
		}
		return alternation;
	}

	private static Node generateConcatenation(Rule actualRule) {
		Node concatenation = new NonTerminalNode("concatenation");
		if (actualRule.getClass().isAssignableFrom(Conjunction.class)) {
			boolean firstChild = true;
			for (Rule child : actualRule) {
				if (!firstChild) {
					Node wsp = new NonTerminalNode("wsp");
					wsp.addChild(new TerminalNode(" "));
					Node cwsp = new NonTerminalNode("c-wsp");
					cwsp.addChild(wsp);
					concatenation.addChild(cwsp);
				}

				Node repetition = generateRepetition(child);
				concatenation.addChild(repetition);
				firstChild = false;
			}
		} else {
			concatenation.addChild(generateRepetition(actualRule));
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
				for (int index = 0; index < number.length(); index++) {
					String n = number.substring(index, index + 1);
					Node digit = new NonTerminalNode("digit");
					digit.addChild(new TerminalNode(n));
					repeat.addChild(digit);
				}
			} else {
				if (min != 0) {
					String number = Integer.toString(min);
					for (int index = 0; index < number.length(); index++) {
						String n = number.substring(index, index + 1);
						Node digit = new NonTerminalNode("digit");
						digit.addChild(new TerminalNode(n));
						repeat.addChild(digit);
					}
				}
				repeat.addChild(new TerminalNode("*"));
				if (max != Integer.MAX_VALUE) {
					String number = Integer.toString(max);
					for (int index = 0; index < number.length(); index++) {
						String n = number.substring(index, index + 1);
						Node digit = new NonTerminalNode("digit");
						digit.addChild(new TerminalNode(n));
						repeat.addChild(digit);
					}
				}
			}
			repetition.addChild(repeat);
			repetition.addChild(generateElement(rule.getRule()));
		} else {
			repetition.addChild(generateElement(actualRule));
		}
		return repetition;
	}

	private static Node generateElement(Rule actualRule) {
		Node element = new NonTerminalNode("element");
		if (actualRule.getClass().isAssignableFrom(NonTerminal.class)) {
			NonTerminal rule = (NonTerminal) actualRule;
			Node rulename = new NonTerminalNode("rulename");
			String name = rule.getName();
			for (int index = 0; index < name.length(); index++) {
				Node alpha = new NonTerminalNode("alpha");
				String terminal = name.substring(index, index + 1);
				alpha.addChild(new TerminalNode(terminal));
				rulename.addChild(alpha);
			}
			element.addChild(rulename);
		} else if (actualRule.getClass().isAssignableFrom(CharacterValue.class)) {
			CharacterValue rule = (CharacterValue) actualRule;
			String terminal = rule.getTerminal();

			Node charVal = new NonTerminalNode("char-val");
			String name = rule.isCaseSensitive() ? "case-sensitive-string" : "case-insensitive-string";
			Node caseString = new NonTerminalNode(name);
			charVal.addChild(caseString);
			if (rule.isCaseSensitive())
				caseString.addChild(new TerminalNode("%s"));
			Node quotedString = new NonTerminalNode("quoted-string");
			caseString.addChild(quotedString);

			Node dQuote = new NonTerminalNode("dQuote");
			dQuote.addChild(new TerminalNode("\""));
			quotedString.addChild(dQuote);
			for (int index = 0; index < terminal.length(); index++) {
				String character = terminal.substring(index, index + 1);
				quotedString.addChild(new TerminalNode(character));
			}
			quotedString.addChild(dQuote);
			element.addChild(charVal);
		} else if (actualRule.getClass().isAssignableFrom(NumberValue.class)) {
			NumberValue rule = (NumberValue) actualRule;
			int radix = rule.getRadix();

			String name = "";
			name = radix == 16 ? "hex-val" : name;
			name = radix == 10 ? "dec-val" : name;
			name = radix == 2 ? "bin-val" : name;
			Node value = new NonTerminalNode(name);

			String marker = "";
			marker = radix == 16 ? "x" : marker;
			marker = radix == 10 ? "d" : marker;
			marker = radix == 2 ? "b" : marker;
			value.addChild(new TerminalNode(marker));

			String numType = "";
			numType = radix == 16 ? "hexdig" : numType;
			numType = radix == 10 ? "digit" : numType;
			numType = radix == 2 ? "bit" : numType;
			if (rule.getTerminal() != null) {
				String terminal = rule.getTerminal();
				for (int index = 0; index < terminal.length(); index++) {
					String number = Integer.toString(terminal.charAt(index), radix);
					for (char digitOfNumber : number.toCharArray()) {
						NonTerminalNode digit = new NonTerminalNode(numType);
						digit.addChild(new TerminalNode(Character.toString(digitOfNumber)));
						value.addChild(digit);
					}
					if (index + 1 < terminal.length())
						value.addChild(new TerminalNode("."));
				}
			} else {
				String start = Integer.toString(rule.getRangeStart().charValue(), radix);
				String end = Integer.toString(rule.getRangeEnd().charValue(), radix);

				for (char digitOfNumber : start.toCharArray()) {
					NonTerminalNode digit = new NonTerminalNode(numType);
					digit.addChild(new TerminalNode(Character.toString(digitOfNumber)));
					value.addChild(digit);
				}
				value.addChild(new TerminalNode("-"));
				for (char digitOfNumber : end.toCharArray()) {
					NonTerminalNode digit = new NonTerminalNode(numType);
					digit.addChild(new TerminalNode(Character.toString(digitOfNumber)));
					value.addChild(digit);
				}
			}

			Node numVal = new NonTerminalNode("num-val");
			numVal.addChild(new TerminalNode("%"));
			numVal.addChild(value);
			element.addChild(numVal);
		} else if (actualRule.getClass().isAssignableFrom(Optional.class)) {
			Optional rule = (Optional) actualRule;

			Node option = new NonTerminalNode("option");
			option.addChild(new TerminalNode("["));
			option.addChild(generateAlternation(rule.getRule()));
			option.addChild(new TerminalNode("]"));
			element.addChild(option);
		} else {
			Node option = new NonTerminalNode("group");
			option.addChild(new TerminalNode("("));
			option.addChild(generateAlternation(actualRule));
			option.addChild(new TerminalNode(")"));
			element.addChild(option);
		}
		return element;
	}
}
