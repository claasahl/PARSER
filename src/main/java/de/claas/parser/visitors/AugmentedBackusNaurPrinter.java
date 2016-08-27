package de.claas.parser.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

public class AugmentedBackusNaurPrinter implements RuleVisitor {

	private final Set<Rule> visitedPath = new HashSet<>();
	private final Set<Rule> visitedNonTerminals = new HashSet<>();
	private final List<String> printedRules = new ArrayList<>();

	public AugmentedBackusNaurPrinter(NonTerminal rule) {
		rule.visit(this);
	}

	@Override
	public void visitConjunction(Conjunction rule) {
		if (visitedPath.add(rule)) {
			for (Rule child : rule)
				child.visit(this);
			visitedPath.remove(rule);
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		if (visitedPath.add(rule)) {
			for (Rule child : rule)
				child.visit(this);
			visitedPath.remove(rule);
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
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		if (visitedPath.add(rule)) {
			rule.getRule().visit(this);
			visitedPath.remove(rule);
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		if (visitedPath.add(rule)) {
			rule.getRule().visit(this);
			visitedPath.remove(rule);
		}
	}

	@Override
	public void visitTerminal(Terminal rule) {
		// nothing to be done
	}

	@Override
	public String toString() {
		return printedRules.stream().collect(Collectors.joining("\n"));
	}

	private static class NonTerminalPrinter implements RuleVisitor {

		private final StringBuilder stringBuilder;

		public NonTerminalPrinter(NonTerminal rule) {
			this.stringBuilder = new StringBuilder();
			rule.visit(this);
		}

		@Override
		public void visitConjunction(Conjunction rule) {
			stringBuilder.append("(");
			Iterator<Rule> children = rule.iterator();
			while (children.hasNext()) {
				children.next().visit(this);
				if (children.hasNext())
					stringBuilder.append(" ");
			}
			stringBuilder.append(")");
		}

		@Override
		public void visitDisjunction(Disjunction rule) {
			stringBuilder.append("(");
			Iterator<Rule> children = rule.iterator();
			while (children.hasNext()) {
				children.next().visit(this);
				if (children.hasNext())
					stringBuilder.append(" / ");
			}
			stringBuilder.append(")");
		}

		@Override
		public void visitNonTerminal(NonTerminal rule) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(rule.getName());
			} else {
				stringBuilder.append(rule.getName());
				stringBuilder.append(" = ");
				rule.getRule().visit(this);
			}

		}

		@Override
		public void visitOptional(Optional rule) {
			stringBuilder.append("*1");
			stringBuilder.append("(");
			rule.getRule().visit(this);
			stringBuilder.append(")");
		}

		@Override
		public void visitRepetition(Repetition rule) {
			stringBuilder.append("*");
			stringBuilder.append("(");
			rule.getRule().visit(this);
			stringBuilder.append(")");
		}

		@Override
		public void visitTerminal(Terminal rule) {
			stringBuilder.append("(");
			Iterator<String> terminals = rule.getTerminals();
			while (terminals.hasNext()) {
				stringBuilder.append("'");
				String terminal = terminals.next();
				if (terminal.length() == 1) {
					char character = terminal.charAt(0);
					if (Character.isLetterOrDigit(character)) {
						stringBuilder.append(terminal);
					} else {
						stringBuilder.append(String.format("x%02X", (int) character));
					}
				} else {
					stringBuilder.append(terminal);
				}
				stringBuilder.append("'");
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
