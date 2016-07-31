package de.claas.parser;

/**
 * 
 * Superclass of all rule-based visitors. This class is intended to model a
 * visitor for {@link Rule} instances and their children. Implementations of
 * this class will most likely extract details (e.g. terminal symbols or
 * non-terminal symbols) or otherwise process rule-hierarchies.
 * <p/>
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
	 */
	void visitConjunction(Conjunction rule);

	/**
	 * Called by {@link Disjunction}-rules.
	 * 
	 * @param rule
	 *            the rule
	 */
	void visitDisjunction(Disjunction rule);

	/**
	 * Called by {@link NonTerminal}-rules.
	 * 
	 * @param rule
	 *            the rule
	 */
	void visitNonTerminal(NonTerminal rule);

	/**
	 * Called by {@link Optional}-rules.
	 * 
	 * @param rule
	 *            the rule
	 */
	void visitOptional(Optional rule);

	/**
	 * Called by {@link Repetition}-rules.
	 * 
	 * @param rule
	 *            the rule
	 */
	void visitRepetition(Repetition rule);

	/**
	 * Called by {@link Terminal}-rules.
	 * 
	 * @param rule
	 *            the rule
	 */
	void visitTerminal(Terminal rule);

}
