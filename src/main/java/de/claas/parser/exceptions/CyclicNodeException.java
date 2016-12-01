package de.claas.parser.exceptions;

import de.claas.parser.Node;

/**
 * The exception {@link CyclicNodeException}. It is thrown to indicate that a
 * graph of {@link Node}s contains a cyclic node (i.e. a node that directly or
 * indirectly references itself) and that the throwing entity cannot handle this
 * cyclic node.
 * 
 * @author Claas Ahlrichs
 */
public class CyclicNodeException extends RuntimeException {

	private static final long serialVersionUID = -3896618031014844794L;

	/**
	 * Constructs a new {@link CyclicNodeException} with the specified
	 * parameter.
	 * 
	 * @param node
	 *            the node where the cycle was detected
	 */
	public CyclicNodeException(Node node) {
		super(node.toString());
	}

}
