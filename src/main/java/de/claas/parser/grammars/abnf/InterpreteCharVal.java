package de.claas.parser.grammars.abnf;

import java.util.Iterator;

import de.claas.parser.Node;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The class {@link InterpreteCharVal}. It is intended to interpret "char-val"
 * rules of the {@link AugmentedBackusNaur} grammar. This interpreter verifies
 * the overall structure of the node-tree being visited (i.e.
 * <code>DQUOTE *(%x20-21 / %x23-7E) DQUOTE</code>). Any terminal symbols are
 * assumed to be correct (i.e. the content between the quotes is not
 * double-checked).
 * <p>
 * In case of an error, an {@link InterpretingException} is thrown.
 * 
 * @see AugmentedBackusNaur
 * 
 * @author Claas Ahlrichs
 *
 */
public class InterpreteCharVal extends InterpreterBase {

	private final StringBuilder content = new StringBuilder();

	/**
	 * Constructs a new {@link InterpreteCharVal} with default parameters.
	 */
	public InterpreteCharVal() {
		super("char-val");
	}

	@Override
	protected void visitingTerminalNode(TerminalNode node) {
		content.append(node.getTerminal());
	}

	@Override
	protected void visitingNonTerminalNode(NonTerminalNode node) {
		switch (node.getName().toLowerCase()) {
		case "char-val":
			try {
				// first child should be a double quote
				Iterator<Node> iterator = node.iterator();
				expectNonTerminalNode("dquote");
				iterator.next().visit(this);

				// at least one terminal node should follow
				expectTerminalNode();
				Node child = null;
				do {
					child = iterator.next();
					if (iterator.hasNext()) {
						child.visit(this);
					}
				} while (iterator.hasNext());

				// the last child should be a double quote
				expectNonTerminalNode("dquote");
				child.visit(this);

				// construct rule
				setRule(new Terminal(content.toString()));
			} catch (Exception e) {
				String msg = "Unexpected structure / order of non-terminals";
				throw new InterpretingException(msg, e);
			}
			break;
		case "dquote":
			// quotes are not part of the content
			break;
		default:
			String msg = "Unexpected non-terminal: '%s'";
			throw new InterpretingException(String.format(msg, node.getName()));
		}
	}

}
