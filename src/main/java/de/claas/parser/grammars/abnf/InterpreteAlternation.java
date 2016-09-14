package de.claas.parser.grammars.abnf;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link InterpreteAlternation}. It is intended to interpret
 * "alternation" rules of the {@link AugmentedBackusNaur} grammar. This
 * interpreter verifies the overall structure of the node-tree being visited
 * (i.e. <code>concatenation *(*c-wsp "/" *c-wsp concatenation)</code>).
 * <p>
 * In case of an error, an {@link InterpretingException} is thrown.
 * 
 * @see AugmentedBackusNaur
 * 
 * @author Claas Ahlrichs
 *
 */
public class InterpreteAlternation extends InterpreterBase {

	private final StringBuilder content = new StringBuilder();

	/**
	 * Constructs a new {@link InterpreteAlternation} with default parameters.
	 */
	public InterpreteAlternation() {
		super("alternation");
	}

	@Override
	protected void visitingTerminalNode(TerminalNode node) {
		content.append(node.getTerminal());
	}

	@Override
	protected void visitingNonTerminalNode(NonTerminalNode node) {
		switch (node.getName().toLowerCase()) {
		case "alternation":
			try {
				expectNonTerminalNode("concatenation");
				for (Node child : node) {
					child.visit(this);
					expectNothing();
				}
			} catch (Exception e) {
				String msg = "Unexpected structure / order of non-terminals";
				throw new InterpretingException(msg, e);
			}
			break;
		case "concatenation":
			InterpreteConcatenation concatenation = new InterpreteConcatenation();
			node.visit(concatenation);
			setRule(concatenation.getRule());
			break;
		case "c-wsp":
			// whitespace is ignored
			break;
		default:
			String msg = "Unexpected non-terminal: '%s'";
			throw new InterpretingException(String.format(msg, node.getName()));
		}
	}

}
