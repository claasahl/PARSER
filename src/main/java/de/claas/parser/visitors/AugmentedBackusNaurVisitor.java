package de.claas.parser.visitors;

import java.util.Iterator;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

public class AugmentedBackusNaurVisitor implements RuleVisitor {

	@Override
	public void visitConjunction(Conjunction rule) {
		for(Rule child : rule)
			child.visit(this);
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		for(Rule child : rule)
			child.visit(this);
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		System.out.format("%s = ", rule.getName());
		for(Rule child : rule)
			child.visit(this);
		System.out.println();
	}

	@Override
	public void visitOptional(Optional rule) {
		for(Rule child : rule)
			child.visit(this);
	}

	@Override
	public void visitRepetition(Repetition rule) {
		for(Rule child : rule)
			child.visit(this);
	}

	@Override
	public void visitTerminal(Terminal rule) {
		Iterator<String> terminals = rule.getTerminals();
		boolean skipSeparator = true;
		System.out.format("[");
		while (terminals.hasNext()) {
			if (!skipSeparator) {
				System.out.format(",%s", terminals.next());
			} else {
				System.out.format("%s", terminals.next());
				skipSeparator = false;
			}

		}
		System.out.format("]");
	}

}
