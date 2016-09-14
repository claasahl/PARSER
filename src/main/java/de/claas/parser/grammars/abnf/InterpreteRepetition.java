package de.claas.parser.grammars.abnf;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link InterpreteRepetition}. It is intended to interpret
 * "repetition" rules of the {@link AugmentedBackusNaur} grammar. This
 * interpreter verifies the overall structure of the node-tree being visited
 * (i.e. <code>[repeat] element</code>).
 * <p>
 * In case of an error, an {@link InterpretingException} is thrown.
 * 
 * @see AugmentedBackusNaur
 * 
 * @author Claas Ahlrichs
 *
 */
public class InterpreteRepetition extends InterpreterBase {

	private final StringBuilder content = new StringBuilder();

	/**
	 * Constructs a new {@link InterpreteRepetition} with default parameters.
	 */
	public InterpreteRepetition() {
		super("repetition");
	}

	@Override
	protected void visitingTerminalNode(TerminalNode node) {
		content.append(node.getTerminal());
	}

	@Override
	protected void visitingNonTerminalNode(NonTerminalNode node) {
		switch (node.getName().toLowerCase()) {
		case "repetition":
			try {
				expectNonTerminalNode("element");
				for (Node child : node) {
					child.visit(this);
					expectNothing();
				}
			} catch (Exception e) {
				String msg = "Unexpected structure / order of non-terminals";
				throw new InterpretingException(msg, e);
			}
			break;
		case "element":
			InterpreteElement element = new InterpreteElement();
			node.visit(element);
			setRule(element.getRule());
			break;
		case "repeat":
		default:
			String msg = "Unexpected non-terminal: '%s'";
			throw new InterpretingException(String.format(msg, node.getName()));
		}
	}

}
