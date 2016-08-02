package de.claas.parser;

/**
 * 
 * Superclass of all node-based visitors. This class is intended to model a
 * visitor for {@link Node} instances and their children. Implementations of
 * this class will most likely process or interpret node-hierarchies.
 * <p>
 * This class resembles the <i>visitor</i> design pattern. It includes a
 * visit-method for all implementations of the {@link Node} class.
 * 
 * @author Claas Ahlrichs
 *
 */
public interface NodeVisitor {

	/**
	 * Called by {@link TerminalNode}-nodes.
	 * 
	 * @param node
	 *            the node
	 */
	void visitTerminalNode(TerminalNode node);

	/**
	 * Called by {@link IntermediateNode}-nodes. The default implementation will
	 * simply visit all children of the given node.
	 * 
	 * @param node
	 *            the node
	 */
	default void visitIntermediateNode(IntermediateNode node) {
		for (Node n : node) {
			n.visit(this);
		}
	}

	/**
	 * Called by {@link NonTerminalNode}-nodes.
	 * 
	 * @param node
	 *            the node
	 */
	void visitNonTerminaNode(NonTerminalNode node);

}
