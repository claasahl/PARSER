package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;

import de.claas.parser.Rule;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The JUnit test for class {@link ExtractTerminalsTest}. It is intended to
 * collect and document a set of test cases for the tested class. Please refer
 * to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class ExtractTerminalsTest extends RuleVisitorTest {

	private static final String NAME = "ruleName";
	private static final Terminal ALPHA = new Terminal("A", "B", "C");
	private static final Terminal NUM = new Terminal("1", "2", "3");
	private ExtractTerminals visitor;

	@Before
	public void before() {
		visitor = new ExtractTerminals();
	}

	@Override
	public void shouldHandleNoRule() {
		assertTerminals();
	}

	@Override
	public void shouldHandleConjunctionRule() {
		new Conjunction(ALPHA).visit(visitor);
		assertTerminals(ALPHA);
	}

	@Override
	public void shouldHandleDisjunctionRule() {
		new Disjunction(NUM).visit(visitor);
		assertTerminals(NUM);
	}

	@Override
	public void shouldHandleNonTerminalRule() {
		new NonTerminal(NAME, NUM).visit(visitor);
		assertTerminals(NUM);
	}

	@Override
	public void shouldHandleOptionalRule() {
		new Optional(ALPHA).visit(visitor);
		assertTerminals(ALPHA);
	}

	@Override
	public void shouldHandleRepetitionRule() {
		new Repetition(ALPHA).visit(visitor);
		assertTerminals(ALPHA);
	}

	@Override
	public void shouldHandleTerminalRule() {
		NUM.visit(visitor);
		assertTerminals(NUM);
	}

	@Override
	public void shouldHandleRules() {
		Terminal asterics = new Terminal("*");
		Rule digit = new NonTerminal("digit", NUM);
		Rule digits = new Repetition(digit);
		Rule repeat = new NonTerminal("repeat",
				new Disjunction(new Conjunction(digit, digits), new Conjunction(digits, asterics, digits)));
		repeat.visit(visitor);
		assertTerminals(NUM, asterics);
	}

	@Override
	public void shouldHandleCyclicRepetitionRule() {
		Repetition rule = new Repetition(null);
		rule.setRule(new Disjunction(rule, NUM));
		rule.visit(visitor);
		assertTerminals(NUM);
	}

	@Override
	public void shouldHandleCyclicOptionalRule() {
		Optional rule = new Optional(null);
		rule.setRule(new Disjunction(rule, ALPHA));
		rule.visit(visitor);
		assertTerminals(ALPHA);
	}

	@Override
	public void shouldHandleCyclicNonTerminalRule() {
		NonTerminal rule = new NonTerminal(NAME);
		rule.setRule(new Disjunction(rule, NUM));
		rule.visit(visitor);
		assertTerminals(NUM);
	}

	@Override
	public void shouldHandleCyclicDisjunctionRule() {
		Disjunction rule = new Disjunction(ALPHA);
		rule.addChild(rule);
		rule.visit(visitor);
		assertTerminals(ALPHA);
	}

	@Override
	public void shouldHandleCyclicConjunctionRule() {
		Conjunction rule = new Conjunction(NUM);
		rule.addChild(rule);
		rule.visit(visitor);
		assertTerminals(NUM);
	}

	private void assertTerminals(Terminal... terminals) {
		Set<String> expectedTerminals = new HashSet<>();
		for (Terminal terminal : terminals) {
			Iterator<String> iterator = terminal.getTerminals();
			while (iterator.hasNext())
				expectedTerminals.add(iterator.next());
		}

		Set<String> actualTerminals = visitor.getTerminals();
		for (String terminal : expectedTerminals) {
			assertTrue("List of actual terminals did not include '" + terminal + "' (" + actualTerminals + ").",
					actualTerminals.contains(terminal));
		}
		assertEquals("The actual list of terminals is larger than expected.", expectedTerminals.size(),
				actualTerminals.size());
	}

}
