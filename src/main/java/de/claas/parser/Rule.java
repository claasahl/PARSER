package de.claas.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * Superclass of all (grammar) rules within this package. This class is intended
 * to model rules that describe grammars. Implementations of this class are
 * utilized by {@link Grammar} instances to describe abstract concepts such as
 * words, phrases and sentences.
 * <p>
 * The hierarchy of this class resembles the <i>composite</i> design pattern.
 * This is due to the fact that {@link Grammar} instances can largely be
 * described by a tree of nested (grammar) rules.
 * 
 * @author Claas Ahlrichs
 * 
 * @see Grammar
 * 
 */
public abstract class Rule implements Iterable<Rule> {

	/**
	 * Internal list of children. This is not intended to be exposed for outside
	 * access. The {@link #iterator()} function already provides access to this
	 * list, but in a way that decouples the internal representation of children
	 * from the way outside classes access them.
	 */
	private final List<Rule> children = new ArrayList<>();

	/**
	 * Creates an instance with the given parameters. All children are added by
	 * calling {@link #addChild(Rule)} in the order they are passed into this
	 * constructor.
	 * 
	 * @param children
	 *            the children
	 */
	public Rule(Rule... children) {
		for (Rule rule : children) {
			addChild(rule);
		}
	}

	/**
	 * Adds the given (child) rule. Returns <code>true</code> if the child was
	 * successfully added. Otherwise <code>false</code> is returned.
	 * 
	 * @param rule
	 *            the (child) rule
	 * @return <code>true</code> if the child was successfully added.
	 *         <code>false</code> otherwise
	 */
	public boolean addChild(Rule rule) {
		return rule != null ? children.add(rule) : false;
	}

	/**
	 * Removes the given (child) rule. Returns <code>true</code> if the child
	 * was successfully removed. Otherwise <code>false</code> is returned.
	 * 
	 * @param rule
	 *            the (child) rule
	 * @return <code>true</code> if the child was successfully removed.
	 *         <code>false</code> otherwise
	 */
	public boolean removeChild(Rule rule) {
		return children.remove(rule);
	}

	/**
	 * Returns <code>true</code> if this rule has children. Otherwise
	 * <code>false</code> is returned (i.e. number of children is zero).
	 * 
	 * @return <code>true</code> if this rule has children. <code>false</code>
	 *         otherwise
	 */
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public Iterator<Rule> iterator() {
		return children.iterator();
	}

	/**
	 * Tests if the given {@link State} object fulfills this rule. If successful
	 * (i.e. state fulfills this rule), then the state is processed and a
	 * {@link Node} (that represents the processed state) is returned. If
	 * unsuccessful (i.e. state does not fulfills this rule), then the state
	 * remains unchanged and <code>null</code> is returned.
	 * 
	 * @param state
	 *            the state
	 * @return the {@link Node} that represents the processed state or
	 *         <code>null</code> if the state does not fulfill this rule
	 */
	public abstract Node process(State state);

	/**
	 * Instructs this rule to visit the given {@link RuleVisitor} instance.
	 *
	 * @param visitor
	 *            the visitor
	 */
	public abstract void visit(RuleVisitor visitor);

}