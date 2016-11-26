package de.claas.parser.visitors;

import java.util.HashSet;
import java.util.Set;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.NumberValue;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;

/**
 * The class {@link RuleHashCode}. It is an implementation of the interface
 * {@link RuleVisitor}. It is intended to determine a combined hash code for all
 * visited {@link Rule}s.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to determine multiple hash codes.
 *
 * @author Claas Ahlrichs
 */
public class RuleHashCode implements RuleVisitor {

	private final Set<Integer> visitedPath = new HashSet<>();
	private int hashCode = 0;

	@Override
	public void visitConjunction(Conjunction rule) {
		this.hashCode += rule.getClass().hashCode();

		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			for (Rule child : rule) {
				child.visit(this);
			}
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		this.hashCode += rule.getClass().hashCode();

		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			for (Rule child : rule) {
				child.visit(this);
			}
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		this.hashCode += rule.getClass().hashCode();
		this.hashCode += rule.getName().hashCode();
		if (rule.getComment() != null)
			this.hashCode += rule.getComment().hashCode();

		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			if (rule.getRule() != null)
				rule.getRule().visit(this);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		this.hashCode += rule.getClass().hashCode();

		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			rule.getRule().visit(this);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		this.hashCode += rule.getClass().hashCode();
		this.hashCode += Integer.hashCode(rule.getMinimumNumberOfRepetions());
		this.hashCode += Integer.hashCode(rule.getMaximumNumberOfRepetions());

		Integer uniqueId = new Integer(System.identityHashCode(rule));
		if (this.visitedPath.add(uniqueId)) {
			rule.getRule().visit(this);
			this.visitedPath.remove(uniqueId);
		}
	}

	@Override
	public void visitTerminal(CharacterValue rule) {
		this.hashCode += rule.getClass().hashCode();
		this.hashCode += rule.isCaseSensitive() ? 4096 : 512;
		if (rule.getTerminal() != null)
			this.hashCode += rule.getTerminal().hashCode();
	}

	@Override
	public void visitTerminal(NumberValue rule) {
		this.hashCode += rule.getClass().hashCode();
		this.hashCode += Integer.hashCode(rule.getRadix());
		if (rule.getTerminal() != null)
			this.hashCode += rule.getTerminal().hashCode();
		if (rule.getRangeStart() != null)
			this.hashCode += rule.getRangeStart().hashCode();
		if (rule.getRangeEnd() != null)
			this.hashCode += rule.getRangeEnd().hashCode();
	}

	/**
	 * Returns a combined hash code for all visited {@link Rule}s.
	 * 
	 * @return a combined hash code for all visited {@link Rule}s
	 */
	public int getHashCode() {
		return this.hashCode;
	}

}
