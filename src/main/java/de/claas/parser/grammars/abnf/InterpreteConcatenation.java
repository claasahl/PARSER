package de.claas.parser.grammars.abnf;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link InterpreteConcatenation}. It is intended to interpret
 * "concatenation" rules of the {@link AugmentedBackusNaur} grammar. This
 * interpreter verifies the overall structure of the node-tree being visited
 * (i.e. <code>repetition *(1*c-wsp repetition)</code>).
 * <p>
 * In case of an error, an {@link InterpretingException} is thrown.
 * 
 * @see AugmentedBackusNaur
 * 
 * @author Claas Ahlrichs
 *
 */
public class InterpreteConcatenation extends InterpreterBase {

	private final StringBuilder content = new StringBuilder();

	/**
	 * Constructs a new {@link InterpreteConcatenation} with default parameters.
	 */
	public InterpreteConcatenation() {
		super("concatenation");
	}

	@Override
	protected void visitingTerminalNode(TerminalNode node) {
		content.append(node.getTerminal());
	}

	@Override
	protected void visitingNonTerminalNode(NonTerminalNode node) {
		switch (node.getName().toLowerCase()) {
		case "concatenation":
			try {
				expectNonTerminalNode("repetition");
				for (Node child : node) {
					child.visit(this);
					expectNothing();
				}
			} catch (Exception e) {
				String msg = "Unexpected structure / order of non-terminals";
				throw new InterpretingException(msg, e);
			}
			break;
		case "repetition":
			InterpreteRepetition repitition = new InterpreteRepetition();
			node.visit(repitition);
			setRule(repitition.getRule());
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
