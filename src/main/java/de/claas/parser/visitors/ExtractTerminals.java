package de.claas.parser.visitors;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.claas.parser.Grammar;
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
 * The class {@link ExtractTerminals}. It is an implementation of the
 * {@link RuleVisitor} class. It is intended to collect and return the terminal
 * symbols in grammars. These can than be used to tokenize (e.g.
 * {@link Grammar#tokenize(List, String)}) sentences of any grammar.
 * 
 * @author Claas Ahlrichs
 *
 */
public class ExtractTerminals implements RuleVisitor {

	private Set<String> terminals = new HashSet<>();
	private Set<Rule> visited = new HashSet<>();

	/**
	 * Returns the terminal symbols. If this visitor is used to visit multiple
	 * grammars, then the terminal symbols of all grammars are returned.
	 * 
	 * @return the terminal symbols
	 */
	public Set<String> getTerminals() {
		return Collections.unmodifiableSet(terminals);
	}

	@Override
	public void visitConjunction(Conjunction rule) {
		Iterator<Rule> iterator = rule.iterator();
		while (iterator.hasNext()) {
			Rule child = iterator.next();
			if (!visited.contains(child)) {
				visited.add(child);
				child.visit(this);
			}
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		Iterator<Rule> iterator = rule.iterator();
		while (iterator.hasNext()) {
			Rule child = iterator.next();
			if (!visited.contains(child)) {
				visited.add(child);
				child.visit(this);
			}
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		Rule child = rule.getRule();
		if (!visited.contains(child)) {
			visited.add(child);
			child.visit(this);
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		Rule child = rule.getRule();
		if (!visited.contains(child)) {
			visited.add(child);
			child.visit(this);
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		Rule child = rule.getRule();
		if (!visited.contains(child)) {
			visited.add(child);
			child.visit(this);
		}
	}

	@Override
	public void visitTerminal(Terminal rule) {
		Iterator<String> iterator = rule.getTerminals();
		while (iterator.hasNext()) {
			terminals.add(iterator.next());
		}
	}

}