package de.claas.parser.grammars.abnf;

import java.util.Iterator;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The class {@link InterpreteRulename}. It is intended to interpret "rulename"
 * rules of the {@link AugmentedBackusNaur} grammar. This interpreter verifies
 * the overall structure of the node-tree being visited (i.e.
 * <code>ALPHA *(ALPHA / DIGIT / "-")</code>). Any terminal symbols are assumed
 * to be correct (i.e. the children of <code>ALPHA</code> and <code>DIGIT</code>
 * are not double-checked).
 * <p>
 * In case of an error, an {@link InterpretingException} is thrown.
 * 
 * @see AugmentedBackusNaur
 * 
 * @author Claas Ahlrichs
 *
 */
public class InterpreteRulename extends InterpreterBase {

	private final StringBuilder content = new StringBuilder();

	/**
	 * Constructs a new {@link InterpreteRulename} with default parameters.
	 */
	public InterpreteRulename() {
		super("rulename");
	}

	@Override
	protected void visitingTerminalNode(TerminalNode node) {
		content.append(node.getTerminal());
	}

	@Override
	protected void visitingNonTerminalNode(NonTerminalNode node) {
		switch (node.getName().toLowerCase()) {
		case "rulename":
			try {
				// first child should be a character (ALPHA)
				Iterator<Node> iterator = node.iterator();
				expectNonTerminalNode("alpha");
				iterator.next().visit(this);

				// any number of characters, digits and "-" should follow
				while (iterator.hasNext()) {
					expect(this::expectAlphaDigitDash);
					iterator.next().visit(this);
				}

				// construct rule
				setRule(new Terminal(content.toString()));
			} catch (Exception e) {
				String msg = "Unexpected structure / order of non-terminals";
				throw new InterpretingException(msg, e);
			}
			break;
		case "alpha":
		case "digit":
			for (Node child : node) {
				expectTerminalNode();
				child.visit(this);
			}
			break;
		default:
			String msg = "Unexpected non-terminal: '%s'";
			throw new InterpretingException(String.format(msg, node.getName()));
		}
	}

	/**
	 * Tests if the specified node is a {@link NonTerminalNode} with the
	 * expected name (i.e. <code>ALPHA</code> or <code>DIGIT</code>) or a
	 * {@link TerminalNode}.
	 * 
	 * @param node
	 *            the node
	 * @return <code>true</code> if the specified node is a
	 *         {@link NonTerminalNode} with the expected name (i.e.
	 *         <code>ALPHA</code> or <code>DIGIT</code>) or a
	 *         {@link TerminalNode}, <code>false</code> otherwise
	 */
	private boolean expectAlphaDigitDash(Node node) {
		if (node != null && node.getClass().isAssignableFrom(NonTerminalNode.class)) {
			String name = ((NonTerminalNode) node).getName();
			return "alpha".equalsIgnoreCase(name) || "digit".equalsIgnoreCase(name);
		} else if (node != null && node.getClass().isAssignableFrom(TerminalNode.class)) {
			return true;
		} else {
			return false;
		}
	}

}
