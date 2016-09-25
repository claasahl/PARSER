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
		if (node == this.obj)
			return;
		if (this.obj == null || node.getClass() != this.obj.getClass()) {
			this.equality = false;
			return;
		}
		TerminalNode other = (TerminalNode) this.obj;
		if (node.getTerminal() == null) {
			if (other.getTerminal() != null) {
				this.equality = false;
				return;
			}
		} else if (!node.getTerminal().equals(other.getTerminal())) {
			this.equality = false;
			return;
		}
	}

	@Override
	public void visitIntermediateNode(IntermediateNode node) {
		if (node == this.obj)
			return;
		if (this.obj == null || node.getClass() != this.obj.getClass()) {
			this.equality = false;
			return;
		}

		IntermediateNode other = (IntermediateNode) this.obj;
		Integer uniqueId = new Integer(System.identityHashCode(node));
		if (this.visitedPath.add(uniqueId)) {
			visitChildren(node, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		if (node == this.obj)
			return;
		if (this.obj == null || node.getClass() != this.obj.getClass()) {
			this.equality = false;
			return;
		}

		NonTerminalNode other = (NonTerminalNode) this.obj;
		if (node.getName() == null) {
			if (other.getName() != null) {
				this.equality = false;
				return;
			}
		} else if (!node.getName().equals(other.getName())) {
			this.equality = false;
			return;
		}
		Integer uniqueId = new Integer(System.identityHashCode(node));
		if (this.visitedPath.add(uniqueId)) {
			visitChildren(node, other);
			this.visitedPath.remove(uniqueId);
		}
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
		return this.equality;
	}

}
