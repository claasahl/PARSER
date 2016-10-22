package de.claas.parser.visitors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Decorator;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.NumberValue;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;

/**
 * 
 * The class {@link RuleEquality}. It is an implementation of the interface
 * {@link RuleVisitor}. It is intended to compare a {@link Rule}-hierarchy with
 * a reference object.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to compare multiple {@link Rule}s.
 *
 * @author Claas Ahlrichs
 *
 */
public class RuleEquality implements RuleVisitor {

	private final Set<Integer> visitedPath = new HashSet<>();
	private Object obj;
	private boolean visited = false;
	private boolean equality = true;

	/**
	 * Constructs a new {@link RuleEquality} with the specified parameter.
	 * 
	 * @param obj
	 *            the reference object with which the visited {@link Rule}s are
	 *            compared
	 */
	public RuleEquality(Object obj) {
		this.obj = obj;
	}

	@Override
	public void visitConjunction(Conjunction rule) {
		markAsVisited();
		if (preliminaryComparison(rule, this.obj))
			return;

		Conjunction other = (Conjunction) this.obj;
		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			visitChildren(rule, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		markAsVisited();
		if (preliminaryComparison(rule, this.obj))
			return;

		Disjunction other = (Disjunction) this.obj;
		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			visitChildren(rule, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		markAsVisited();
		if (preliminaryComparison(rule, this.obj))
			return;

		NonTerminal other = (NonTerminal) this.obj;
		if (isUnequal(rule.getName(), other.getName()))
			return; // already marked as unequal
		if (isUnequal(rule.getComment(), other.getComment()))
			return; // already marked as unequal
		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			visitChild(rule, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		markAsVisited();
		if (preliminaryComparison(rule, this.obj))
			return;

		Optional other = (Optional) this.obj;
		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			visitChild(rule, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		markAsVisited();
		if (preliminaryComparison(rule, this.obj))
			return;

		Repetition other = (Repetition) this.obj;
		if (rule.getMinimumNumberOfRepetions() != other.getMinimumNumberOfRepetions()
				|| rule.getMaximumNumberOfRepetions() != other.getMaximumNumberOfRepetions()) {
			markAsUnequal();
			return;
		}
		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			visitChild(rule, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitTerminal(CharacterValue rule) {
		markAsVisited();
		if (preliminaryComparison(rule, this.obj))
			return;

		CharacterValue other = (CharacterValue) this.obj;
		if (rule.isCaseSensitive() != other.isCaseSensitive()) {
			markAsUnequal();
			return;
		}
		if (isUnequal(rule.getTerminal(), other.getTerminal()))
			return; // already marked as unequal
	}

	@Override
	public void visitTerminal(NumberValue rule) {
		markAsVisited();
		if (preliminaryComparison(rule, this.obj))
			return;

		NumberValue other = (NumberValue) this.obj;
		if (rule.getRadix() != other.getRadix()) {
			markAsUnequal();
			return;
		}
		if (isUnequal(rule.getTerminal(), other.getTerminal()))
			return; // already marked as unequal
		if (isUnequal(rule.getRangeStart(), other.getRangeStart()))
			return; // already marked as unequal
		if (isUnequal(rule.getRangeEnd(), other.getRangeEnd()))
			return; // already marked as unequal
	}

	/**
	 * Marks the two rules as unequal.
	 */
	private void markAsUnequal() {
		this.equality = false;
	}

	/**
	 * Marks this visitor as visited. Be default it is assumed that two rules
	 * are equal, unless proven otherwise. However, this assumption required the
	 * visitor to be visited (otherwise any two rules would be assumed to be
	 * equal).
	 */
	private void markAsVisited() {
		this.visited = true;
	}

	/**
	 * Returns <code>true</code> if the two objects can already be said to be
	 * equal (or unequal). Otherwise, <code>false</code> is returned.
	 * <p>
	 * <b>Side effect</b>: this method may call {@link #markAsUnequal()}
	 * 
	 * @param rule
	 *            the original rule
	 * @param other
	 *            the reference rule with which the original rule is compared
	 * @return <code>true</code> if the two object can already be said to be
	 *         equal (or unequal). Otherwise, <code>false</code> is returned
	 */
	private boolean preliminaryComparison(Rule rule, Object other) {
		if (rule == other)
			return true; // "this.equality" is already "true"
		if (other == null || rule.getClass() != other.getClass()) {
			markAsUnequal();
			return true;
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if the two objects can be said to be unequal.
	 * Otherwise, <code>false</code> is returned.
	 * <p>
	 * <b>Side effect</b>: this method may call {@link #markAsUnequal()}
	 * 
	 * @param original
	 *            the original object (e.g. name of rule, terminal symbol, etc.)
	 * @param other
	 *            the reference object with which the original object is
	 *            compared
	 * @return <code>true</code> if the two objects can be said to be unequal.
	 *         Otherwise, <code>false</code> is returned.
	 */
	private boolean isUnequal(Object original, Object other) {
		if (original == null) {
			if (other != null) {
				markAsUnequal();
				return true;
			}
		} else if (!original.equals(other)) {
			markAsUnequal();
			return true;
		}
		return false;
	}

	/**
	 * A helper function that successively visits all children of both specified
	 * {@link Rule}s. It is main purpose is to ensure that the order in which
	 * the children occur is identical and that the children themselves are
	 * equal as well.
	 * 
	 * @param rule
	 *            the original rule
	 * @param other
	 *            the reference rule with which the original rule is compared
	 */
	private void visitChildren(Rule rule, Rule other) {
		Iterator<Rule> children = rule.iterator();
		Iterator<Rule> otherChildren = other.iterator();
		while (children.hasNext() && otherChildren.hasNext()) {
			Rule child = children.next();
			this.obj = otherChildren.next();
			child.visit(this);

			if (!this.equality)
				return;
		}
		this.equality = children.hasNext() == otherChildren.hasNext();
	}

	/**
	 * A helper function that visits the child of both specified {@link Rule}s.
	 * 
	 * @param rule
	 *            the original rule
	 * @param other
	 *            the reference rule with which the original rule is compared
	 */
	private void visitChild(Decorator rule, Decorator other) {
		Rule child = rule.getRule();
		this.obj = other.getRule();
		child.visit(this);
	}

	/**
	 * Returns <code>true</code> if the visited rules represent the same object
	 * that was passed into the constructor of this visitor. Otherwise
	 * <code>false</code> is returned.
	 * 
	 * @return <code>true</code> if the visited rules represent the same object
	 *         that was passed into the constructor of this visitor,
	 *         <code>false</code> other
	 */
	public boolean isEquality() {
		return this.equality && this.visited;
	}

}
