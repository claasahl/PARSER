package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Decorator;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;

/**
 *
 * The JUnit test for class {@link UpdateNonTerminalReferences}. It is intended
 * to collect and document a set of test cases for the tested class. Please
 * refer to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class UpdateNonTerminalReferencesTest extends RuleVisitorTest {

	private final Map<String, NonTerminal> rules = new HashMap<>();
	private final String ANOTHER_NON_TERMINAL = "another non-terminal";
	private final String NON_TERMINAL = "non-terminal";
	private final String TERMINAL = "some terminal";

	/**
	 * Returns an instantiated {@link UpdateNonTerminalReferences} class with
	 * the given {@link NonTerminal}s.
	 *
	 * @param rules
	 *            the {@link NonTerminal}s
	 * @return an instantiated {@link UpdateNonTerminalReferences} class
	 */
	@SuppressWarnings("static-method")
	private RuleVisitor build(Collection<NonTerminal> rules) {
		return new UpdateNonTerminalReferences(rules);
	}

	@Before
	public void before() {
		this.rules.clear();
		this.rules.put(this.NON_TERMINAL, new NonTerminal(this.NON_TERMINAL, new CharacterValue(this.NON_TERMINAL)));
		this.rules.put(this.ANOTHER_NON_TERMINAL,
				new NonTerminal(this.ANOTHER_NON_TERMINAL, new CharacterValue(this.ANOTHER_NON_TERMINAL)));
	}

	@Override
	public void shouldHandleNoRule() {
		// nothing to be done
	}

	@Override
	public void shouldHandleConjunctionRule() {
		Rule actual = new Conjunction(new NonTerminal(this.NON_TERMINAL));
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Rule expected = new Conjunction(this.rules.get(this.NON_TERMINAL));
		assertEquals(expected, actual);
	}

	@Override
	public void shouldHandleDisjunctionRule() {
		Rule actual = new Disjunction(new NonTerminal(this.NON_TERMINAL));
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Rule expected = new Disjunction(this.rules.get(this.NON_TERMINAL));
		assertEquals(expected, actual);
	}

	@Override
	public void shouldHandleNonTerminalRule() {
		Rule actual = new NonTerminal(this.NON_TERMINAL);
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Rule expected = this.rules.get(this.NON_TERMINAL);
		assertEquals(expected, actual);
	}

	@Override
	public void shouldHandleOptionalRule() {
		Rule actual = new Optional(new NonTerminal(this.NON_TERMINAL));
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Rule expected = new Optional(this.rules.get(this.NON_TERMINAL));
		assertEquals(expected, actual);
	}

	@Override
	public void shouldHandleRepetitionRule() {
		Rule actual = new Repetition(new NonTerminal(this.NON_TERMINAL));
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Rule expected = new Repetition(this.rules.get(this.NON_TERMINAL));
		assertEquals(expected, actual);
	}

	@Override
	public void shouldHandleTerminalRule() {
		Rule actual = new CharacterValue(this.TERMINAL);
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Rule expected = new CharacterValue(this.TERMINAL);
		assertEquals(expected, actual);
	}

	@Override
	public void shouldHandleRules() {
		Rule actual = new Conjunction();
		actual.addChild(new CharacterValue(this.TERMINAL));
		actual.addChild(new NonTerminal(this.NON_TERMINAL));
		actual.addChild(new CharacterValue(this.TERMINAL));
		actual.addChild(new NonTerminal(this.ANOTHER_NON_TERMINAL));
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Rule expected = new Conjunction();
		expected.addChild(new CharacterValue(this.TERMINAL));
		expected.addChild(this.rules.get(this.NON_TERMINAL));
		expected.addChild(new CharacterValue(this.TERMINAL));
		expected.addChild(this.rules.get(this.ANOTHER_NON_TERMINAL));
		assertEquals(expected, actual);
	}

	@Override
	public void shouldHandleCyclicRepetitionRule() {
		Decorator cycle = new Repetition(null);
		Rule actual = new Conjunction(cycle, new NonTerminal(this.NON_TERMINAL));
		cycle.setRule(actual);
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Decorator expectedCycle = new Repetition(null);
		Rule expected = new Conjunction(expectedCycle, this.rules.get(this.NON_TERMINAL));
		expectedCycle.setRule(expected);
		assertEquals(expected, actual);
	}

	@Override
	public void shouldHandleCyclicOptionalRule() {
		Decorator cycle = new Optional(null);
		Rule actual = new Conjunction(cycle, new NonTerminal(this.NON_TERMINAL));
		cycle.setRule(actual);
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Decorator expectedCycle = new Optional(null);
		Rule expected = new Conjunction(expectedCycle, this.rules.get(this.NON_TERMINAL));
		expectedCycle.setRule(expected);
		assertEquals(expected, actual);
	}

	@Override
	public void shouldHandleCyclicNonTerminalRule() {
		Decorator cycle = new NonTerminal("i am cyclic");
		Rule actual = new Conjunction(cycle, new NonTerminal(this.NON_TERMINAL));
		cycle.setRule(actual);
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Decorator expectedCycle = new NonTerminal("i am cyclic");
		Rule expected = new Conjunction(expectedCycle, this.rules.get(this.NON_TERMINAL));
		expectedCycle.setRule(expected);
		assertEquals(expected, actual);
	}

	@Override
	public void shouldHandleCyclicDisjunctionRule() {
		Rule cycle = new Disjunction(new CharacterValue(this.TERMINAL));
		Rule actual = new Conjunction(cycle, new NonTerminal(this.NON_TERMINAL));
		cycle.addChild(actual);
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Rule expectedCycle = new Disjunction(new CharacterValue(this.TERMINAL));
		Rule expected = new Conjunction(expectedCycle, this.rules.get(this.NON_TERMINAL));
		expectedCycle.addChild(expected);
		assertEquals(expected, actual);
	}

	@Override
	public void shouldHandleCyclicConjunctionRule() {
		Rule cycle = new Conjunction(new CharacterValue(this.TERMINAL));
		Rule actual = new Conjunction(cycle, new NonTerminal(this.NON_TERMINAL));
		cycle.addChild(actual);
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Rule expectedCycle = new Conjunction(new CharacterValue(this.TERMINAL));
		Rule expected = new Conjunction(expectedCycle, this.rules.get(this.NON_TERMINAL));
		expectedCycle.addChild(expected);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldNotUpdateNonTerminals() {
		Rule actual = new Conjunction();
		actual.addChild(new CharacterValue(this.TERMINAL));
		actual.addChild(CharacterValue.alternatives(false, "hello", "world"));
		actual.addChild(new CharacterValue(this.TERMINAL));
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Rule expected = new Conjunction();
		expected.addChild(new CharacterValue(this.TERMINAL));
		expected.addChild(CharacterValue.alternatives(false, "hello", "world"));
		expected.addChild(new CharacterValue(this.TERMINAL));
		assertEquals(expected, actual);
	}

	@Test
	public void shouldOnlyUpdateRegisteredNonTerminals() {
		Rule actual = new Conjunction();
		actual.addChild(new NonTerminal(this.NON_TERMINAL));
		actual.addChild(new CharacterValue(this.TERMINAL));
		actual.addChild(new NonTerminal("unregistered", new CharacterValue(this.TERMINAL)));
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		Rule expected = new Conjunction();
		expected.addChild(this.rules.get(this.NON_TERMINAL));
		expected.addChild(new CharacterValue(this.TERMINAL));
		expected.addChild(new NonTerminal("unregistered", new CharacterValue(this.TERMINAL)));
		assertEquals(expected, actual);
	}

	@Test
	public void shouldUpdateNestedNonTerminals() {
		Rule rule = new NonTerminal(this.NON_TERMINAL);
		NonTerminal incomplete = new NonTerminal("incomplete", rule);
		this.rules.put("incomplete", incomplete);

		Rule actual = new Conjunction();
		actual.addChild(incomplete);
		RuleVisitor visitor = build(this.rules.values());
		actual.visit(visitor);

		NonTerminal complete = new NonTerminal("incomplete");
		complete.setRule(this.rules.get(this.NON_TERMINAL));
		Rule expected = new Conjunction();
		expected.addChild(complete);
		assertEquals(expected, actual);
	}

}
