package de.claas.parser;

import de.claas.parser.exceptions.ParsingException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.visitors.RemoveIntermediateNodes;

/**
 * 
 * The class {@link Grammar}. It is intended to parse sentences of a given
 * grammar. This class takes a {@link NonTerminal} as initial rule (i.e. named
 * rule) and uses it to parse sentences. The result is returned as a tree of
 * {@link Node} instances.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Grammar {

	private final NonTerminal start;

	/**
	 * 
	 * Constructs a new {@link Grammar} with the specified parameter.
	 * 
	 * @param grammar
	 *            the grammar's initial {@link NonTerminal} rule
	 */
	public Grammar(NonTerminal grammar) {
		this.start = grammar;
	}

	/**
	 * Parses and returns the tree of terminals and non-terminals that
	 * represents the specified data. Any intermediate nodes (and thus
	 * non-essential nodes) are removed. Any preceding and trailing whitespace
	 * are removed as well. If the given data is in any way invalid, then
	 * <code>null</code> is returned.
	 * <p>
	 * Calling this method is equivalent <code>tryParse(data, false)</code> (see
	 * {@link #tryParse(String, boolean)}).
	 * 
	 * @param data
	 *            the data that is being parsed
	 * @return the tree of terminals and non-terminals that represents the
	 *         specified data
	 */
	public Node tryParse(String data) {
		return tryParse(data, false);
	}

	/**
	 * Parses and returns the tree of terminals and non-terminals that
	 * represents the specified data. Any intermediate nodes (and thus
	 * non-essential nodes) are removed. Any preceding and trailing whitespace
	 * are removed as well. If the given data is in any way invalid, then a
	 * {@link ParsingException} is thrown.
	 * <p>
	 * Calling this method is equivalent <code>parse(data, false)</code> (see
	 * {@link #parse(String, boolean)}).
	 * 
	 * @param data
	 *            the data that is being parsed
	 * @return the tree of terminals and non-terminals that represents the
	 *         specified data
	 * @throws ParsingException
	 *             if the data is invalid (e.g. contains illegal tokens or the
	 *             data is otherwise not in accordance with the grammar that was
	 *             passed into the constructor)
	 */
	public Node parse(String data) {
		return parse(data, false);
	}

	/**
	 * Parses and returns the tree of terminals and non-terminals that
	 * represents the specified data. Optionally intermediate nodes (and thus
	 * non-essential nodes) can be removed. Also optionally, preceding and
	 * trailing whitespace can be removed. If the given data is in any way
	 * invalid, then <code>null</code> is returned.
	 * 
	 * @param data
	 *            the data that is being parsed
	 * @param retainIntermediateNodes
	 *            whether {@link IntermediateNode} instances should be retained.
	 *            Set to <code>false</code> if only non-terminal and terminal
	 *            nodes are desired
	 * @return the tree of terminals and non-terminals that represents the
	 *         specified data
	 */
	public Node tryParse(String data, boolean retainIntermediateNodes) {
		State state = new State(data);
		Node node = start.process(state);
		if (node != null && !retainIntermediateNodes)
			node.visit(new RemoveIntermediateNodes());
		return node;
	}

	/**
	 * Parses and returns the tree of terminals and non-terminals that
	 * represents the specified data. Optionally intermediate nodes (and thus
	 * non-essential nodes) can be removed. Also optionally, preceding and
	 * trailing whitespace can be removed. If the given data is in any way
	 * invalid, then a {@link ParsingException} is thrown.
	 * 
	 * @param data
	 *            the data that is being parsed
	 * @param retainIntermediateNodes
	 *            whether {@link IntermediateNode} instances should be retained.
	 *            Set to <code>false</code> if only non-terminal and terminal
	 *            nodes are desired
	 * @return the tree of terminals and non-terminals that represents the
	 *         specified data
	 * @throws ParsingException
	 *             if the data is invalid (e.g. contains illegal tokens or the
	 *             data is otherwise not in accordance with the grammar that was
	 *             passed into the constructor)
	 */
	public Node parse(String data, boolean retainIntermediateNodes) {
		State state = new State(data);
		Node node = start.process(state);
		if (node == null || !state.getUnprocessedData().isEmpty())
			throw new ParsingException("Could not process all tokens.");
		if (!retainIntermediateNodes)
			node.visit(new RemoveIntermediateNodes());
		return node;
	}
}
