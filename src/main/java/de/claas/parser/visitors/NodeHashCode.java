package de.claas.parser.visitors;

import java.util.HashSet;
import java.util.Set;

import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * The class {@link NodeHashCode}. It is an implementation of the interface
 * {@link NodeVisitor}. It is intended to determine a combined hash code for all
 * visited {@link Node}s.
 * <p>
 * This visitor is meant for one-time use, only.  As such, every hash-code needs
 * to be determined with a separate instance of this visitor. An instance of
 * this visitor should not be used to determine multiple hash codes.
 *
 * @author Claas Ahlrichs
 */
public class NodeHashCode implements NodeVisitor {

	private final Set<Integer> visitedPath = new HashSet<>();
	private int hashCode = 0;

	@Override
	public void visitTerminalNode(TerminalNode node) {
		this.hashCode += node.getClass().hashCode();
		this.hashCode += node.getTerminal().hashCode();
	}

	@Override
	public void visitIntermediateNode(IntermediateNode node) {
		this.hashCode += node.getClass().hashCode();

		Integer uniqueId = new Integer(System.identityHashCode(node));
		if (this.visitedPath.add(uniqueId)) {
			for (Node child : node) {
				child.visit(this);
			}
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitNonTerminaNode(NonTerminalNode node) {
		this.hashCode += node.getClass().hashCode();
		this.hashCode += node.getName().hashCode();

		Integer uniqueId = new Integer(System.identityHashCode(node));
		if (this.visitedPath.add(uniqueId)) {
			for (Node child : node) {
				child.visit(this);
			}
			this.visitedPath.remove(uniqueId);
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
