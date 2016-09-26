package de.claas.parser;

import de.claas.parser.exceptions.CyclicRuleException;
import de.claas.parser.rules.Character;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Number;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

/**
 * 
 * Superclass of all rule-based visitors. This class is intended to model a
 * visitor for {@link Rule} instances and their children. Implementations of
 * this class will most likely extract details (e.g. terminal symbols or
 * non-terminal symbols) or otherwise process rule-hierarchies.
 * <p>
 * This class resembles the <i>visitor</i> design pattern. It includes a
 * visit-method for all implementations of the {@link Rule} class.
 * 
 * @author Claas Ahlrichs
 *
 */
public interface RuleVisitor {

	/**
	 * Called by {@link Conjunction}-rules.
	 * 
	 * @param rule
	 *            the rule
	 * @throws CyclicRuleException
	 *             if the visited rule is part of a cyclic graph (i.e. the rule
	 *             references itself either directly or indirectly) and if this
	 *             cannot be handled by the visitor
	 */
	void visitConjunction(Conjunction rule);

	/**
	 * Called by {@link Disjunction}-rules.
	 * 
	 * @param rule
	 *            the rule
	 * @throws CyclicRuleException
	 *             if the visited rule is part of a cyclic graph (i.e. the rule
	 *             references itself either directly or indirectly) and if this
	 *             cannot be handled by the visitor
	 */
	void visitDisjunction(Disjunction rule);

	/**
	 * Called by {@link NonTerminal}-rules.
	 * 
	 * @param rule
	 *            the rule
	 * @throws CyclicRuleException
	 *             if the visited rule is part of a cyclic graph (i.e. the rule
	 *             references itself either directly or indirectly) and if this
	 *             cannot be handled by the visitor
	 */
	void visitNonTerminal(NonTerminal rule);

	/**
	 * Called by {@link Optional}-rules.
	 * 
	 * @param rule
	 *            the rule
	 * @throws CyclicRuleException
	 *             if the visited rule is part of a cyclic graph (i.e. the rule
	 *             references itself either directly or indirectly) and if this
	 *             cannot be handled by the visitor
	 */
	void visitOptional(Optional rule);

	/**
	 * Called by {@link Repetition}-rules.
	 * 
	 * @param rule
	 *            the rule
	 * @throws CyclicRuleException
	 *             if the visited rule is part of a cyclic graph (i.e. the rule
	 *             references itself either directly or indirectly) and if this
	 *             cannot be handled by the visitor
	 */
	void visitRepetition(Repetition rule);

	/**
	 * Called by {@link Terminal}-rules.
	 * 
	 * @param rule
	 *            the rule
	 */
	void visitTerminal(Terminal rule);

	/**
	 * Called by {@link Character}-rules.
	 * 
	 * @param rule
	 *            the rule
	 */
	default void visitTerminal(Character rule) {
		return;
	}
	
	/**
	 * Called by {@link Number}-rules.
	 * 
	 * @param rule
	 *            the rule
	 */
	default void visitTerminal(Number rule) {
		return;
	}

}
