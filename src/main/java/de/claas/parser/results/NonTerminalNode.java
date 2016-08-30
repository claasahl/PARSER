package de.claas.parser.results;

import de.claas.parser.Grammar;
import de.claas.parser.Node;
import de.claas.parser.NodeVisitor;

/**
 * 
 * The class {@link NonTerminalNode}. It is an implementation of the
 * {@link Node} class. It is intended to represent non-terminal symbols of
 * parsed sentences. Instances of this class capture the context in which its
 * child nodes occurred.
 * 
 * @author Claas Ahlrichs
 * 
 * @see Grammar#parse(String)
 *
 */
public class NonTerminalNode extends Node {

	private final String name;

	/**
	 * Constructs a new {@link NonTerminalNode} with the specified parameter.
	 * 
	 * @param name
	 *            the name
	 */
	public NonTerminalNode(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of this (non-terminal) node.
	 * 
	 * @return the name of this (non-terminal) node
	 */
	public String getName() {
		return name;
	}

	@Override
	public void visit(NodeVisitor visitor) {
		visitor.visitNonTerminaNode(this);
	}

	@Override
	public String toString() {
		return String.format("N:%s", getName());
	}

}
