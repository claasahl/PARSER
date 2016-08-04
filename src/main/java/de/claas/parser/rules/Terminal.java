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
	 * Creates an instances with the given parameters.
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
	 * @param terminals
	 *            the terminal symbols
	 */
	public Terminal(char... terminals) {
		this.terminals = new ArrayList<>();
		for(char c : terminals) {
			this.terminals.add("" + c);
		}
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
		String token = state.processToken();
		if (token == null)
			return null;

		// look for terminal symbol
		Node node = null;
		for (String terminal : terminals) {
			if (terminal.equals(token)) {
				node = new TerminalNode(token);
				break;
			}
		}

		// revert state if no valid terminal was found
		if (node == null)
			state.unprocessToken();
		return node;
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitTerminal(this);
	}

}