package de.claas.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.claas.parser.exceptions.ParsingException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.visitors.RemoveIntermediateNodes;
import de.claas.parser.visitors.ExtractTerminals;

/**
 * 
 * The class {@link Grammar}. It is intended to parse sentences of a given
 * grammar. This class takes a set of {@link NonTerminal} rules (i.e. named
 * rules) and uses them to parse sentences. The result is returned as a tree of
 * {@link Node} instances.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Grammar {

	private final NonTerminal start;

	/**
	 * Creates an instance with the given parameter.
	 * 
	 * @param grammar
	 *            the grammar
	 */
	public Grammar(NonTerminal grammar) {
		this.start = grammar;
	}

	/**
	 * Parses and returns the tree of terminals and non-terminals that
	 * represents the given pattern. Any intermediate nodes (and thus
	 * non-essential nodes) are removed. Any preceding and trailing whitespace
	 * are removed as well. If the given pattern is invalid, then a
	 * {@link ParsingException} is thrown.
	 * <p>
	 * Calling this method is equivalent <code>parse(data, false, false)</code>
	 * (see {@link #parse(String, boolean, boolean)}).
	 * 
	 * @param data
	 *            the pattern
	 * @return the tree of terminals and non-terminals that represents the given
	 *         pattern
	 * @throws ParsingException
	 *             if the pattern is invalid (e.g. contains illegal tokens or
	 *             the pattern is otherwise not in accordance with the grammar
	 *             that was passed into the constructor)
	 */
	public Node parse(String data) throws ParsingException {
		return parse(data, false, false);
	}

	/**
	 * Parses and returns the tree of terminals and non-terminals that
	 * represents the given pattern. Optionally intermediate nodes (and thus
	 * non-essential nodes) can be removed. Also optionally, preceding and
	 * trailing whitespace can be removed. If the given pattern is invalid, then
	 * a {@link ParsingException} is thrown.
	 * 
	 * @param data
	 *            the pattern
	 * @param retainIntermediateNodes
	 *            whether {@link IntermediateNode} instances should be retained.
	 *            Set to <code>false</code> if only non-terminal and terminal
	 *            nodes are desired
	 * @param retainWhitespace
	 *            whether preceding and trailing whitespace should be retained.
	 *            Set to <code>false</code> if whitespace is to be removed
	 * @return the tree of terminals and non-terminals that represents the given
	 *         pattern
	 * @throws ParsingException
	 *             if the pattern is invalid (e.g. contains illegal tokens or
	 *             the pattern is otherwise not in accordance with the grammar
	 *             that was passed into the constructor)
	 */
	public Node parse(String data, boolean retainIntermediateNodes, boolean retainWhitespace) throws ParsingException {
		ExtractTerminals visitor = new ExtractTerminals();
		start.visit(visitor);
		List<String> tokenized = tokenize(visitor.getTerminals(), data, retainWhitespace);
		Stack<String> tokens = new Stack<>();
		for (int i = tokenized.size() - 1; i >= 0; i--) {
			tokens.push(tokenized.get(i));
		}
		State state = new State(tokens);
		Node node = start.process(state);
		if (state.getUnprocessedTokens() > 0)
			throw new ParsingException("Could not process all tokens.");
		if (!retainIntermediateNodes)
			node.visit(new RemoveIntermediateNodes());
		return node;
	}

	/**
	 * Returns the tokens that make up the given pattern. The pattern needs to
	 * be in accordance with the grammar described in above. Otherwise a
	 * {@link ParsingException} is thrown.
	 * <p>
	 * This function is sensitive to upper and lower case characters. Tokens may
	 * be separated by white space, but do not need to be separated by it. Any
	 * preceding and trailing whitespace can be removed during tokenization.
	 * 
	 * @param terminals
	 *            the terminals (i.e. valid tokens)
	 * @param pattern
	 *            the pattern
	 * @param retainWhitespaces
	 *            whether preceding and trailing whitespace should be retained.
	 *            Set to <code>false</code> if whitespace is to be removed
	 * 
	 * @return the tokens that make up the given pattern
	 * @throws ParsingException
	 *             if the pattern is invalid (i.e. contains illegal tokens)
	 */
	private static List<String> tokenize(List<String> terminals, String pattern, boolean retainWhitespace)
			throws ParsingException {
		List<String> tokens = new ArrayList<>();
		String token = retainWhitespace ? pattern : pattern.trim();
		do {
			// look for tokens ...
			boolean foundValidToken = false;
			for (String validToken : terminals) {
				foundValidToken = token.startsWith(validToken);
				if (foundValidToken) {
					tokens.add(validToken);
					if(retainWhitespace)
						token = token.substring(validToken.length());
					else
						token = token.substring(validToken.length()).trim();
					break;
				}
			}

			// ... and handle unknown tokens
			if (!foundValidToken)
				throw new ParsingException("unknown token '" + token + "'");
		} while (!token.isEmpty());
		return tokens;
	}
}
