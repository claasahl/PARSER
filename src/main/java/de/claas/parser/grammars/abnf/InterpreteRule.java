package de.claas.parser.grammars.abnf;

import java.util.Iterator;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The class {@link InterpreteRule}. It is intended to interpret "rule" rules of
 * the {@link AugmentedBackusNaur} grammar. This interpreter verifies the
 * overall structure of the node-tree being visited (i.e.
 * <code>rulename defined-as elements c-nl</code>).
 * <p>
 * In case of an error, an {@link InterpretingException} is thrown.
 * 
 * @see AugmentedBackusNaur
 * 
 * @author Claas Ahlrichs
 *
 */
public class InterpreteRule extends InterpreterBase {

	private final StringBuilder content = new StringBuilder();

	/**
	 * Constructs a new {@link InterpreteRule} with default parameters.
	 */
	public InterpreteRule() {
		super("rule");
	}

	@Override
	protected void visitingTerminalNode(TerminalNode node) {
		content.append(node.getTerminal());
	}

	@Override
	protected void visitingNonTerminalNode(NonTerminalNode node) {
		switch (node.getName().toLowerCase()) {
		case "rule":
			try {
				Iterator<Node> iterator = node.iterator();

				expectNonTerminalNode("rulename");
				iterator.next().visit(this);
				expectNonTerminalNode("defined-as");
				iterator.next().visit(this);
				expectNonTerminalNode("elements");
				iterator.next().visit(this);
				expectNonTerminalNode("c-nl");
				iterator.next().visit(this);

				// construct rule
				setRule(new Terminal(content.toString()));
			} catch (Exception e) {
				String msg = "Unexpected structure / order of non-terminals";
				throw new InterpretingException(msg, e);
			}
			break;
		case "rulename":
			InterpreteRulename rulename = new InterpreteRulename();
			node.visit(rulename);
			setRule(rulename.getRule());
			break;
		case "defined-as":
			InterpreteDefinedAs definedAs = new InterpreteDefinedAs();
			node.visit(definedAs);
			setRule(definedAs.getRule());
			break;
		case "elements":
			InterpreteElements elements = new InterpreteElements();
			node.visit(elements);
			setRule(elements.getRule());
			break;
		case "c-nl":
			// whitespace are not part of the content
			break;
		default:
			String msg = "Unexpected non-terminal: '%s'";
			throw new InterpretingException(String.format(msg, node.getName()));
		}
	}

}
