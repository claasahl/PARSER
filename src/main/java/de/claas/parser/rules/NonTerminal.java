package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;

/**
 * 
 * The class {@link NonTerminal}. It is an implementation of the
 * {@link Decorator} class. It is intended to represent a non-terminal rule
 * within a grammar. Non-terminal rules are named rules which largely make up a
 * grammar.
 * <p>
 * This rule acts like any other rule. The only difference is that it has a name
 * and an optional comment.
 * 
 * @author Claas Ahlrichs
 *
 */
public class NonTerminal extends Decorator {

	private final String name;
	private final String comment;

	/**
	 * 
	 * Constructs a new {@link NonTerminal} with the specified parameters.
	 * Calling this constructor is equivalent to calling
	 * <code>{@link NonTerminal#NonTerminal(String, String, Rule)}</code> with
	 * <code>null</code> as comment as and <code>null</code> as rule.
	 * 
	 * @param name
	 *            the name
	 */
	public NonTerminal(String name) {
		this(name, null, null);
	}

	/**
	 * 
	 * Constructs a new {@link NonTerminal} with the specified parameters.
	 * Calling this constructor is equivalent to calling
	 * <code>{@link NonTerminal#NonTerminal(String, String, Rule)}</code> with
	 * <code>null</code> as comment.
	 * 
	 * @param name
	 *            the name
	 * @param rule
	 *            the decorated rule
	 */
	public NonTerminal(String name, Rule rule) {
		this(name, null, rule);
	}

	/**
	 * 
	 * Constructs a new {@link NonTerminal} with the specified parameters.
	 * 
	 * @param name
	 *            the name
	 * @param comment
	 *            the comment
	 * @param rule
	 *            the decorated rule
	 */
	public NonTerminal(String name, String comment, Rule rule) {
		super(rule);
		this.name = name;
		this.comment = comment;
	}

	/**
	 * Returns the name of this (non-terminal) rule.
	 * 
	 * @return the name of this (non-terminal) rule
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the comment of this (non-terminal) rule.
	 * 
	 * @return the comment of this (non-terminal) rule
	 */
	public String getComment() {
		return this.comment;
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitNonTerminal(this);
	}

}