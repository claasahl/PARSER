package de.claas.parser.visitors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

public class NodeEquality implements NodeVisitor {

	private final Set<Integer> visitedPath = new HashSet<>();
	private Object obj;
	private boolean equality = true;

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

	public boolean isEquality() {
		return this.equality;
	}

}
