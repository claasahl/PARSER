package de.claas.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * 
 * The class {@link Grammar}. It is intended to parse sentences of a given
 * grammar.
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
	 * non-essential nodes) are removed. If the given pattern is invalid, then a
	 * {@link ParsingException} is thrown.
	 * <p/>
	 * Calling this method is equivalent <code>parse(data, false)</code> (see
	 * {@link #parse(String, boolean)}).
	 * 
	 * @param data
	 *            the pattern
	 * @return the tree of terminals and non-terminals that represents the given
	 *         pattern.
	 * @throws ParsingException
	 *             if the pattern is invalid (e.g. contains illegal tokens or
	 *             the pattern is otherwise not in accordance with the grammar
	 *             that was passed into the constructor)
	 */
	public Node parse(String data) throws ParsingException {
		return parse(data, false);
	}

	/**
	 * Parses and returns the tree of terminals and non-terminals that
	 * represents the given pattern. Optionally intermediate nodes (and thus
	 * non-essential nodes) can be removed. If the given pattern is invalid,
	 * then a {@link ParsingException} is thrown
	 * 
	 * @param data
	 *            the pattern
	 * @param retainIntermediateNodes
	 *            whether {@link IntermediateNode} instances should be retained.
	 *            Set to <code>false</code> if only non-terminal and terminal
	 *            nodes are desired.
	 * @return the tree of terminals and non-terminals that represents the given
	 *         pattern.
	 * @throws ParsingException
	 *             if the pattern is invalid (e.g. contains illegal tokens or
	 *             the pattern is otherwise not in accordance with the grammar
	 *             that was passed into the constructor)
	 */
	public Node parse(String data, boolean retainIntermediateNodes) throws ParsingException {
		ExtractTerminals visitor = new ExtractTerminals();
		start.visit(visitor);
		List<String> tokenized = tokenize(visitor.getTerminals(), data);
		Stack<String> tokens = new Stack<>();
		for (int i = tokenized.size() - 1; i >= 0; i--) {
			tokens.push(tokenized.get(i));
		}
		State state = new State(tokens);
		Node node = start.process(state);
		if (state.getUnprocessedTokens() > 0)
			throw new ParsingException("Could not process all tokens.");
		if (!retainIntermediateNodes)
			node.visit(new CleanUpVisitor());
		return node;
	}

	/**
	 * Returns the tokens that make up the given pattern. The pattern needs to
	 * be in accordance with the grammar described in above. Otherwise a
	 * {@link ParsingException} is thrown.
	 * <p/>
	 * This function is sensitive to upper and lower case characters. Tokens may
	 * be separated by white space, but do not need to be separated by it. Any
	 * white space characters are removed during tokenization.
	 * 
	 * @param terminals
	 *            the terminals (i.e. valid tokens)
	 * @param pattern
	 *            the pattern
	 * 
	 * @return the tokens that make up the given pattern
	 * @throws ParsingException
	 *             if the pattern is invalid (i.e. contains illegal tokens)
	 */
	private static List<String> tokenize(List<String> terminals, String pattern) throws ParsingException {
		List<String> tokens = new ArrayList<>();
		String token = pattern.trim();
		do {
			// look for tokens ...
			boolean foundValidToken = false;
			for (String validToken : terminals) {
				foundValidToken = token.startsWith(validToken);
				if (foundValidToken) {
					tokens.add(validToken);
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

	/**
	 * 
	 * The class {@link ExtractTerminals}. It is an implementation of the
	 * {@link RuleVisitor} class. It is intended to collect and return the
	 * terminal symbols in grammars. These can than be used tokenize (e.g.
	 * {@link Grammar#tokenize(List, String)}) sentences of any grammar.
	 * 
	 * @author Claas Ahlrichs
	 *
	 */
	private static class ExtractTerminals implements RuleVisitor {

		private Set<String> terminals = new HashSet<>();
		private Set<Rule> visited = new HashSet<>();

		/**
		 * Returns the terminal symbols in reversed alphabetical order. If this
		 * visitor is used to visit multiple grammars, then the terminal symbols
		 * of all grammars are returned.
		 * 
		 * @return the terminal symbols in reversed alphabetical order
		 */
		public List<String> getTerminals() {
			ArrayList<String> bla = new ArrayList<>(terminals);
			bla.sort((String s1, String s2) -> -s1.compareToIgnoreCase(s2));
			return bla;
		}

		@Override
		public void visitConjunction(Conjunction rule) {
			Iterator<Rule> iterator = rule.iterator();
			while (iterator.hasNext()) {
				Rule child = iterator.next();
				if (!visited.contains(child)) {
					visited.add(child);
					child.visit(this);
				}
			}
		}

		@Override
		public void visitDisjunction(Disjunction rule) {
			Iterator<Rule> iterator = rule.iterator();
			while (iterator.hasNext()) {
				Rule child = iterator.next();
				if (!visited.contains(child)) {
					visited.add(child);
					child.visit(this);
				}
			}
		}

		@Override
		public void visitNonTerminal(NonTerminal rule) {
			Rule child = rule.getRule();
			if (!visited.contains(child)) {
				visited.add(child);
				child.visit(this);
			}
		}

		@Override
		public void visitOptional(Optional rule) {
			Rule child = rule.getRule();
			if (!visited.contains(child)) {
				visited.add(child);
				child.visit(this);
			}
		}

		@Override
		public void visitRepetition(Repetition rule) {
			Rule child = rule.getRule();
			if (!visited.contains(child)) {
				visited.add(child);
				child.visit(this);
			}
		}

		@Override
		public void visitTerminal(Terminal rule) {
			Iterator<String> iterator = rule.getTerminals();
			while (iterator.hasNext()) {
				terminals.add(iterator.next());
			}
		}

	}

}
