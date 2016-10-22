package de.claas.parser.visitors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.Rule;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link NodeEquality}. It is an implementation of the interface
 * {@link NodeVisitor}. It is intended to compare a {@link Node}-hierarchy with
 * a reference object.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to compare multiple {@link Rule}s.
 * 
 * @author Claas Ahlrichs
 *
 */
public class NodeEquality implements NodeVisitor {

	private final Set<Integer> visitedPath = new HashSet<>();
	private Object obj;
	private boolean visited = false;
	private boolean equality = true;

	/**
	 * Constructs a new {@link NodeEquality} with the specified parameter.
	 * 
	 * @param obj
	 *            the reference object with which the visited {@link Node}s are
	 *            compared
	 */
	public NodeEquality(Object obj) {
		this.obj = obj;
	}

	@Override
	public void visitTerminalNode(TerminalNode node) {
		markAsVisited();
		if (preliminaryComparison(node, this.obj))
			return;

		TerminalNode other = (TerminalNode) this.obj;
		if (isUnequal(node.getTerminal(), other.getTerminal()))
			return; // already marked as unequal
	}

	@Override
	public void visitIntermediateNode(IntermediateNode node) {
		markAsVisited();
		if (preliminaryComparison(node, this.obj))
			return;

		IntermediateNode other = (IntermediateNode) this.obj;
		Integer uniqueId = new Integer(System.identityHashCode(node));
		if (this.visitedPath.add(uniqueId)) {
			visitChildren(node, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		markAsVisited();
		if (preliminaryComparison(node, this.obj))
			return;

		NonTerminalNode other = (NonTerminalNode) this.obj;
		if (isUnequal(node.getName(), other.getName()))
			return; // already marked as unequal
		Integer uniqueId = new Integer(System.identityHashCode(node));
		if (this.visitedPath.add(uniqueId)) {
			visitChildren(node, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	/**
	 * Marks the two rules as unequal.
	 */
	private void markAsUnequal() {
		this.equality = false;
	}

	/**
	 * Marks this visitor as visited. Be default it is assumed that two nodes
	 * are equal, unless proven otherwise. However, this assumption required the
	 * visitor to be visited (otherwise any two nodes would be assumed to be
	 * equal).
	 */
	private void markAsVisited() {
		this.visited = true;
	}

	/**
	 * Returns <code>true</code> if the two objects can already be said to be
	 * equal (or unequal). Otherwise, <code>false</code> is returned.
	 * <p>
	 * <b>Side effect</b>: this method may call {@link #markAsUnequal()}
	 * 
	 * @param node
	 *            the original node
	 * @param other
	 *            the reference node with which the original node is compared
	 * @return <code>true</code> if the two object can already be said to be
	 *         equal (or unequal). Otherwise, <code>false</code> is returned
	 */
	private boolean preliminaryComparison(Node node, Object other) {
		if (node == other)
			return true; // "this.equality" is already "true"
		if (other == null || node.getClass() != other.getClass()) {
			markAsUnequal();
			return true;
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if the two objects can be said to be unequal.
	 * Otherwise, <code>false</code> is returned.
	 * <p>
	 * <b>Side effect</b>: this method may call {@link #markAsUnequal()}
	 * 
	 * @param original
	 *            the original object (e.g. name of node, terminal symbol, etc.)
	 * @param other
	 *            the reference object with which the original object is
	 *            compared
	 * @return <code>true</code> if the two objects can be said to be unequal.
	 *         Otherwise, <code>false</code> is returned.
	 */
	private boolean isUnequal(Object original, Object other) {
		if (original == null) {
			if (other != null) {
				markAsUnequal();
				return true;
			}
		} else if (!original.equals(other)) {
			markAsUnequal();
			return true;
		}
		return false;
	}

	/**
	 * A helper function that successively visits all children of both specified
	 * {@link Node}s. It is main purpose is to ensure that the order in which
	 * the children occur is identical and that the children themselves are
	 * equal as well.
	 * 
	 * @param node
	 *            the original node
	 * @param other
	 *            the reference node with which the original node is compared
	 */
	private void visitChildren(Node node, Node other) {
		Iterator<Node> children = node.iterator();
		Iterator<Node> otherChildren = other.iterator();
		while (children.hasNext() && otherChildren.hasNext()) {
			Node child = children.next();
			this.obj = otherChildren.next();
			child.visit(this);

			if (!this.equality)
				return;
		}
		this.equality = children.hasNext() == otherChildren.hasNext();
	}

	/**
	 * Returns <code>true</code> if the visited nodes represent the same object
	 * that was passed into the constructor of this visitor. Otherwise
	 * <code>false</code> is returned.
	 * 
	 * @return <code>true</code> if the visited nodes represent the same object
	 *         that was passed into the constructor of this visitor,
	 *         <code>false</code> other
	 */
	public boolean isEquality() {
		return this.equality && this.visited;
	}

}
