package de.claas.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.claas.parser.visitors.RuleEquality;
import de.claas.parser.visitors.RuleHashCode;

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
	 * Internal flag for signaling that the hash code needs to be updated. The
	 * actual update is done in a lazy fashion (i.e. hash code is updated the
	 * next time it is needed).
	 */
	private boolean invalidHashCode = true;

	/**
	 * Internally cached hash code. The hash code is kept in local storage for
	 * performance reasons. It will be updated in accordance with the
	 * {@link #invalidHashCode}-flag.
	 */
	private int hashCode;

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
		invalidateHashCode();
		return rule != null ? this.children.add(rule) : false;
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
		invalidateHashCode();
		return this.children.remove(rule);
	}

	/**
	 * Returns <code>true</code> if this rule has children. Otherwise
	 * <code>false</code> is returned (i.e. number of children is zero).
	 * 
	 * @return <code>true</code> if this rule has children. <code>false</code>
	 *         otherwise
	 */
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	@Override
	public Iterator<Rule> iterator() {
		return this.children.iterator();
	}

	/**
	 * Instructs this rule to visit the given {@link RuleVisitor} instance.
	 *
	 * @param visitor
	 *            the visitor
	 */
	public abstract void visit(RuleVisitor visitor);

	/**
	 * Notifies this node that its hash code is invalid. The hash code will be
	 * lazily updated the time it is needed.
	 */
	protected void invalidateHashCode() {
		this.invalidHashCode = true;
	}

	@Override
	public int hashCode() {
		// it is acceptable that the hash code only changes if (local) fields
		// are changed. This hash code has no way of knowing whether any child
		// was modified, since this hash code was last updated, and does not
		// reflect any such changes in any of its children.
		if (this.invalidHashCode) {
			RuleHashCode visitor = new RuleHashCode();
			this.visit(visitor);
			this.hashCode = visitor.getHashCode();
			this.invalidHashCode = false;
		}
		return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		RuleEquality visitor = new RuleEquality(obj);
		this.visit(visitor);
		return visitor.isEquality();
	}

}