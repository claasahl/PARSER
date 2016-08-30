package de.claas.parser.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.State;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link Terminal}. It is an implementation of the {@link Rule}
 * class. It is intended to represent a terminal symbol (e.g. 'letter' of the
 * grammar's alphabet) within a grammar.
 * <p>
 * This rule will successfully process a given state if the processed token
 * equals any of the terminal symbols (see {@link #getTerminals()}).
 * 
 * @author Claas Ahlrichs
 *
 */
public class Terminal extends Rule {

	private final List<String> terminals;

	/**
	 * Constructs a new {@link Terminal} with the specified parameters.
	 * 
	 * @param terminals
	 *            the terminal symbols
	 */
	public Terminal(String... terminals) {
		this.terminals = Arrays.asList(terminals);
	}

	/**
	 * Creates an instances with the given parameters.
	 * 
	 * @param rangeStart
	 *            first character that this rule represents (inclusive)
	 * @param rangeEnd
	 *            last character that this rule represents (inclusive)
	 */
	public Terminal(char rangeStart, char rangeEnd) {
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
		return terminals.iterator();
	}

	@Override
	public Node process(State state) {
		state.beginGroup();
		try {
			for (String terminal : terminals) {
				if (state.process(terminal)) {
					return new TerminalNode(terminal);
				}
			}
			return null;
		} finally {
			state.endGroup();
		}
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitTerminal(this);
	}

}