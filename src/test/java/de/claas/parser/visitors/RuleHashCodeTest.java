package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;

import org.junit.Before;

import de.claas.parser.Rule;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.NumberValue;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;

/**
 * The JUnit test for class {@link RuleHashCode}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 */
public class RuleHashCodeTest extends RuleVisitorTest {

	private CharacterValue defaultChild;
	private int defaultChildHashCode;

	/**
	 * Returns an instantiated {@link RuleHashCode} class with default values.
	 * 
	 * @return an instantiated {@link RuleHashCode} class
	 */
	@SuppressWarnings("static-method")
	private RuleHashCode build() {
		return new RuleHashCode();
	}

	@Before
	public void before() {
		this.defaultChild = new CharacterValue("child");
		this.defaultChildHashCode = this.defaultChild.getClass().hashCode();
		this.defaultChildHashCode += this.defaultChild.isCaseSensitive() ? 4096 : 512;
		this.defaultChildHashCode += this.defaultChild.getTerminal().hashCode();
	}

	@Override
	public void shouldHandleNoRule() {
		RuleHashCode visitor = build();
		assertEquals(0, visitor.getHashCode());
	}

	@Override
	public void shouldHandleConjunctionRule() {
		RuleHashCode visitor = build();
		Rule rule = new Conjunction(this.defaultChild);
		rule.visit(visitor);

		int expected = rule.getClass().hashCode();
		expected += this.defaultChildHashCode;
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleDisjunctionRule() {
		RuleHashCode visitor = build();
		Rule rule = new Disjunction(this.defaultChild);
		rule.visit(visitor);

		int expected = rule.getClass().hashCode();
		expected += this.defaultChildHashCode;
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleNonTerminalRule() {
		RuleHashCode visitor = build();
		Rule rule = new NonTerminal("non-terminal", "comment", this.defaultChild);
		rule.visit(visitor);

		int expected = rule.getClass().hashCode();
		expected += "non-terminal".hashCode();
		expected += "comment".hashCode();
		expected += this.defaultChildHashCode;
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleOptionalRule() {
		RuleHashCode visitor = build();
		Rule rule = new Optional(this.defaultChild);
		rule.visit(visitor);

		int expected = rule.getClass().hashCode();
		expected += this.defaultChildHashCode;
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleRepetitionRule() {
		RuleHashCode visitor = build();
		Rule rule = new Repetition(this.defaultChild, 10, 42);
		rule.visit(visitor);

		int expected = rule.getClass().hashCode();
		expected += Integer.hashCode(10);
		expected += Integer.hashCode(42);
		expected += this.defaultChildHashCode;
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleTerminalRule() {
		RuleHashCode visitor = build();
		boolean caseSensitive = true;
		Rule rule = new CharacterValue(caseSensitive, "child");
		rule.visit(visitor);

		int expected = rule.getClass().hashCode();
		expected += caseSensitive ? 4096 : 512;
		expected += "child".hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleRules() {
		RuleHashCode visitor = build();
		Rule terminals = new NumberValue(16, '0', '9');
		Rule digit = new NonTerminal("digit", terminals);
		Rule digits = new Repetition(digit, 1, 7);
		digits.visit(visitor);

		int expected = digits.getClass().hashCode();
		expected += Integer.hashCode(1);
		expected += Integer.hashCode(7);
		expected += digit.getClass().hashCode();
		expected += "digit".hashCode();
		expected += terminals.getClass().hashCode();
		expected += Integer.hashCode(16);
		expected += Character.hashCode('0');
		expected += Character.hashCode('9');
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleCyclicRepetitionRule() {
		RuleHashCode visitor = build();
		Rule r0 = new Conjunction();
		Rule r1 = new Repetition(r0);
		r0.addChild(r1);
		r1.visit(visitor);

		int expected = 2 * r1.getClass().hashCode();
		expected += 2 * Integer.hashCode(0);
		expected += 2 * Integer.hashCode(Integer.MAX_VALUE);
		expected += r0.getClass().hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleCyclicOptionalRule() {
		RuleHashCode visitor = build();
		Rule r0 = new Conjunction();
		Rule r1 = new Optional(r0);
		r0.addChild(r1);
		r1.visit(visitor);

		int expected = 2 * r1.getClass().hashCode();
		expected += r0.getClass().hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleCyclicNonTerminalRule() {
		RuleHashCode visitor = build();
		Rule r0 = new Conjunction();
		Rule r1 = new NonTerminal("rulename", r0);
		r0.addChild(r1);
		r1.visit(visitor);

		int expected = 2 * r1.getClass().hashCode();
		expected += 2 * "rulename".hashCode();
		expected += r0.getClass().hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleCyclicDisjunctionRule() {
		RuleHashCode visitor = build();
		Rule r0 = new Conjunction();
		Rule r1 = new Disjunction(r0);
		r0.addChild(r1);
		r1.visit(visitor);

		int expected = 2 * r1.getClass().hashCode();
		expected += r0.getClass().hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

	@Override
	public void shouldHandleCyclicConjunctionRule() {
		RuleHashCode visitor = build();
		Rule r0 = new Conjunction();
		Rule r1 = new Conjunction(r0);
		r0.addChild(r1);
		r1.visit(visitor);

		int expected = 2 * r1.getClass().hashCode();
		expected += r0.getClass().hashCode();
		assertEquals(expected, visitor.getHashCode());
	}

}
