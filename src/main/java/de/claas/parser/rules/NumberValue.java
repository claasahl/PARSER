package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;

/**
 * The class {@link NumberValue}. It is a concrete implementation of the
 * {@link Terminal} class. It is intended to represent a range of numeric
 * terminal symbols or a collection of numeric terminal symbols (e.g. 'letter'
 * of the grammar's alphabet) within a grammar.
 * 
 * @author Claas Ahlrichs
 */
public class NumberValue extends Terminal {

	private final int radix;
	private final String terminal;
	private final java.lang.Character rangeStart;
	private final java.lang.Character rangeEnd;

	/**
	 * Constructs a new {@link NumberValue} with the specified parameters.
	 * 
	 * @param radix
	 *            the radix (i.e. 2, 10 or 16)
	 * @param terminals
	 *            the terminal symbols
	 */
	public NumberValue(int radix, char... terminals) {
		this.radix = radix;
		this.terminal = new String(terminals);
		this.rangeStart = null;
		this.rangeEnd = null;
	}

	/**
	 * Constructs a new {@link NumberValue} with the specified parameter.
	 * 
	 * @param radix
	 *            the radix (i.e. 2, 10 or 16)
	 * @param rangeStart
	 *            first character that this rule represents (inclusive)
	 * @param rangeEnd
	 *            last character that this rule represents (inclusive)
	 */
	public NumberValue(int radix, int rangeStart, int rangeEnd) {
		this.radix = radix;
		this.terminal = null;
		this.rangeStart = new Character((char) rangeStart);
		this.rangeEnd = new Character((char) rangeEnd);
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
	 * Returns the terminal symbol if this is a collection-based terminal. If
	 * this is not a collection-based (i.e. range-based) <code>null</code> is
	 * returned.
	 * 
	 * @return the terminal symbol unless this is a collection-based terminal,
	 *         otherwise <code>null</code>
	 */
	public String getTerminal() {
		return this.terminal;
	}

	/**
	 * Returns the lower end of the range if this is a range-based terminal. If
	 * this is not a range-based (i.e. collection-based) <code>null</code> is
	 * returned.
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