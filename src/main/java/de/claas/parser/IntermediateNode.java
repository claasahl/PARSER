package de.claas.parser;

/**
 * 
 * The class {@link IntermediateNode}. It is an implementation of the
 * {@link Node} class. It is intended to represent intermediate symbols of
 * parsed sentences. Instances of this class provide additional contextual
 * information that would otherwise be lost during parsing. However, most
 * applications will work just fine without this level of detail.
 * 
 * @author Claas Ahlrichs
 * 
 * @see Grammar#parse(String)
 *
 */
public class IntermediateNode extends Node {

	@Override
	public void visit(NodeVisitor visitor) {
		visitor.visitIntermediateNode(this);
	}

	@Override
	public String toString() {
		return "I";
	}

}
