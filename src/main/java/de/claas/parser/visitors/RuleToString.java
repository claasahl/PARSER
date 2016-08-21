package de.claas.parser.visitors;

import java.util.Iterator;
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
 * objects. The tree is turned into a
 * 
 * @author Claas Ahlrichs
 *
 */
public class RuleToString implements RuleVisitor {

	private static final String SEPARATOR = "-";
	private static final String NEWLINE = "\n";
	private final StringBuilder builder = new StringBuilder();
	private final AtomicInteger indents = new AtomicInteger();

	@Override
	public void visitConjunction(Conjunction rule) {
		appendRule(rule);
		incrementIndent();
		for (Rule child : rule) {
			child.visit(this);
		}
		decrementIndent();
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		appendRule(rule);
		incrementIndent();
		for (Rule child : rule) {
			child.visit(this);
		}
		decrementIndent();
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		appendRule(rule, rule.getName());
		incrementIndent();
		rule.getRule().visit(this);
		decrementIndent();
	}

	@Override
	public void visitOptional(Optional rule) {
		appendRule(rule);
		incrementIndent();
		rule.getRule().visit(this);
		decrementIndent();
	}

	@Override
	public void visitRepetition(Repetition rule) {
		appendRule(rule);
		incrementIndent();
		rule.getRule().visit(this);
		decrementIndent();
	}

	@Override
	public void visitTerminal(Terminal rule) {
		Iterator<String> iterator = rule.getTerminals();
		while (iterator.hasNext())
			appendRule(rule, iterator.next());
	}

	/**
	 * Increments the indentation for the next rule.
	 */
	private void incrementIndent() {
		indents.incrementAndGet();
	}

	/**
	 * Decrements the indentation for the next rule.
	 */
	private void decrementIndent() {
		indents.decrementAndGet();
	}

	/**
	 * Appends the specified rule with its notes. The rule will occupy a
	 * separate line and use the correct indentation / spacing.
	 * 
	 * @param rule
	 *            the rule
	 * @param notes
	 *            the notes
	 */
	private void appendRule(Rule rule, String... notes) {
		builder.append(new String(new byte[indents.get()]).replaceAll("\0", SEPARATOR));
		for (String note : notes) {
			builder.append(note);
			builder.append(SEPARATOR);
		}
		builder.append(rule.getClass().getName());
		builder.append(NEWLINE);
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
