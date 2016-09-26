package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;

/**
 * 
 * The class {@link Character}. It is an implementation of the {@link Rule}
 * class. It is intended to represent a terminal symbol (e.g. 'letter' of the
 * grammar's alphabet) within a grammar.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Character extends Terminal {

	private final boolean caseSensitive;
	private final String terminal;

	/**
	 * 
	 * Constructs a new {@link Character} with default parameters. Calling this
	 * constructor is equivalent to calling
	 * <code>{@link Character#Terminal(boolean, String...)}</code> without case
	 * sensitivity.
	 * 
	 * @param terminal
	 *            the terminal symbol
	 */
	public Character(String terminal) {
		this(false, terminal);
	}

	/**
	 * Constructs a new {@link Character} with the specified parameters.
	 * 
	 * @param caseSensitive
	 *            whether the terminal are case sensitive
	 * @param terminal
	 *            the terminal symbol
	 */
	public Character(boolean caseSensitive, String terminal) {
		this.caseSensitive = caseSensitive;
		this.terminal = terminal;
	}

	/**
	 * Returns the terminal symbol that this rule represents.
	 * 
	 * @return the terminal symbol that this rule represents
	 */
	public String getTerminal() {
		return this.terminal;
	}

	/**
	 * Whether the terminal is case sensitive or not.
	 * 
	 * @return <code>true</code> if the terminal are case sensitive, otherwise
	 *         <code>false</code>
	 */
	public boolean isCaseSensitive() {
		return this.caseSensitive;	
	}

	@Override
	public boolean addChild(Rule rule) {
		return false;
	}

	@Override
	public boolean removeChild(Rule rule) {
		return false;
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitTerminal(this);
	}

}