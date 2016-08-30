package de.claas.parser.visitors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The class {@link RuleToString}. It is an implementation of the interface
 * {@link RuleVisitor}. It is intended to "visualize" a tree of {@link Rule}
 * objects. The tree is turned into a human readable (if not "pretty") string.
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
	 * <code>{@link #RuleToString(String, String)}</code> with {@value #DEFAULT_
	 * LEVEL_SEPARATOR} as default level separator and the system's line
	 * separator (property {@literal line.separator}).
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
		if (visitedPath.add(rule)) {
			incrementIndent();
			for (Rule child : rule) {
				child.visit(this);
			}
			decrementIndent();
			visitedPath.remove(rule);
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		appendRule(rule, null);
		if (visitedPath.add(rule)) {
			incrementIndent();
			for (Rule child : rule) {
				child.visit(this);
			}
			decrementIndent();
			visitedPath.remove(rule);
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		appendRule(rule, rule.getName());
		if (visitedPath.add(rule)) {
			incrementIndent();
			rule.getRule().visit(this);
			decrementIndent();
			visitedPath.remove(rule);
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		appendRule(rule, null);
		if (visitedPath.add(rule)) {
			incrementIndent();
			rule.getRule().visit(this);
			decrementIndent();
			visitedPath.remove(rule);
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		appendRule(rule, null);
		if (visitedPath.add(rule)) {
			incrementIndent();
			rule.getRule().visit(this);
			decrementIndent();
			visitedPath.remove(rule);
		}
	}

	@Override
	public void visitTerminal(Terminal rule) {
		Iterator<String> iterator = rule.getTerminals();
		while (iterator.hasNext()) {
			appendRule(rule, iterator.next());
		}
	}

	/**
	 * Increments the level / indentation for the next rule.
	 */
	private void incrementIndent() {
		levels.incrementAndGet();
	}

	/**
	 * Decrements the level / indentation for the next rule.
	 */
	private void decrementIndent() {
		levels.decrementAndGet();
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
		builder.append(new String(new byte[levels.get()]).replaceAll("\0", levelSeparator));
		builder.append(rule.getClass().getSimpleName());
		if (postfix != null) {
			builder.append(":");
			builder.append(postfix);
		}
		builder.append(lineSeparator);
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
