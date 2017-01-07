package de.claas.parser.grammars;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import de.claas.parser.Grammar;
import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.exceptions.CyclicRuleException;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.NumberValue;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;

/**
 * The class {@link AugmentedBackusNaurPrinter}. It is an implementation of the
 * interface {@link RuleVisitor}. It is intended to "visualize" a
 * {@link Grammar} by turning it into a human readable string. The resulting
 * string is in augmented Backus Naur form. Details on syntax and grammar can be
 * found in <a href="https://www.ietf.org/rfc/rfc5234.txt">RFC 5234</a> and
 * <a href="https://www.ietf.org/rfc/rfc7405.txt">RFC 7405</a>.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to visualize multiple grammars.
 *
 * @author Claas Ahlrichs
 */
public class AugmentedBackusNaurPrinter implements RuleVisitor {

	private static final String DEFAULT_LINE_NEWLINE = "\r\n";
	private final Set<Rule> visitedPath = new HashSet<>();
	private final Set<Rule> visitedNonTerminals = new HashSet<>();
	private final List<String> printedRules = new ArrayList<>();
	private final String lineSeparator;

	/**
	 * Constructs a new {@link AugmentedBackusNaurPrinter} with default
	 * parameters. Calling this constructor is equivalent to calling
	 * <code>{@link #AugmentedBackusNaurPrinter(String)}</code> with the
	 * system's line separator (property {@literal line.separator}).
	 */
	public AugmentedBackusNaurPrinter() {
		this(System.getProperty("line.separator", DEFAULT_LINE_NEWLINE));
	}

	/**
	 * Constructs a new {@link AugmentedBackusNaurPrinter} with the specified
	 * parameters. The line separator is appended to every visited / stringified
	 * {@link NonTerminal} rule.
	 * 
	 * @param lineSeparator
	 *            the line separator
	 */
	public AugmentedBackusNaurPrinter(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	@Override
	public void visitConjunction(Conjunction rule) {
		if (this.visitedPath.add(rule)) {
			for (Rule child : rule)
				child.visit(this);
			this.visitedPath.remove(rule);
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		if (this.visitedPath.add(rule)) {
			for (Rule child : rule)
				child.visit(this);
			this.visitedPath.remove(rule);
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		if (this.visitedPath.add(rule)) {
			if (this.visitedNonTerminals.add(rule)) {
				String printedRule = new NonTerminalPrinter(rule).toString();
				this.printedRules.add(printedRule);
				rule.getRule().visit(this);
			}
			this.visitedPath.remove(rule);
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		if (this.visitedPath.add(rule)) {
			rule.getRule().visit(this);
			this.visitedPath.remove(rule);
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		if (this.visitedPath.add(rule)) {
			rule.getRule().visit(this);
			this.visitedPath.remove(rule);
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitTerminal(CharacterValue rule) {
		// nothing to be done
	}

	@Override
	public void visitTerminal(NumberValue rule) {
		// nothing to be done
	}

	@Override
	public String toString() {
		return String.join(this.lineSeparator, this.printedRules);
	}

	/**
	 * The class {@link NonTerminalPrinter}. It is an implementation of the
	 * interface {@link RuleVisitor}. It is intended to "visualize" a single
	 * {@link NonTerminal} rule by turning it into a human readable string. The
	 * resulting string is in augmented Backus Naur form. Details on syntax and
	 * grammar can be found in
	 * <a href="https://www.ietf.org/rfc/rfc5234.txt">RFC 5234</a> and
	 * <a href="https://www.ietf.org/rfc/rfc7405.txt">RFC 7405</a>.
	 *
	 * @author Claas Ahlrichs
	 */
	private static class NonTerminalPrinter implements RuleVisitor {

		private final Set<Rule> visitedPath = new HashSet<>();
		private final StringBuilder stringBuilder = new StringBuilder();

		/**
		 * Constructs a new NonTerminalPrinter with the specified parameters.
		 * The specified (non terminal) rule is stringified.
		 * 
		 * @param rule
		 *            the (non terminal) rule
		 */
		public NonTerminalPrinter(NonTerminal rule) {
			rule.visit(this);
		}

		@Override
		public void visitConjunction(Conjunction rule) {
			if (this.visitedPath.add(rule)) {
				this.stringBuilder.append("(");
				Iterator<Rule> children = rule.iterator();
				while (children.hasNext()) {
					children.next().visit(this);
					if (children.hasNext())
						this.stringBuilder.append(" ");
				}
				this.stringBuilder.append(")");
				this.visitedPath.remove(rule);
			} else {
				throw new CyclicRuleException(rule);
			}
		}

		@Override
		public void visitDisjunction(Disjunction rule) {
			if (this.visitedPath.add(rule)) {
				this.stringBuilder.append("(");
				Iterator<Rule> children = rule.iterator();
				while (children.hasNext()) {
					children.next().visit(this);
					if (children.hasNext())
						this.stringBuilder.append(" / ");
				}
				this.stringBuilder.append(")");
				this.visitedPath.remove(rule);
			} else {
				throw new CyclicRuleException(rule);
			}
		}

		@Override
		public void visitNonTerminal(NonTerminal rule) {
			if (this.visitedPath.add(rule)) {
				if (this.stringBuilder.length() > 0) {
					this.stringBuilder.append(rule.getName());
				} else {
					this.stringBuilder.append(rule.getName());
					this.stringBuilder.append(" = ");
					rule.getRule().visit(this);
				}
				this.visitedPath.remove(rule);
			} else {
				this.stringBuilder.append(rule.getName());
			}
		}

		@Override
		public void visitOptional(Optional rule) {
			if (this.visitedPath.add(rule)) {
				this.stringBuilder.append("*1");
				this.stringBuilder.append("(");
				rule.getRule().visit(this);
				this.stringBuilder.append(")");
				this.visitedPath.remove(rule);
			} else {
				throw new CyclicRuleException(rule);
			}
		}

		@Override
		public void visitRepetition(Repetition rule) {
			if (this.visitedPath.add(rule)) {
				this.stringBuilder.append("*");
				this.stringBuilder.append("(");
				rule.getRule().visit(this);
				this.stringBuilder.append(")");
				this.visitedPath.remove(rule);
			} else {
				throw new CyclicRuleException(rule);
			}
		}

		@Override
		public void visitTerminal(CharacterValue rule) {
			this.stringBuilder.append("'");
			this.stringBuilder.append(rule.getTerminal());
			this.stringBuilder.append("'");
		}

		@Override
		public void visitTerminal(NumberValue rule) {
			int radix = rule.getRadix();
			String marker = AugmentedBackusNaur.marker(radix);
			if (rule.getTerminal() != null) {
				String terminal = rule.getTerminal();
				IntFunction<? extends String> mapper = (c) -> Integer.toString(c, radix);
				String value = terminal.chars().mapToObj(mapper).collect(Collectors.joining("."));

				this.stringBuilder.append("%");
				this.stringBuilder.append(marker);
				this.stringBuilder.append(value);
			} else {
				String start = Integer.toString(rule.getRangeStart().charValue(), radix);
				String end = Integer.toString(rule.getRangeEnd().charValue(), radix);
				this.stringBuilder.append("%");
				this.stringBuilder.append(marker);
				this.stringBuilder.append(start);
				this.stringBuilder.append("-");
				this.stringBuilder.append(end);
			}
		}

		@Override
		public String toString() {
			return this.stringBuilder.toString();
		}
	}
}
