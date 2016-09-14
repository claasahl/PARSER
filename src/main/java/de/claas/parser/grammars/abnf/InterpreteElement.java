package de.claas.parser.grammars.abnf;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link InterpreteElement}. It is intended to interpret "element"
 * rules of the {@link AugmentedBackusNaur} grammar. This interpreter verifies
 * the overall structure of the node-tree being visited (i.e.
 * <code>rulename / group / option / char-val / num-val / prose-val</code>).
 * <p>
 * In case of an error, an {@link InterpretingException} is thrown.
 * 
 * @see AugmentedBackusNaur
 * 
 * @author Claas Ahlrichs
 *
 */
public class InterpreteElement extends InterpreterBase {

	private final StringBuilder content = new StringBuilder();

	/**
	 * Constructs a new {@link InterpreteElement} with default parameters.
	 */
	public InterpreteElement() {
		super("element");
	}

	@Override
	protected void visitingTerminalNode(TerminalNode node) {
		content.append(node.getTerminal());
	}

	@Override
	protected void visitingNonTerminalNode(NonTerminalNode node) {
		switch (node.getName().toLowerCase()) {
		case "element":
			try {
				expect(this::expectElements);
				for (Node child : node) {
					child.visit(this);
					expectNothing();
				}
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
		case "char-val":
			InterpreteCharVal charVal = new InterpreteCharVal();
			node.visit(charVal);
			setRule(charVal.getRule());
			break;
		case "group":
		case "option":
		case "num-val":
		case "prose-val":
		default:
			String msg = "Unexpected non-terminal: '%s'";
			throw new InterpretingException(String.format(msg, node.getName()));
		}
	}

	/**
	 * Tests if the specified node is a {@link NonTerminalNode} with the
	 * expected name (i.e. <code>rulename</code>, <code>group</code>,
	 * <code>option</code>, <code>char-val</code>, <code>num-val</code> or
	 * <code>prose-val</code>).
	 * 
	 * @param node
	 *            the node
	 * @return <code>true</code> if the specified node is a
	 *         {@link NonTerminalNode} with the expected name (i.e.
	 *         <code>rulename</code>, <code>group</code>, <code>option</code>,
	 *         <code>char-val</code>, <code>num-val</code> or
	 *         <code>prose-val</code>), <code>false</code> otherwise
	 */
	private boolean expectElements(Node node) {
		if (node != null && node.getClass().isAssignableFrom(NonTerminalNode.class)) {
			String name = ((NonTerminalNode) node).getName();
			return "rulename".equalsIgnoreCase(name) || "group".equalsIgnoreCase(name)
					|| "option".equalsIgnoreCase(name) || "char-val".equalsIgnoreCase(name)
					|| "num-val".equalsIgnoreCase(name) || "prose-val".equalsIgnoreCase(name);
		} else if (node != null && node.getClass().isAssignableFrom(TerminalNode.class)) {
			return true;
		} else {
			return false;
		}
	}

}
