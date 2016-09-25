package de.claas.parser.rules;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;

/**
 * 
 * The class {@link Terminal}. It is an implementation of the {@link Rule}
 * class. It is intended to represent a terminal symbol (e.g. 'letter' of the
 * grammar's alphabet) within a grammar.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Terminal extends Rule {

	private final boolean caseSensitive;
	private final List<String> terminals;

	/**
	 * 
	 * Constructs a new {@link Terminal} with default parameters. Calling this
	 * constructor is equivalent to calling
	 * <code>{@link Terminal#Terminal(boolean, String...)}</code> without case
	 * sensitivity.
	 * 
	 * @param terminals
	 *            the terminal symbols
	 */
	public Terminal(String... terminals) {
		this(false, terminals);
	}

	/**
	 * Constructs a new {@link Terminal} with the specified parameters.
	 * 
	 * @param caseSensitive
	 *            whether the terminals are case sensitive
	 * @param terminals
	 *            the terminal symbols
	 */
	public Terminal(boolean caseSensitive, String... terminals) {
		this.caseSensitive = caseSensitive;
		this.terminals = Arrays.asList(terminals);
		Collections.sort(this.terminals, Collections.reverseOrder(new TerminalLengthComparator()));
	}

	/**
	 * Constructs a new {@link Terminal} with default parameters.
	 * 
	 * @param rangeStart
	 *            first character that this rule represents (inclusive)
	 * @param rangeEnd
	 *            last character that this rule represents (inclusive)
	 */
	public Terminal(char rangeStart, char rangeEnd) {
		this.caseSensitive = true;
		this.terminals = new ArrayList<>();
		for (int character = rangeStart; character <= rangeEnd; character++)
			this.terminals.add("" + (char) character);
	}

	/**
	 * Returns the terminal symbols that this rule represents.
	 * 
	 * @return the terminal symbols that this rule represents
	 */
	public Iterator<String> getTerminals() {
		return this.terminals.iterator();
	}

	/**
	 * Whether the terminals are case sensitive or not.
	 * 
	 * @return <code>true</code> if the terminals are case sensitive, otherwise
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

	/**
	 * 
	 * The class {@link TerminalLengthComparator}. It is intended to order
	 * terminal symbols according to their length. The rational behind this
	 * comparator is such that longer terminal symbols are given preference by
	 * the {@link Terminal} rule that uses it.
	 * 
	 * @author Claas Ahlrichs
	 *
	 */
	private static class TerminalLengthComparator implements Comparator<String> {

		/**
		 * Constructs a new {@link TerminalLengthComparator} with default
		 * parameters.
		 */
		public TerminalLengthComparator() {
		}

		@Override
		public int compare(String terminalA, String terminalB) {
			return Integer.compare(terminalA.length(), terminalB.length());
		}

	}

}