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

public class AugmentedBackusNaurPrinter implements RuleVisitor {
	
	private final StringBuilder stringBuilder;
	
	public AugmentedBackusNaurPrinter(NonTerminal rule) {
		this.stringBuilder = new StringBuilder();
		rule.visit(this);
	}

	@Override
	public void visitConjunction(Conjunction rule) {
		stringBuilder.append("(");
		Iterator<Rule> children = rule.iterator();
		while(children.hasNext()) {
			children.next().visit(this);
			if(children.hasNext())
				stringBuilder.append(" ");
		}
		stringBuilder.append(")");
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		stringBuilder.append("(");
		Iterator<Rule> children = rule.iterator();
		while(children.hasNext()) {
			children.next().visit(this);
			if(children.hasNext())
				stringBuilder.append(" / ");
		}
		stringBuilder.append(")");
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		if(stringBuilder.length() > 0) {
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
		while(terminals.hasNext()) {
			stringBuilder.append("'");
			stringBuilder.append(terminals.next());
			stringBuilder.append("'");
			if(terminals.hasNext())
				stringBuilder.append(" / ");
		}
		stringBuilder.append(")");
	}
	
	@Override
	public String toString() {
		return stringBuilder.toString();
	}

}
