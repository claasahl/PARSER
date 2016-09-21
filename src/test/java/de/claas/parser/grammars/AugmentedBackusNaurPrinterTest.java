package de.claas.parser.grammars;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.claas.parser.Rule;
import de.claas.parser.exceptions.CyclicRuleException;
import de.claas.parser.grammars.AugmentedBackusNaurPrinter;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;
import de.claas.parser.visitors.RuleVisitorTest;

/**
 * 
 * The JUnit test for class {@link AugmentedBackusNaurPrinter}. It is intended
 * to collect and document a set of test cases for the tested class. Please
 * refer to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class AugmentedBackusNaurPrinterTest extends RuleVisitorTest {

	private static final String NAME = "ruleName";
	private static final Terminal ALPHA = new Terminal("A", "B", "C");
	private static final Terminal NUM = new Terminal("1", "2", "3");

	@Override
	public void shouldHandleNoRule() {
		// nothing to be done
	}

	@Override
	public void shouldHandleConjunctionRule() {
		NonTerminal rule = new NonTerminal(NAME, new Conjunction(ALPHA, NUM));
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter();
		rule.visit(printer);
		assertEquals("ruleName = (('A' / 'B' / 'C') ('1' / '2' / '3'))", printer.toString());
	}

	@Override
	public void shouldHandleDisjunctionRule() {
		NonTerminal rule = new NonTerminal(NAME, new Disjunction(ALPHA, NUM));
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter();
		rule.visit(printer);
		assertEquals("ruleName = (('A' / 'B' / 'C') / ('1' / '2' / '3'))", printer.toString());
	}

	@Override
	public void shouldHandleNonTerminalRule() {
		NonTerminal rule = new NonTerminal(NAME, NUM);
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter();
		rule.visit(printer);
		assertEquals("ruleName = ('1' / '2' / '3')", printer.toString());
	}

	@Override
	public void shouldHandleOptionalRule() {
		NonTerminal rule = new NonTerminal(NAME, new Optional(ALPHA));
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter();
		rule.visit(printer);
		assertEquals("ruleName = *1(('A' / 'B' / 'C'))", printer.toString());
	}

	@Override
	public void shouldHandleRepetitionRule() {
		NonTerminal rule = new NonTerminal(NAME, new Repetition(ALPHA));
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter();
		rule.visit(printer);
		assertEquals("ruleName = *(('A' / 'B' / 'C'))", printer.toString());
	}

	@Override
	public void shouldHandleTerminalRule() {
		NonTerminal rule = new NonTerminal(NAME, ALPHA);
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter();
		rule.visit(printer);
		assertEquals("ruleName = ('A' / 'B' / 'C')", printer.toString());
	}

	@Override
	public void shouldHandleRules() {
		Rule asterics = new Terminal("*");
		Rule digit = new NonTerminal("digit", NUM);
		Rule digits = new Repetition(digit);
		NonTerminal repeat = new NonTerminal("repeat",
				new Disjunction(new Conjunction(digit, digits), new Conjunction(digits, asterics, digits)));
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter("\n");
		repeat.visit(printer);

		List<String> lines = new ArrayList<>();
		lines.add("repeat = ((digit *(digit)) / (*(digit) ('*') *(digit)))");
		lines.add("digit = ('1' / '2' / '3')");
		assertEquals(String.join("\n", lines), printer.toString());
	}

	@Override
	@Test(expected = CyclicRuleException.class)
	public void shouldHandleCyclicRepetitionRule() {
		Repetition rule = new Repetition(null);
		rule.setRule(new Disjunction(rule, ALPHA));
		NonTerminal root = new NonTerminal(NAME, rule);
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter();
		root.visit(printer);
	}

	@Override
	@Test(expected = CyclicRuleException.class)
	public void shouldHandleCyclicOptionalRule() {
		Optional rule = new Optional(null);
		rule.setRule(new Disjunction(rule, ALPHA));
		NonTerminal root = new NonTerminal(NAME, rule);
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter();
		root.visit(printer);
	}

	@Override
	public void shouldHandleCyclicNonTerminalRule() {
		NonTerminal rule = new NonTerminal(NAME);
		rule.setRule(new Disjunction(rule, NUM));
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter();
		rule.visit(printer);
		assertEquals("ruleName = (ruleName / ('1' / '2' / '3'))", printer.toString());
	}

	@Override
	@Test(expected = CyclicRuleException.class)
	public void shouldHandleCyclicDisjunctionRule() {
		Disjunction rule = new Disjunction();
		rule.addChild(rule);
		rule.addChild(ALPHA);
		NonTerminal root = new NonTerminal(NAME, rule);
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter();
		root.visit(printer);
	}

	@Override
	@Test(expected = CyclicRuleException.class)
	public void shouldHandleCyclicConjunctionRule() {
		Disjunction rule = new Disjunction();
		rule.addChild(rule);
		rule.addChild(ALPHA);
		NonTerminal root = new NonTerminal(NAME, rule);
		AugmentedBackusNaurPrinter printer = new AugmentedBackusNaurPrinter();
		root.visit(printer);
	}
}
