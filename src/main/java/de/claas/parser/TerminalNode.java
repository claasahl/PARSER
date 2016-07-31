package de.claas.parser;

/**
 * 
 * The class {@link TerminalNode}. It is an implementation of the {@link Node}
 * class. It is intended to represent terminal symbols of parsed sentences.
 * Instances of this class hold a token (or part) of a concrete sentence after
 * parsing.
 * 
 * @author Claas Ahlrichs
 * 
 * @see Grammar#parse(String)
 *
 */
public class TerminalNode extends Node {

	private final String terminal;

	/**
	 * Creates an instance with the given parameter.
	 * 
	 * @param terminal
	 *            the terminal symbol
	 */
	public TerminalNode(String terminal) {
		this.terminal = terminal;
	}

	/**
	 * Returns the terminal symbol that this node represents.
	 * 
	 * @return the terminal symbol that this node represents
	 */
	public String getTerminal() {
		return terminal;
	}

	@Override
	public boolean addChild(Node node) {
		return false;
	}

	@Override
	public boolean removeChild(Node node) {
		return false;
	}

	@Override
	public void visit(NodeVisitor visitor) {
		visitor.visitTerminalNode(this);
	}

	@Override
	public String toString() {
		return String.format("T:%s", getTerminal());
	}

}
