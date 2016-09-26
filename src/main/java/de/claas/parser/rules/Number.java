package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;

/**
 * 
 * The class {@link Number}. It is an implementation of the {@link Rule} class.
 * It is intended to represent a range of numeric terminal symbols or a
 * collection of numeric terminal symbols (e.g. 'letter' of the grammar's
 * alphabet) within a grammar.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Number extends Terminal {

	private final int radix;
	private final String terminal;
	private final java.lang.Character rangeStart;
	private final java.lang.Character rangeEnd;

	/**
	 * Constructs a new {@link Number} with the specified parameters.
	 * 
	 * @param radix
	 *            the radix (i.e. 2, 10 or 16)
	 * @param terminals
	 *            the terminal symbols
	 */
	public Number(int radix, char... terminals) {
		this.radix = radix;
		this.terminal = new String(terminals);
		this.rangeStart = null;
		this.rangeEnd = null;
	}

	/**
	 * Constructs a new {@link Number} with default parameters.
	 * 
	 * @param radix
	 *            the radix (i.e. 2, 10 or 16)
	 * @param rangeStart
	 *            first character that this rule represents (inclusive)
	 * @param rangeEnd
	 *            last character that this rule represents (inclusive)
	 */
	public Number(int radix, char rangeStart, char rangeEnd) {
		this.radix = radix;
		this.terminal = null;
		this.rangeStart = new java.lang.Character(rangeStart);
		this.rangeEnd = new java.lang.Character(rangeEnd);
	}

	/**
	 * Returns the radix that is used for conversion to and from this terminal's
	 * string representation.
	 * 
	 * @return the radix that is used for conversion to and from this terminal's
	 *         string representation
	 */
	public int getRadix() {
		return this.radix;
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
	 * Returns the lower end of the range if this is a range-based terminal. If
	 * this is not a range based <code>null</code> is returned.
	 * 
	 * @return the lower end of the range if this is a range-based terminal,
	 *         otherwise <code>null</code>
	 */
	public java.lang.Character getRangeStart() {
		return this.rangeStart;
	}

	/**
	 * Returns the upper end of the range if this is a range-based terminal. If
	 * this is not a range based <code>null</code> is returned.
	 * 
	 * @return the upper end of the range if this is a range-based terminal,
	 *         otherwise <code>null</code>
	 */
	public java.lang.Character getRangeEnd() {
		return this.rangeEnd;
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