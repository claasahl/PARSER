package de.claas.parser.grammars.abnf;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link InterpreteElements}. It is intended to interpret "elements"
 * rules of the {@link AugmentedBackusNaur} grammar. This interpreter verifies
 * the overall structure of the node-tree being visited (i.e.
 * <code>alternation *c-wsp</code>).
 * <p>
 * In case of an error, an {@link InterpretingException} is thrown.
 * 
 * @see AugmentedBackusNaur
 * 
 * @author Claas Ahlrichs
 *
 */
public class InterpreteElements extends InterpreterBase {

	private final StringBuilder content = new StringBuilder();

	/**
	 * Constructs a new {@link InterpreteElements} with default parameters.
	 */
	public InterpreteElements() {
		super("elements");
	}

	@Override
	protected void visitingTerminalNode(TerminalNode node) {
		content.append(node.getTerminal());
	}

	@Override
	protected void visitingNonTerminalNode(NonTerminalNode node) {
		switch (node.getName().toLowerCase()) {
		case "elements":
			try {
				expectNonTerminalNode("alternation");
				for (Node child : node) {
					child.visit(this);
					expectNothing();
				}
			} catch (Exception e) {
				String msg = "Unexpected structure / order of non-terminals";
				throw new InterpretingException(msg, e);
			}
			break;
		case "alternation":
			InterpreteAlternation alternation = new InterpreteAlternation();
			node.visit(alternation);
			setRule(alternation.getRule());
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
