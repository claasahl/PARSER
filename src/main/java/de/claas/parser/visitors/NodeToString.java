package de.claas.parser.visitors;

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
 * objects. The tree is turned into a
 * 
 * @author Claas Ahlrichs
 *
 */
public class NodeToString implements NodeVisitor {

	private static final String SEPARATOR = "-";
	private static final String NEWLINE = "\n";
	private final StringBuilder builder = new StringBuilder();
	private final AtomicInteger indents = new AtomicInteger();

	@Override
	public void visitTerminalNode(TerminalNode node) {
		appendNode(node, node.getTerminal());
	}

	@Override
	public void visitIntermediateNode(IntermediateNode node) {
		appendNode(node);
		incrementIndent();
		for (Node n : node) {
			n.visit(this);
		}
		decrementIndent();
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		appendNode(node, node.getName());
		incrementIndent();
		for (Node n : node) {
			n.visit(this);
		}
		decrementIndent();
	}

	private void incrementIndent() {
		indents.incrementAndGet();
	}

	private void decrementIndent() {
		indents.decrementAndGet();
	}

	private void appendNode(Node node, String... notes) {
		spacing();
		for (String note : notes) {
			builder.append(note);
			builder.append(SEPARATOR);
		}
		builder.append(node.getClass().getName());
		builder.append(NEWLINE);
	}

	private void spacing() {
		builder.append(new String(new byte[indents.get()]).replaceAll("\0", SEPARATOR));
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
