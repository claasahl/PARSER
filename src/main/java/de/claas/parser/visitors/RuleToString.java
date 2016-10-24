package de.claas.parser.visitors;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.NumberValue;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;

/**
 * 
 * The class {@link RuleToString}. It is an implementation of the interface
 * {@link RuleVisitor}. It is intended to "visualize" a tree of {@link Rule}
 * objects. The tree is turned into a human readable (if not "pretty") string.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to visualize multiple trees.
 * 
 * @author Claas Ahlrichs
 *
 */
public class RuleToString implements RuleVisitor {

	private static final String DEFAULT_LEVEL_SEPARATOR = "  ";
	private static final String DEFAULT_LINE_NEWLINE = "\r\n";
	private final StringBuilder builder = new StringBuilder();
	private final AtomicInteger levels = new AtomicInteger();
	private final Set<Rule> visitedPath = new HashSet<>();
	private final String levelSeparator;
	private final String lineSeparator;

	/**
	 * Constructs a new {@link RuleToString} with default parameters. Calling
	 * this constructor is equivalent to calling
	 * <code>{@link #RuleToString(String, String)}</code> with
	 * {@link #DEFAULT_LEVEL_SEPARATOR} as default level separator and the
	 * system's line separator (property {@literal line.separator}).
	 */
	public RuleToString() {
		this(DEFAULT_LEVEL_SEPARATOR, System.getProperty("line.separator", DEFAULT_LINE_NEWLINE));
	}

	/**
	 * Constructs a new {@link RuleToString} with the specified parameters. The
	 * level separator is prefixed to every stringified (i.e. turned into a
	 * string) {@link Rule} object and signified the rule's depth within the
	 * tree. The line separator is appended to every stringified {@link Rule}
	 * object.
	 * 
	 * @param levelSeparator
	 *            the level separator
	 * @param lineSeparator
	 *            the line separator
	 */
	public RuleToString(String levelSeparator, String lineSeparator) {
		this.levelSeparator = levelSeparator;
		this.lineSeparator = lineSeparator;
	}

	@Override
	public void visitConjunction(Conjunction rule) {
		appendRule(rule, null);
		if (this.visitedPath.add(rule)) {
			incrementIndent();
			for (Rule child : rule) {
				child.visit(this);
			}
			decrementIndent();
			this.visitedPath.remove(rule);
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		appendRule(rule, null);
		if (this.visitedPath.add(rule)) {
			incrementIndent();
			for (Rule child : rule) {
				child.visit(this);
			}
			decrementIndent();
			this.visitedPath.remove(rule);
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		appendRule(rule, rule.getName());
		if (this.visitedPath.add(rule)) {
			incrementIndent();
			rule.getRule().visit(this);
			decrementIndent();
			this.visitedPath.remove(rule);
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		appendRule(rule, null);
		if (this.visitedPath.add(rule)) {
			incrementIndent();
			rule.getRule().visit(this);
			decrementIndent();
			this.visitedPath.remove(rule);
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		appendRule(rule, null);
		if (this.visitedPath.add(rule)) {
			incrementIndent();
			rule.getRule().visit(this);
			decrementIndent();
			this.visitedPath.remove(rule);
		}
	}

	@Override
	public void visitTerminal(CharacterValue rule) {
		appendRule(rule, rule.getTerminal());
	}

	@Override
	public void visitTerminal(NumberValue rule) {
		int radix = rule.getRadix();
		if (rule.getTerminal() != null) {
			String terminal = rule.getTerminal();
			appendRule(rule, stringifyTerminal(radix, terminal));
		} else {
			String start = Integer.toString(rule.getRangeStart().charValue(), radix);
			String end = Integer.toString(rule.getRangeEnd().charValue(), radix);
			appendRule(rule, String.format("%%%s%s-%s", marker(radix), start, end));
		}
	}

	/**
	 * A support function that returns a textual representation of the specified
	 * terminal symbol.
	 * 
	 * @param radix
	 *            the radix
	 * @param terminal
	 *            the terminal symbol
	 * @return a textual representation of the specified terminal symbol
	 */
	private static String stringifyTerminal(int radix, String terminal) {
		IntFunction<? extends String> mapper = (c) -> Integer.toString(c, radix);
		String value = terminal.chars().mapToObj(mapper).collect(Collectors.joining("."));

		StringBuilder builder = new StringBuilder();
		builder.append("%");
		builder.append(marker(radix));
		builder.append(value);
		return builder.toString();
	}

	/**
	 * A support function that returns the ABFN marker for the specified radix.
	 * Only radix 16, 10 and 2 are supported! Any other radix will return an
	 * empty string.
	 * 
	 * @param radix
	 *            the radix
	 * @return the ABFN marker for the specified radix, an empty string if the
	 *         radix is not valid / supported
	 */
	private static String marker(int radix) {
		switch (radix) {
		case 16:
			return "x";
		case 10:
			return "d";
		case 2:
			return "b";
		default:
			return "";
		}
	}

	/**
	 * Increments the level / indentation for the next rule.
	 */
	private void incrementIndent() {
		this.levels.incrementAndGet();
	}

	/**
	 * Decrements the level / indentation for the next rule.
	 */
	private void decrementIndent() {
		this.levels.decrementAndGet();
	}

	/**
	 * Appends the specified rule. The rule will occupy a separate line and use
	 * the correct indentation / spacing (according to its level within the
	 * tree). The rule will be represented by its (simple) class name and an
	 * optional postfix.
	 * 
	 * @param rule
	 *            the rule
	 * @param postfix
	 *            the postfix
	 */
	private void appendRule(Rule rule, String postfix) {
		this.builder.append(new String(new byte[this.levels.get()]).replaceAll("\0", this.levelSeparator));
		this.builder.append(rule.getClass().getSimpleName());
		if (postfix != null) {
			this.builder.append(":");
			this.builder.append(postfix);
		}
		this.builder.append(this.lineSeparator);
	}

	@Override
	public String toString() {
		return this.builder.toString();
	}
}
