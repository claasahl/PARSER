package de.claas.parser.grammars.abnf;

import java.util.Iterator;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The class {@link InterpreteDefinedAs}. It is intended to interpret
 * "defined-as" rules of the {@link AugmentedBackusNaur} grammar. This
 * interpreter verifies the overall structure of the node-tree being visited
 * (i.e. <code>*c-wsp ("=" / "=/") *c-wsp</code>). Any terminal symbols are
 * assumed to be correct. Leading and / trailing whitespace are ignored.
 * <p>
 * In case of an error, an {@link InterpretingException} is thrown.
 * 
 * @see AugmentedBackusNaur
 * 
 * @author Claas Ahlrichs
 *
 */
public class InterpreteDefinedAs extends InterpreterBase {

	private final StringBuilder content = new StringBuilder();

	/**
	 * Constructs a new {@link InterpreteDefinedAs} with default parameters.
	 */
	public InterpreteDefinedAs() {
		super("defined-as");
	}

	@Override
	protected void visitingTerminalNode(TerminalNode node) {
		content.append(node.getTerminal());
		expectNonTerminalNode("c-wsp");
	}

	@Override
	protected void visitingNonTerminalNode(NonTerminalNode node) {
		switch (node.getName().toLowerCase()) {
		case "defined-as":
			try {
				Iterator<Node> iterator = node.iterator();
				expect(this::expectWhitespaceOrTerminal);
				while (iterator.hasNext()) {
					iterator.next().visit(this);
				}
				setRule(new Terminal(content.toString()));
			} catch (Exception e) {
				String msg = "Unexpected structure / order of non-terminals";
				throw new InterpretingException(msg, e);
			}
			break;
		case "c-wsp":
			// whitespace are not part of the content
			break;
		default:
			String msg = "Unexpected non-terminal: '%s'";
			throw new InterpretingException(String.format(msg, node.getName()));
		}
	}

	/**
	 * Tests if the specified node is a {@link NonTerminalNode} with the
	 * expected name (i.e. <code>c-wsp</code>) or a {@link TerminalNode}.
	 * 
	 * @param node
	 *            the node
	 * @return <code>true</code> if the specified node is a
	 *         {@link NonTerminalNode} with the expected name (i.e.
	 *         <code>c-wsp</code>) or a {@link TerminalNode}, <code>false</code>
	 *         otherwise
	 */
	private boolean expectWhitespaceOrTerminal(Node node) {
		if (node != null && node.getClass().isAssignableFrom(NonTerminalNode.class)) {
			return "c-wsp".equalsIgnoreCase(((NonTerminalNode) node).getName());
		} else if (node != null && node.getClass().isAssignableFrom(TerminalNode.class)) {
			return true;
		} else {
			return false;
		}
	}

}
