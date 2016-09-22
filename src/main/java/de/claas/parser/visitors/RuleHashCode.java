package de.claas.parser.visitors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The class {@link RuleHashCode}. It is an implementation of the interface
 * {@link RuleVisitor}. It is intended to determine a combined hash code for all
 * visited {@link Rule}s.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should not be used
 * to determine multiple hash codes.
 *
 * @author Claas Ahlrichs
 *
 */
public class RuleHashCode implements RuleVisitor {

	private final List<Rule> visitedPath = new ArrayList<>();
	private int hashCode = 0;

	@Override
	public void visitConjunction(Conjunction rule) {
		if (!this.visitedPath.contains(rule) && this.visitedPath.add(rule)) {
			this.hashCode += rule.getClass().hashCode();
			for (Rule child : rule) {
				child.visit(this);
			}
			this.visitedPath.remove(rule);
		} else {
			this.hashCode += rule.getClass().hashCode();
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		if (!this.visitedPath.contains(rule) && this.visitedPath.add(rule)) {
			this.hashCode += rule.getClass().hashCode();
			for (Rule child : rule) {
				child.visit(this);
			}
			this.visitedPath.remove(rule);
		} else {
			this.hashCode += rule.getClass().hashCode();
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		if (!this.visitedPath.contains(rule) && this.visitedPath.add(rule)) {
			this.hashCode += rule.getClass().hashCode();
			this.hashCode += rule.getName().hashCode();
			if (rule.getComment() != null)
				this.hashCode += rule.getComment().hashCode();
			if (rule.getRule() != null)
				rule.getRule().visit(this);
			this.visitedPath.remove(rule);
		} else {
			this.hashCode += rule.getClass().hashCode();
			this.hashCode += rule.getName().hashCode();
			if (rule.getComment() != null)
				this.hashCode += rule.getComment().hashCode();
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		if (!this.visitedPath.contains(rule) && this.visitedPath.add(rule)) {
			this.hashCode += rule.getClass().hashCode();
			rule.getRule().visit(this);
			this.visitedPath.remove(rule);
		} else {
			this.hashCode += rule.getClass().hashCode();
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		if (!this.visitedPath.contains(rule) && this.visitedPath.add(rule)) {
			this.hashCode += rule.getClass().hashCode();
			this.hashCode += Integer.hashCode(rule.getMinimumNumberOfRepetions());
			this.hashCode += Integer.hashCode(rule.getMaximumNumberOfRepetions());
			rule.getRule().visit(this);
			this.visitedPath.remove(rule);
		} else {
			this.hashCode += rule.getClass().hashCode();
			this.hashCode += Integer.hashCode(rule.getMinimumNumberOfRepetions());
			this.hashCode += Integer.hashCode(rule.getMaximumNumberOfRepetions());
		}
	}

	@Override
	public void visitTerminal(Terminal rule) {
		this.hashCode += rule.getClass().hashCode();
		this.hashCode += rule.isCaseSensitive() ? 4096 : 512;
		Iterator<String> terminals = rule.getTerminals();
		while (terminals.hasNext()) {
			this.hashCode += terminals.next().hashCode();
		}
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
