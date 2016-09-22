package de.claas.parser.visitors;

import java.util.ArrayList;
import java.util.List;

import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link NodeHashCode}. It is an implementation of the interface
 * {@link NodeVisitor}. It is intended to determine a combined hash code for all
 * visited {@link Node}s.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to determine multiple hash codes.
 *
 * @author Claas Ahlrichs
 *
 */
public class NodeHashCode implements NodeVisitor {

	private final List<Node> visitedPath = new ArrayList<>();
	private int hashCode = 0;

	@Override
	public void visitTerminalNode(TerminalNode node) {
		this.hashCode += node.getClass().hashCode();
		this.hashCode += node.getTerminal().hashCode();
	}

	@Override
	public void visitIntermediateNode(IntermediateNode node) {
		if (!this.visitedPath.contains(node) && this.visitedPath.add(node)) {
			this.hashCode += node.getClass().hashCode();
			for (Node n : node) {
				n.visit(this);
			}
			this.visitedPath.remove(node);
		} else {
			this.hashCode += node.getClass().hashCode();
		}
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		if (!this.visitedPath.contains(node) && this.visitedPath.add(node)) {
			this.hashCode += node.getClass().hashCode();
			this.hashCode += node.getName().hashCode();
			for (Node n : node) {
				n.visit(this);
			}
			this.visitedPath.remove(node);
		} else {
			this.hashCode += node.getClass().hashCode();
			this.hashCode += node.getName().hashCode();
		}
	}

	/**
	 * Returns a combined hash code for all visited {@link Node}s.
	 * 
	 * @return a combined hash code for all visited {@link Node}s
	 */
	public int getHashCode() {
		return this.hashCode;
	}

}
