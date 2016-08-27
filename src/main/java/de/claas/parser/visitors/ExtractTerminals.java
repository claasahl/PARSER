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

	private final Set<Rule> visitedPath = new HashSet<>();
	private final Set<String> terminals = new HashSet<>();

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
		if (visitedPath.add(rule)) {
			Iterator<Rule> iterator = rule.iterator();
			while (iterator.hasNext()) {
				iterator.next().visit(this);
			}
			visitedPath.remove(rule);
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		if (visitedPath.add(rule)) {
			Iterator<Rule> iterator = rule.iterator();
			while (iterator.hasNext()) {
				iterator.next().visit(this);
			}
			visitedPath.remove(rule);
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		if (visitedPath.add(rule)) {
			rule.getRule().visit(this);
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
		Iterator<String> iterator = rule.getTerminals();
		while (iterator.hasNext()) {
			terminals.add(iterator.next());
		}
	}

}