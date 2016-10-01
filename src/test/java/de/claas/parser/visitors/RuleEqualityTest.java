package de.claas.parser.visitors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.claas.parser.Rule;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.NumberValue;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

/**
 *
 * The JUnit test for class {@link RuleEquality}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class RuleEqualityTest extends RuleVisitorTest {

	private Terminal defaultChild;

	/**
	 * Returns an instantiated {@link RuleEquality} class with the specified
	 * parameter.
	 * 
	 * @param obj
	 *            the reference object with which the visited {@link Rule}s are
	 *            compared
	 * 
	 * @return an instantiated {@link RuleEquality} class with the specified
	 *         parameter
	 */
	@SuppressWarnings("static-method")
	private RuleEquality build(Object obj) {
		return new RuleEquality(obj);
	}

	@Before
	public void before() {
		this.defaultChild = new CharacterValue("child");
	}

	@Override
	public void shouldHandleNoRule() {
		RuleEquality visitor = build(null);
		assertFalse(visitor.isEquality());
	}

	@Override
	public void shouldHandleConjunctionRule() {
		Object obj = new Conjunction(this.defaultChild);

		RuleEquality visitor = build(obj);
		Rule rule = new Conjunction(this.defaultChild);
		rule.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(obj);
		rule = new Disjunction(this.defaultChild);
		rule.visit(visitor);
		assertFalse(visitor.isEquality());
	}

	@Override
	public void shouldHandleDisjunctionRule() {
		Object obj = new Disjunction(this.defaultChild);

		RuleEquality visitor = build(obj);
		Rule rule = new Disjunction(this.defaultChild);
		rule.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(obj);
		rule = new Conjunction(this.defaultChild);
		rule.visit(visitor);
		assertFalse(visitor.isEquality());
	}

	@Override
	public void shouldHandleNonTerminalRule() {
		Object obj = new NonTerminal("non-terminal", "comment", this.defaultChild);

		RuleEquality visitor = build(obj);
		Rule rule = new NonTerminal("non-terminal", "comment", this.defaultChild);
		rule.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(obj);
		rule = new Disjunction(this.defaultChild);
		rule.visit(visitor);
		assertFalse(visitor.isEquality());
	}

	@Override
	public void shouldHandleOptionalRule() {
		Object obj = new Optional(this.defaultChild);

		RuleEquality visitor = build(obj);
		Rule rule = new Optional(this.defaultChild);
		rule.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(obj);
		rule = new Disjunction(this.defaultChild);
		rule.visit(visitor);
		assertFalse(visitor.isEquality());
	}

	@Override
	public void shouldHandleRepetitionRule() {
		Object obj = new Repetition(this.defaultChild, 10, 42);

		RuleEquality visitor = build(obj);
		Rule rule = new Repetition(this.defaultChild, 10, 42);
		rule.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(obj);
		rule = new Disjunction(this.defaultChild);
		rule.visit(visitor);
		assertFalse(visitor.isEquality());
	}

	@Override
	public void shouldHandleTerminalRule() {
		Object obj = CharacterValue.alternatives(true, "child", "node");

		RuleEquality visitor = build(obj);
		Rule rule = CharacterValue.alternatives(true, "child", "node");
		rule.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(obj);
		rule = new Disjunction(this.defaultChild);
		rule.visit(visitor);
		assertFalse(visitor.isEquality());
	}

	@Override
	public void shouldHandleRules() {
		Rule asterics = new CharacterValue("*");
		Rule digit = new NonTerminal("digit", new NumberValue(16, '0', '9'));
		Rule digits = new Repetition(digit);
		Rule repeat = new NonTerminal("repeat",
				new Disjunction(new Conjunction(digit, digits), new Conjunction(digits, asterics, digits)));

		RuleEquality visitor = build(repeat);
		asterics = new CharacterValue("*");
		digit = new NonTerminal("digit", new NumberValue(16, '0', '9'));
		digits = new Repetition(digit);
		repeat = new NonTerminal("repeat",
				new Disjunction(new Conjunction(digit, digits), new Conjunction(digits, asterics, digits)));
		repeat.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Override
	public void shouldHandleCyclicRepetitionRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Repetition(r0);
		r0.addChild(r1);

		RuleEquality visitor = build(r1);
		r0 = new Conjunction();
		r1 = new Repetition(r0);
		r0.addChild(r1);
		r1.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Override
	public void shouldHandleCyclicOptionalRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Optional(r0);
		r0.addChild(r1);

		RuleEquality visitor = build(r1);
		r0 = new Conjunction();
		r1 = new Optional(r0);
		r0.addChild(r1);
		r1.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Override
	public void shouldHandleCyclicNonTerminalRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new NonTerminal("rulename", r0);
		r0.addChild(r1);

		RuleEquality visitor = build(r1);
		r0 = new Conjunction();
		r1 = new NonTerminal("rulename", r0);
		r0.addChild(r1);
		r1.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Override
	public void shouldHandleCyclicDisjunctionRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Disjunction(r0);
		r0.addChild(r1);

		RuleEquality visitor = build(r1);
		r0 = new Conjunction();
		r1 = new Disjunction(r0);
		r0.addChild(r1);
		r1.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Override
	public void shouldHandleCyclicConjunctionRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Conjunction(r0);
		r0.addChild(r1);

		RuleEquality visitor = build(r1);
		r0 = new Conjunction();
		r1 = new Conjunction(r0);
		r0.addChild(r1);
		r1.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Test
	public void shouldHandleNull() {
		RuleEquality visitor = build(null);
		Rule rule = new Conjunction();
		rule.visit(visitor);
		assertFalse(visitor.isEquality());
	}

	@Test
	public void shouldBeReflexive() {
		Rule rule = new Conjunction();

		RuleEquality visitor = build(rule);
		rule.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Test
	public void shouldBeSymmetric() {
		Rule ruleA = new Conjunction();
		Rule ruleB = new Conjunction();

		RuleEquality visitor = build(ruleA);
		ruleB.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(ruleB);
		ruleA.visit(visitor);
		assertTrue(visitor.isEquality());
	}

	@Test
	public void shouldBeTransitive() {
		Rule ruleA = new Conjunction();
		Rule ruleB = new Conjunction();
		Rule ruleC = new Conjunction();

		RuleEquality visitor = build(ruleB);
		ruleA.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(ruleC);
		ruleB.visit(visitor);
		assertTrue(visitor.isEquality());

		visitor = build(ruleC);
		ruleA.visit(visitor);
		assertTrue(visitor.isEquality());
	}

}
