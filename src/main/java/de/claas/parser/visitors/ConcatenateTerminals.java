package de.claas.parser.visitors;

import java.util.HashSet;
import java.util.Set;

import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.exceptions.CyclicNodeException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link ConcatenateTerminals}. It is an implementation of the
 * interface {@link NodeVisitor}. It is intended to collect and concatenate
 * terminal symbols of the {@link Node} tree, that is being visited.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to visualize multiple trees.
 *
 * @author Claas Ahlrichs
 *
 */
public class ConcatenateTerminals implements NodeVisitor {

	private final Set<Node> visitedPath = new HashSet<>();
	private final StringBuilder data = new StringBuilder();

	@Override
	public void visitTerminalNode(TerminalNode node) {
		this.data.append(node.getTerminal());
	}

	@Override
	public void visitIntermediateNode(IntermediateNode node) {
		if (this.visitedPath.add(node)) {
			for (Node child : node) {
				child.visit(this);
			}
			this.visitedPath.remove(node);
		} else {
			throw new CyclicNodeException(node);
		}
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		if (this.visitedPath.add(node)) {
			for (Node child : node) {
				child.visit(this);
			}
			this.visitedPath.remove(node);
		} else {
			throw new CyclicNodeException(node);
		}
	}

	@Override
	public String toString() {
		return this.data.toString();
	}

}
