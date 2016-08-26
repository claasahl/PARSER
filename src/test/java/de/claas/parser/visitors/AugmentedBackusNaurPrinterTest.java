package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The JUnit test for class {@link AugmentedBackusNaurPrinterTest}. It is
 * intended to collect and document a set of test cases for the tested class.
 * Please refer to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class AugmentedBackusNaurPrinterTest extends RuleVisitorTest {

	private static final String RULE_NAME = "ruleName";
	private static final Terminal TERMINAL_RULE_ALPHA = new Terminal("A", "B", "C");
	private static final Terminal TERMINAL_RULE_NUM = new Terminal("1", "2", "3");

	@Override
	public void shouldHandleNoRule() {
		fail();
	}

	@Override
	public void shouldHandleConjunctionRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Conjunction(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM));
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = (('A' / 'B' / 'C') ('1' / '2' / '3'))", printedRule);
	}

	@Override
	public void shouldHandleDisjunctionRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Disjunction(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM));
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = (('A' / 'B' / 'C') / ('1' / '2' / '3'))", printedRule);
	}

	@Override
	public void shouldHandleNonTerminalRule() {
		fail();
	}

	@Override
	public void shouldHandleOptionalRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Optional(TERMINAL_RULE_ALPHA));
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = *1(('A' / 'B' / 'C'))", printedRule);
	}

	@Override
	public void shouldHandleRepetitionRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Repetition(TERMINAL_RULE_ALPHA));
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = *(('A' / 'B' / 'C'))", printedRule);
	}

	@Override
	public void shouldHandleTerminalRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME, TERMINAL_RULE_ALPHA);
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = ('A' / 'B' / 'C')", printedRule);
	}

	@Override
	public void shouldHandleRules() {
		fail();
	}

	@Override
	public void shouldHandleCyclicRepetitionRule() {
		fail();
	}

	@Override
	public void shouldHandleCyclicOptionalRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME);
		rule.setRule(new Disjunction(rule, TERMINAL_RULE_NUM));
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = (ruleName / ('1' / '2' / '3'))", printedRule);	
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
}
