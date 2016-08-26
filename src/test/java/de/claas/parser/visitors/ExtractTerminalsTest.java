package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;

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

	private static final String RULE_NAME = "ruleName";
	private static final Terminal TERMINAL_RULE_ALPHA = new Terminal("A", "B", "C");
	private static final Terminal TERMINAL_RULE_NUM = new Terminal("1", "2", "3");
	private ExtractTerminals visitor;

	@Before
	public void before() {
		visitor = new ExtractTerminals();
	}

	@Override
	public void shouldHandleNoRule() {
		fail();
	}

	@Override
	public void shouldHandleConjunctionRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Conjunction(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM));
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM);
	}

	@Override
	public void shouldHandleDisjunctionRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Disjunction(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM));
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM);
	}

	@Override
	public void shouldHandleNonTerminalRule() {
		fail();
	}

	@Override
	public void shouldHandleOptionalRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Optional(TERMINAL_RULE_ALPHA));
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_ALPHA);
	}

	@Override
	public void shouldHandleRepetitionRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Repetition(TERMINAL_RULE_ALPHA));
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_ALPHA);
	}

	@Override
	public void shouldHandleTerminalRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME, TERMINAL_RULE_ALPHA);
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_ALPHA);
	}

	@Override
	public void shouldHandleRules() {
		fail();
	}

	@Override
	public void shouldHandleCyclicRepetitionRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME);
		rule.setRule(new Disjunction(rule, TERMINAL_RULE_NUM));
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_NUM);
		fail();
	}

	@Override
	public void shouldHandleCyclicOptionalRule() {
		fail();
	}

	@Override
	public void shouldHandleCyclicNonTerminalRule() {
		fail();
	}

	@Override
	public void shouldHandleCyclicDisjunctionRule() {
		fail();
	}

	@Override
	public void shouldHandleCyclicConjunctionRule() {
		fail();
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
