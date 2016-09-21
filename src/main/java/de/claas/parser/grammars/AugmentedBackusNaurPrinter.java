package de.claas.parser.grammars.abnf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.claas.parser.Grammar;
import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.exceptions.CyclicRuleException;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The class {@link AugmentedBackusNaurPrinter}. It is an implementation of the
 * interface {@link RuleVisitor}. It is intended to "visualize" a
 * {@link Grammar} by turning it into a human readable string. The resulting
 * string is in augmented Backus Naur form. Details on syntax and grammar can be
 * found in <a href="https://www.ietf.org/rfc/rfc2234.txt">RFC 2234</a>.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to visualize multiple grammars.
 *
 * @author Claas Ahlrichs
 *
 */
public class AugmentedBackusNaurPrinter implements RuleVisitor {

	private static final String DEFAULT_LINE_NEWLINE = "\r\n";
	private final Set<Rule> visitedPath = new HashSet<>();
	private final Set<Rule> visitedNonTerminals = new HashSet<>();
	private final List<String> printedRules = new ArrayList<>();
	private final String lineSeparator;

	/**
	 * 
	 * Constructs a new {@link AugmentedBackusNaurPrinter} with default
	 * parameters. Calling this constructor is equivalent to calling
	 * <code>{@link #AugmentedBackusNaurPrinter(NonTerminal, String)}</code>
	 * with the system's line separator (property {@literal line.separator}).
	 * 
	 * @param rule
	 *            the (non terminal) rule
	 */
	public AugmentedBackusNaurPrinter(NonTerminal rule) {
		this(rule, System.getProperty("line.separator", DEFAULT_LINE_NEWLINE));
	}

	/**
	 * Constructs a new {@link AugmentedBackusNaurPrinter} with the specified
	 * parameters. The specified (non terminal) rule is assumed to represent the
	 * grammar's root-rule and it is stringified (i.e. turned into a string).
	 * The line separator is appended to every stringified {@link NonTerminal}
	 * rule.
	 * 
	 * @param rule
	 *            the (non terminal) rule
	 * @param lineSeparator
	 *            the line separator
	 */
	public AugmentedBackusNaurPrinter(NonTerminal rule, String lineSeparator) {
		this.lineSeparator = lineSeparator;
		rule.visit(this);
	}

	@Override
	public void visitConjunction(Conjunction rule) {
		if (visitedPath.add(rule)) {
			for (Rule child : rule)
				child.visit(this);
			visitedPath.remove(rule);
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		if (visitedPath.add(rule)) {
			for (Rule child : rule)
				child.visit(this);
			visitedPath.remove(rule);
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		if (visitedPath.add(rule)) {
			if (visitedNonTerminals.add(rule)) {
				String printedRule = new NonTerminalPrinter(rule).toString();
				printedRules.add(printedRule);
				rule.getRule().visit(this);
			}
			visitedPath.remove(rule);
		} else {
			// non terminal rule has already been printed
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		if (visitedPath.add(rule)) {
			rule.getRule().visit(this);
			visitedPath.remove(rule);
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		if (visitedPath.add(rule)) {
			rule.getRule().visit(this);
			visitedPath.remove(rule);
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitTerminal(Terminal rule) {
		// nothing to be done
	}

	@Override
	public String toString() {
		return String.join(this.lineSeparator, this.printedRules);
	}

	/**
	 * 
	 * The class {@link NonTerminalPrinter}. It is an implementation of the
	 * interface {@link RuleVisitor}. It is intended to "visualize" a single
	 * {@link NonTerminal} rule by turning it into a human readable string. The
	 * resulting string is in augmented Backus Naur form. Details on syntax and
	 * grammar can be found in
	 * <a href="https://www.ietf.org/rfc/rfc2234.txt">RFC 2234</a>.
	 *
	 * @author Claas Ahlrichs
	 *
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
			if (visitedPath.add(rule)) {
				stringBuilder.append("(");
				Iterator<Rule> children = rule.iterator();
				while (children.hasNext()) {
					children.next().visit(this);
					if (children.hasNext())
						stringBuilder.append(" ");
				}
				stringBuilder.append(")");
				visitedPath.remove(rule);
			} else {
				throw new CyclicRuleException(rule);
			}
		}

		@Override
		public void visitDisjunction(Disjunction rule) {
			if (visitedPath.add(rule)) {
				stringBuilder.append("(");
				Iterator<Rule> children = rule.iterator();
				while (children.hasNext()) {
					children.next().visit(this);
					if (children.hasNext())
						stringBuilder.append(" / ");
				}
				stringBuilder.append(")");
				visitedPath.remove(rule);
			} else {
				throw new CyclicRuleException(rule);
			}
		}

		@Override
		public void visitNonTerminal(NonTerminal rule) {
			if (visitedPath.add(rule)) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(rule.getName());
				} else {
					stringBuilder.append(rule.getName());
					stringBuilder.append(" = ");
					rule.getRule().visit(this);
				}
				visitedPath.remove(rule);
			} else {
				stringBuilder.append(rule.getName());
			}
		}

		@Override
		public void visitOptional(Optional rule) {
			if (visitedPath.add(rule)) {
				stringBuilder.append("*1");
				stringBuilder.append("(");
				rule.getRule().visit(this);
				stringBuilder.append(")");
				visitedPath.remove(rule);
			} else {
				throw new CyclicRuleException(rule);
			}
		}

		@Override
		public void visitRepetition(Repetition rule) {
			if (visitedPath.add(rule)) {
				stringBuilder.append("*");
				stringBuilder.append("(");
				rule.getRule().visit(this);
				stringBuilder.append(")");
				visitedPath.remove(rule);
			} else {
				throw new CyclicRuleException(rule);
			}
		}

		@Override
		public void visitTerminal(Terminal rule) {
			stringBuilder.append("(");
			Iterator<String> terminals = rule.getTerminals();
			while (terminals.hasNext()) {
				String terminal = terminals.next();
				if (terminal.length() == 1) {
					char character = terminal.charAt(0);
					if (Character.isISOControl(character)) {
						stringBuilder.append(String.format("x%02X", (int) character));
					} else {
						stringBuilder.append("'");
						stringBuilder.append(terminal);
						stringBuilder.append("'");
					}
				} else {
					stringBuilder.append("'");
					stringBuilder.append(terminal);
					stringBuilder.append("'");
				}
				if (terminals.hasNext())
					stringBuilder.append(" / ");
			}
			stringBuilder.append(")");
		}

		@Override
		public String toString() {
			return stringBuilder.toString();
		}
	}
}
