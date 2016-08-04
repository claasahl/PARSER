package de.claas.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Terminal;

/**
 * 
 * Superclass of all nodes within this package. This class is intended to model
 * nodes that represent parsed entities. In contrast to implementations of the
 * {@link Rule} class, implementations of this class do not describe abstract
 * concepts, such as words, phrases or sentences, but rather the concrete words,
 * phrases and sentences.
 * <p>
 * In other words, implementations of this class are a result of parsing
 * concrete sentences. While {@link Terminal} rules and {@link NonTerminal}
 * rules represent constraints on sentences, {@link TerminalNode} nodes and
 * {@link NonTerminalNode} nodes represent parts of a concrete sentence. The
 * hierarchy of nodes forms a tree structure, where the root element represents
 * a full (and concrete) sentence. Nodes at the very bottom of the tree (i.e.
 * leaf nodes) represent concrete tokens (or parts) of a sentence. Any non-leaf
 * nodes capture the context in which leaf nodes occurred.
 * <p>
 * The hierarchy of this class resembles the <i>composite</i> design pattern.
 * 
 * @author Claas Ahlrichs
 * 
 * @see Grammar#parse(String)
 *
 */
public abstract class Node implements Iterable<Node> {

	/**
	 * Internal list of children. This is not intended to be exposed for outside
	 * access. The {@link #iterator()} function already provides access to this
	 * list, but in a way that decouples the internal representation of children
	 * from the way outside classes access them.
	 */
	private final List<Node> children = new ArrayList<>();

	/**
	 * Adds the given (child) node. Returns <code>true</code> if the child was
	 * successfully added. Otherwise <code>false</code> is returned.
	 * 
	 * @param node
	 *            the (child) node
	 * @return <code>true</code> if the child was successfully added.
	 *         <code>false</code> otherwise
	 */
	public boolean addChild(Node node) {
		return node != null ? children.add(node) : false;
	}

	/**
	 * Removes the given (child) node. Returns <code>true</code> if the child
	 * was successfully removed. Otherwise <code>false</code> is returned.
	 * 
	 * @param node
	 *            the (child) node
	 * @return <code>true</code> if the child was successfully removed.
	 *         <code>false</code> otherwise
	 */
	public boolean removeChild(Node node) {
		return children.remove(node);
	}

	/**
	 * Returns <code>true</code> if this node has children. Otherwise
	 * <code>false</code> is returned (i.e. number of children is zero).
	 * 
	 * @return <code>true</code> if this node has children. <code>false</code>
	 *         otherwise
	 */
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public Iterator<Node> iterator() {
		return children.iterator();
	}

	/**
	 * Instructs this node to visit the given {@link NodeVisitor} instance.
	 *
	 * @param visitor
	 *            the visitor
	 */
	public abstract void visit(NodeVisitor visitor);

}
