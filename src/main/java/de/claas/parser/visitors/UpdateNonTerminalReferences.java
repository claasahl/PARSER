package de.claas.parser.visitors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
 * The class {@link UpdateNonTerminalReferences}. It is an implementation of the
 * interface {@link RuleVisitor}. It is intended to updated "broken" references
 * to {@link NonTerminal}s (i.e. {@link NonTerminal}s that do not have an
 * associated rule; {@link NonTerminal#getRule()} returns <code>null</code>).
 * This visitor will look for "broken" {@link NonTerminal}s and update their
 * associated rule, which is determined by a pool of known {@link NonTerminal}s.
 *
 * @author Claas Ahlrichs
 */
public class UpdateNonTerminalReferences implements RuleVisitor {

	private final Set<Rule> visitedPath = new HashSet<>();
	private final Map<String, NonTerminal> rules = new HashMap<>();

	/**
	 * Constructs a new {@link UpdateNonTerminalReferences} with the specified
	 * parameters.
	 * 
	 * @param rules
	 *            the pool of known {@link NonTerminal}s
	 */
	public UpdateNonTerminalReferences(NonTerminal... rules) {
		for (NonTerminal rule : rules) {
			this.rules.put(rule.getName(), rule);
		}
	}

	/**
	 * Constructs a new {@link UpdateNonTerminalReferences} with the specified
	 * parameter.
	 * 
	 * @param rules
	 *            the pool of known {@link NonTerminal}s
	 */
	public UpdateNonTerminalReferences(Collection<NonTerminal> rules) {
		for (NonTerminal rule : rules) {
			this.rules.put(rule.getName(), rule);
		}
	}

	@Override
	public void visitConjunction(Conjunction rule) {
		if (this.visitedPath.add(rule)) {
			for (Rule child : rule)
				child.visit(this);
			this.visitedPath.remove(rule);
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		if (this.visitedPath.add(rule)) {
			for (Rule child : rule)
				child.visit(this);
			this.visitedPath.remove(rule);
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		if (this.visitedPath.add(rule)) {
			if (rule.getRule() == null)
				rule.setRule(this.rules.get(rule.getName()).getRule());
			rule.getRule().visit(this);
			this.visitedPath.remove(rule);
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		if (this.visitedPath.add(rule)) {
			rule.getRule().visit(this);
			this.visitedPath.remove(rule);
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		if (this.visitedPath.add(rule)) {
			rule.getRule().visit(this);
			this.visitedPath.remove(rule);
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

}