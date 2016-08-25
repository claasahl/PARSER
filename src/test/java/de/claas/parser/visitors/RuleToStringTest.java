package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

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
 * The JUnit test for class {@link RuleToStringTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class RuleToStringTest {

	private RuleVisitor visitor;

	@Before
	public void before() {
		visitor = new RuleToString();
	}

	@Test
	public void shouldHandleNoRule() {
		assertEquals("", visitor.toString());
	}

	@Test
	public void shouldHandleConjunctionRule() {
		Rule r1 = new Terminal("terminal");
		new Conjunction(r1).visit(visitor);
		assertEquals(Conjunction.class.getName() + "\n-terminal-" + Terminal.class.getName() + "\n",
				visitor.toString());
	}

	@Test
	public void shouldHandleDisjunctionRule() {
		Rule r1 = new Terminal("terminal");
		new Disjunction(r1).visit(visitor);
		assertEquals(Disjunction.class.getName() + "\n-terminal-" + Terminal.class.getName() + "\n",
				visitor.toString());
	}

	@Test
	public void shouldHandleNonTerminalRule() {
		Rule r1 = new Terminal("terminal");
		new NonTerminal("some rule", r1).visit(visitor);
		assertEquals("some rule-" + NonTerminal.class.getName() + "\n-terminal-" + Terminal.class.getName() + "\n",
				visitor.toString());
	}

	@Test
	public void shouldHandleOptionalRule() {
		Rule r1 = new Terminal("terminal");
		new Optional(r1).visit(visitor);
		assertEquals(Optional.class.getName() + "\n-terminal-" + Terminal.class.getName() + "\n", visitor.toString());
	}

	@Test
	public void shouldHandleRepetitionRule() {
		Rule r1 = new Terminal("terminal");
		new Repetition(r1).visit(visitor);
		assertEquals(Repetition.class.getName() + "\n-terminal-" + Terminal.class.getName() + "\n", visitor.toString());
	}

	@Test
	public void shouldHandleTerminalRule() {
		new Terminal("some", "terminal").visit(visitor);
		assertEquals("some-" + Terminal.class.getName() + "\nterminal-" + Terminal.class.getName() + "\n",
				visitor.toString());
	}

	@Test
	public void shouldHandleRules() {
		Rule digit = new NonTerminal("digit", new Terminal('0', '9'));
		Rule digits = new Repetition(digit);
		Rule repeat = new NonTerminal("repeat",
				new Disjunction(new Conjunction(digit, digits), new Conjunction(digits, new Terminal("*"), digits)));

		List<String> lines = new ArrayList<>();
		lines.add("repeat-" + NonTerminal.class.getName());
		lines.add("-" + Disjunction.class.getName());
		lines.add("--" + Conjunction.class.getName());
		lines.add("---digit-" + NonTerminal.class.getName());
		for (int i = 0; i <= 9; i++) {
			lines.add("----" + i + "-" + Terminal.class.getName());
		}
		lines.add("---" + Repetition.class.getName());
		lines.add("----digit-" + NonTerminal.class.getName());
		for (int i = 0; i <= 9; i++) {
			lines.add("-----" + i + "-" + Terminal.class.getName());
		}
		lines.add("--" + Conjunction.class.getName());
		lines.add("---" + Repetition.class.getName());
		lines.add("----digit-" + NonTerminal.class.getName());
		for (int i = 0; i <= 9; i++) {
			lines.add("-----" + i + "-" + Terminal.class.getName());
		}
		lines.add("---*-" + Terminal.class.getName());
		lines.add("---" + Repetition.class.getName());
		lines.add("----digit-" + NonTerminal.class.getName());
		for (int i = 0; i <= 9; i++) {
			lines.add("-----" + i + "-" + Terminal.class.getName());
		}

		repeat.visit(visitor);
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

	@Test
	public void shouldHandleCyclicRules() {
		Rule r1 = new Conjunction();
		Rule t1 = new Terminal("t1");
		Rule n1 = new NonTerminal("cyclic", r1);
		r1.addChild(t1);
		r1.addChild(n1);
		
		List<String> lines = new ArrayList<>();
		lines.add("cyclic-" + NonTerminal.class.getName());
		lines.add("-" + Conjunction.class.getName());
		lines.add("--t1-" + Terminal.class.getName());
		lines.add("--...-cyclic-" + NonTerminal.class.getName());

		n1.visit(visitor);
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

}
