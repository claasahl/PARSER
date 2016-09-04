package de.claas.parser.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import de.claas.parser.Grammar;
import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link RemoveIntermediateNodes}. It is an implementation of the
 * interface {@link NodeVisitor}. It is intended to simplify the node structure
 * that is returned by {@link Grammar#parse(String, boolean)}. This is
 * accomplished by removing {@link IntermediateNode} instances.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to simplify the node structure of multiple trees.
 * 
 * @author Claas Ahlrichs
 *
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
		if (visitedPath.add(node) && !parents.isEmpty()) {
			// enumerate all siblings
			Node parent = parents.peek();
			ArrayList<Node> siblings = new ArrayList<>();
			for (Node n : parent) {
				siblings.add(n);
			}

			// remove all siblings
			for (Node n : siblings) {
				parent.removeChild(n);
			}

			// add all siblings (preserving order)
			for (Node n : siblings) {
				if (n == node) {
					for (Node c : node) {
						if (c != node) {
							parent.addChild(c);
						}
					}
				} else {
					parent.addChild(n);
				}
			}

			// ... now visit all children
			for (Node n : node) {
				n.visit(this);
			}

			visitedPath.remove(node);
		}
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		if (visitedPath.add(node)) {
			parents.push(node);
			ArrayList<Node> tmp = new ArrayList<>();
			for (Node n : node) {
				tmp.add(n);
			}
			for (Node n : tmp) {
				n.visit(this);
			}
			parents.pop();
			visitedPath.remove(node);
		}
	}

}
