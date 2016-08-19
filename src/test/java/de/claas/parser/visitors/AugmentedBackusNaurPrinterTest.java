package de.claas.parser.visitors;

import static org.junit.Assert.*;

import org.junit.Test;

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
public class AugmentedBackusNaurPrinterTest {

	private static final String RULE_NAME = "ruleName";
	private static final Terminal TERMINAL_RULE_ALPHA = new Terminal("A", "B", "C");
	private static final Terminal TERMINAL_RULE_NUM = new Terminal("1", "2", "3");

	@Test
	public void shouldPrintConjunction() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Conjunction(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM));
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = (('A' / 'B' / 'C') ('1' / '2' / '3'))", printedRule);
	}

	@Test
	public void shouldPrintDisjunction() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Disjunction(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM));
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = (('A' / 'B' / 'C') / ('1' / '2' / '3'))", printedRule);
	}

	@Test
	public void shouldPrintOptional() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Optional(TERMINAL_RULE_ALPHA));
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = *1(('A' / 'B' / 'C'))", printedRule);
	}

	@Test
	public void shouldPrintRepetition() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Repetition(TERMINAL_RULE_ALPHA));
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = *(('A' / 'B' / 'C'))", printedRule);
	}

	@Test
	public void shouldPrintTerminal() {
		NonTerminal rule = new NonTerminal(RULE_NAME, TERMINAL_RULE_ALPHA);
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = ('A' / 'B' / 'C')", printedRule);
	}

	@Test
	public void shouldPrintRecursiveRule() {
		NonTerminal rule = new NonTerminal(RULE_NAME);
		rule.setRule(new Disjunction(rule, TERMINAL_RULE_NUM));
		String printedRule = new AugmentedBackusNaurPrinter(rule).toString();
		assertEquals("ruleName = (ruleName / ('1' / '2' / '3'))", printedRule);
	}
}
