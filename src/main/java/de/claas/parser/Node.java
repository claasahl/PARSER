package de.claas.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Terminal;
import de.claas.parser.visitors.NodeEquality;
import de.claas.parser.visitors.NodeHashCode;

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
	 * Internal flag for signaling that the hash code needs to be updated. The
	 * actual update is done in a lazy fashion (i.e. hash code is updated the
	 * next time it is needed).
	 */
	private boolean invalidHashCode = true;

	/**
	 * Internally cached hash code. The hash code is kept in local storage for
	 * performance reasons. It will be updated in accordance with the
	 * {@link #invalidHashCode}-flag.
	 */
	private int hashCode;

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
		invalidateHashCode();
		return node != null ? this.children.add(node) : false;
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
		invalidateHashCode();
		return this.children.remove(node);
	}

	/**
	 * Returns <code>true</code> if this node has children. Otherwise
	 * <code>false</code> is returned (i.e. number of children is zero).
	 * 
	 * @return <code>true</code> if this node has children. <code>false</code>
	 *         otherwise
	 */
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	@Override
	public Iterator<Node> iterator() {
		return this.children.iterator();
	}

	/**
	 * Instructs this node to visit the given {@link NodeVisitor} instance.
	 *
	 * @param visitor
	 *            the visitor
	 */
	public abstract void visit(NodeVisitor visitor);

	/**
	 * Notifies this node that its hash code is invalid. The hash code will be
	 * lazily updated the time it is needed.
	 */
	protected void invalidateHashCode() {
		this.invalidHashCode = true;
	}

	@Override
	public int hashCode() {
		// it is acceptable that the hash code only changes if (local) fields
		// are changed. This hash code has no way of knowing whether any child
		// was modified, since this hash code was last updated, and does not
		// reflect any such changes in any of its children.
		if (this.invalidHashCode) {
			NodeHashCode visitor = new NodeHashCode();
			this.visit(visitor);
			this.hashCode = visitor.getHashCode();
			this.invalidHashCode = false;
		}
		return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		NodeEquality visitor = new NodeEquality(obj);
		this.visit(visitor);
		return visitor.isEquality();
	}

}
