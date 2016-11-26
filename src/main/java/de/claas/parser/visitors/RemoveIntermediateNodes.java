package de.claas.parser.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import de.claas.parser.Grammar;
import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * The class {@link RemoveIntermediateNodes}. It is an implementation of the
 * interface {@link NodeVisitor}. It is intended to simplify the node structure
 * that is returned by {@link Grammar#parse(String, boolean)}. This is
 * accomplished by removing {@link IntermediateNode} instances.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to simplify the node structure of multiple trees.
 * 
 * @author Claas Ahlrichs
 */
public class RemoveIntermediateNodes implements NodeVisitor {

	private final Set<Node> visitedPath = new HashSet<>();
	private final Stack<Node> parents = new Stack<>();

	@Override
	public void visitTerminalNode(TerminalNode node) {
		// nothing to be done here
	}

	@Override
	public void visitIntermediateNode(IntermediateNode node) {
		if (this.visitedPath.add(node) && !this.parents.isEmpty()) {
			// enumerate all siblings
			Node parent = this.parents.peek();
			List<Node> siblings = new ArrayList<>();
			for (Node child : parent) {
				siblings.add(child);
			}

			// remove all siblings
			for (Node child : siblings) {
				parent.removeChild(child);
			}

			// add all siblings (preserving order)
			for (Node child : siblings) {
				if (node.equals(child)) {
					for (Node c : node) {
						if (!node.equals(c)) {
							parent.addChild(c);
						}
					}
				} else {
					parent.addChild(child);
				}
			}

			// ... now visit all children
			for (Node child : node) {
				child.visit(this);
			}

			this.visitedPath.remove(node);
		}
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		if (this.visitedPath.add(node)) {
			this.parents.push(node);
			ArrayList<Node> tmp = new ArrayList<>();
			for (Node n : node) {
				tmp.add(n);
			}
			for (Node n : tmp) {
				n.visit(this);
			}
			this.parents.pop();
			this.visitedPath.remove(node);
		}
	}

}
