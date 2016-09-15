package de.claas.parser.grammars.abnf;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link InterpreteRulelist}. It is intended to interpret "rulelist"
 * rules of the {@link AugmentedBackusNaur} grammar. This interpreter verifies
 * the overall structure of the node-tree being visited (i.e.
 * <code>rulename defined-as elements c-nl</code>).
 * <p>
 * In case of an error, an {@link InterpretingException} is thrown.
 * 
 * @see AugmentedBackusNaur
 * 
 * @author Claas Ahlrichs
 *
 */
public class InterpreteRulelist extends InterpreterBase {

	private final StringBuilder content = new StringBuilder();

	/**
	 * Constructs a new {@link InterpreteRulelist} with default parameters.
	 */
	public InterpreteRulelist() {
		super("rulelist");
	}

	@Override
	protected void visitingTerminalNode(TerminalNode node) {
		content.append(node.getTerminal());
	}

	@Override
	protected void visitingNonTerminalNode(NonTerminalNode node) {
		switch (node.getName().toLowerCase()) {
		case "rulelist":
			try {
				expectNonTerminalNode("rule");
				for (Node child : node) {
					child.visit(this);
					expectNothing();
				}
			} catch (Exception e) {
				String msg = "Unexpected structure / order of non-terminals";
				throw new InterpretingException(msg, e);
			}
			break;
		case "rule":
			InterpreteRule rule = new InterpreteRule();
			node.visit(rule);
			setRule(rule.getRule());
			break;
		case "c-wsp":
		case "c-nl":
			// whitespace are not part of the content
			break;
		default:
			String msg = "Unexpected non-terminal: '%s'";
			throw new InterpretingException(String.format(msg, node.getName()));
		}
	}

}
