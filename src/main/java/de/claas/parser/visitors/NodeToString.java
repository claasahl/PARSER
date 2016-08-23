package de.claas.parser.visitors;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link NodeToString}. It is an implementation of the interface
 * {@link NodeVisitor}. It is intended to "visualize" a tree of {@link Node}
 * objects. The tree is turned into a human readable (if not "pretty") string.
 * <p>
 * Cyclic dependencies are silently ignored.
 * 
 * @author Claas Ahlrichs
 *
 */
public class NodeToString implements NodeVisitor {

	private static final String SEPARATOR = "-";
	private static final String NEWLINE = "\n";
	private final StringBuilder builder = new StringBuilder();
	private final AtomicInteger indents = new AtomicInteger();
	private final Set<Node> visitedNodes = new HashSet<>();

	@Override
	public void visitTerminalNode(TerminalNode node) {
		if (visitedNodes.add(node)) {
			appendNode(node, node.getTerminal());
		}
	}

	@Override
	public void visitIntermediateNode(IntermediateNode node) {
		if (visitedNodes.add(node)) {
			appendNode(node);
			incrementIndent();
			for (Node n : node) {
				n.visit(this);
			}
			decrementIndent();
		}
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		if (visitedNodes.add(node)) {
			appendNode(node, node.getName());
			incrementIndent();
			for (Node n : node) {
				n.visit(this);
			}
			decrementIndent();
		}
	}

	/**
	 * Increments the indentation for the next node.
	 */
	private void incrementIndent() {
		indents.incrementAndGet();
	}

	/**
	 * Decrements the indentation for the next node.
	 */
	private void decrementIndent() {
		indents.decrementAndGet();
	}

	/**
	 * Appends the specified node with its notes. The node will occupy a
	 * separate line and use the correct indentation / spacing.
	 * 
	 * @param node
	 *            the node
	 * @param notes
	 *            the notes
	 */
	private void appendNode(Node node, String... notes) {
		builder.append(new String(new byte[indents.get()]).replaceAll("\0", SEPARATOR));
		for (String note : notes) {
			builder.append(note);
			builder.append(SEPARATOR);
		}
		builder.append(node.getClass().getName());
		builder.append(NEWLINE);
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
