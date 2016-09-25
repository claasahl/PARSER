package de.claas.parser.visitors;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Decorator;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

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
		if (rule == this.obj)
			return;
		if (this.obj == null || rule.getClass() != this.obj.getClass()) {
			this.equality = false;
			return;
		}

		Conjunction other = (Conjunction) this.obj;
		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			visitChildren(rule, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		if (rule == this.obj)
			return;
		if (this.obj == null || rule.getClass() != this.obj.getClass()) {
			this.equality = false;
			return;
		}

		Disjunction other = (Disjunction) this.obj;
		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			visitChildren(rule, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		if (rule == this.obj)
			return;
		if (this.obj == null || rule.getClass() != this.obj.getClass()) {
			this.equality = false;
			return;
		}

		NonTerminal other = (NonTerminal) this.obj;
		if (rule.getName() == null) {
			if (other.getName() != null) {
				this.equality = false;
				return;
			}
		} else if (!rule.getName().equals(other.getName())) {
			this.equality = false;
			return;
		}
		if (rule.getComment() == null) {
			if (other.getComment() != null) {
				this.equality = false;
				return;
			}
		} else if (!rule.getComment().equals(other.getComment())) {
			this.equality = false;
			return;
		}
		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			visitChild(rule, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		if (rule == this.obj)
			return;
		if (this.obj == null || rule.getClass() != this.obj.getClass()) {
			this.equality = false;
			return;
		}

		Optional other = (Optional) this.obj;
		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			visitChild(rule, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		if (rule == this.obj)
			return;
		if (this.obj == null || rule.getClass() != this.obj.getClass()) {
			this.equality = false;
			return;
		}

		Repetition other = (Repetition) this.obj;
		if (rule.getMinimumNumberOfRepetions() != other.getMinimumNumberOfRepetions()
				|| rule.getMaximumNumberOfRepetions() != other.getMaximumNumberOfRepetions()) {
			this.equality = false;
			return;
		}
		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			visitChild(rule, other);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitTerminal(Terminal rule) {
		if (rule == this.obj)
			return;
		if (this.obj == null || rule.getClass() != this.obj.getClass()) {
			this.equality = false;
			return;
		}
		Terminal other = (Terminal) this.obj;
		if (rule.isCaseSensitive() != other.isCaseSensitive()) {
			this.equality = false;
			return;
		}
		if (rule.getTerminals() == null) {
			if (other.getTerminals() != null) {
				this.equality = false;
				return;
			}
		} else {
			Iterator<String> terminals = rule.getTerminals();
			Iterator<String> otherTerminals = other.getTerminals();
			while (terminals.hasNext() && otherTerminals.hasNext()) {
				String terminal = terminals.next();
				String otherTerminal = otherTerminals.next();
				if (terminal == null) {
					if (otherTerminal != null) {
						this.equality = false;
						return;
					}
				} else if (!terminal.equals(otherTerminal)) {
					this.equality = false;
					return;
				}
			}
			this.equality = terminals.hasNext() == otherTerminals.hasNext();
		}
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
		return this.equality;
	}

}
