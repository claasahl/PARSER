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
 * The class {@link NodeToString}. It is an implementation of the interface
 * {@link NodeVisitor}. It is intended to "visualize" a tree of {@link Node}
 * objects. The tree is turned into a human readable (if not "pretty") string.
 * <p>
 * This visitor is meant for one-time use, only. As such, every tree needs to be
 * visualized with a separate instance of this visitor. An instance of this
 * visitor should not be used to visualize multiple trees.
 * 
 * @author Claas Ahlrichs
 */
public class NodeToString implements NodeVisitor {

	private static final String DEFAULT_LEVEL_SEPARATOR = "  ";
	private static final String DEFAULT_LINE_NEWLINE = "\r\n";
	private final StringBuilder builder = new StringBuilder();
	private final AtomicInteger levels = new AtomicInteger();
	private final Set<Node> visitedPath = new HashSet<>();
	private final String levelSeparator;
	private final String lineSeparator;

	/**
	 * Constructs a new {@link NodeToString} with default parameters. Calling
	 * this constructor is equivalent to calling
	 * <code>{@link #NodeToString(String, String)}</code> with
	 * {@value #DEFAULT_LEVEL_SEPARATOR} as default level separator and the
	 * system's line separator (property {@literal line.separator}).
	 */
	public NodeToString() {
		this(DEFAULT_LEVEL_SEPARATOR, System.getProperty("line.separator", DEFAULT_LINE_NEWLINE));
	}

	/**
	 * Constructs a new {@link NodeToString} with the specified parameters. The
	 * level separator is prefixed to every stringified (i.e. turned into a
	 * string) {@link Node} object and signified the node's depth within the
	 * tree. The line separator is appended to every stringified {@link Node}
	 * object.
	 * 
	 * @param levelSeparator
	 *            the level separator
	 * @param lineSeparator
	 *            the line separator
	 */
	public NodeToString(String levelSeparator, String lineSeparator) {
		this.levelSeparator = levelSeparator;
		this.lineSeparator = lineSeparator;
	}

	@Override
	public void visitTerminalNode(TerminalNode node) {
		appendNode(node, node.getTerminal());
	}

	@Override
	public void visitIntermediateNode(IntermediateNode node) {
		appendNode(node, null);
		if (this.visitedPath.add(node)) {
			incrementLevel();
			for (Node n : node) {
				n.visit(this);
			}
			decrementLevel();
			this.visitedPath.remove(node);
		}
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		appendNode(node, node.getName());
		if (this.visitedPath.add(node)) {
			incrementLevel();
			for (Node n : node) {
				n.visit(this);
			}
			decrementLevel();
			this.visitedPath.remove(node);
		}
	}

	/**
	 * Increments the level / indentation for the next node.
	 */
	private void incrementLevel() {
		this.levels.incrementAndGet();
	}

	/**
	 * Decrements the level / indentation for the next node.
	 */
	private void decrementLevel() {
		this.levels.decrementAndGet();
	}

	/**
	 * Appends the specified node. The node will occupy a separate line and use
	 * the correct indentation / spacing (according to its level within the
	 * tree). The node will be represented by its (simple) class name and an
	 * optional postfix.
	 * 
	 * @param node
	 *            the node
	 * @param postfix
	 *            the postfix
	 */
	private void appendNode(Node node, String postfix) {
		this.builder.append(new String(new byte[this.levels.get()]).replaceAll("\0", this.levelSeparator));
		this.builder.append(node.getClass().getSimpleName());
		if (postfix != null) {
			this.builder.append(":");
			this.builder.append(postfix);
		}
		this.builder.append(this.lineSeparator);
	}

	@Override
	public String toString() {
		return this.builder.toString();
	}
}
