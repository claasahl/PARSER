package de.claas.parser.visitors;

import static org.junit.Assert.*;

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
		visitor = new RuleToString("  ", "\n");
	}

	@Test
	public void shouldHandleNoRule() {
		assertEquals("", visitor.toString());
	}

	@Test
	public void shouldHandleConjunctionRule() {
		Rule r1 = new Terminal("t");
		new Conjunction(r1).visit(visitor);
		
		List<String> lines = new ArrayList<>();
		lines.add("Conjunction");
		lines.add("  Terminal:t");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

	@Test
	public void shouldHandleDisjunctionRule() {
		Rule r1 = new Terminal("t");
		new Disjunction(r1).visit(visitor);
		
		List<String> lines = new ArrayList<>();
		lines.add("Disjunction");
		lines.add("  Terminal:t");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

	@Test
	public void shouldHandleNonTerminalRule() {
		Rule r1 = new Terminal("t");
		new NonTerminal("some rule", r1).visit(visitor);
		
		List<String> lines = new ArrayList<>();
		lines.add("NonTerminal:some rule");
		lines.add("  Terminal:t");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

	@Test
	public void shouldHandleOptionalRule() {
		Rule r1 = new Terminal("t");
		new Optional(r1).visit(visitor);

		List<String> lines = new ArrayList<>();
		lines.add("Optional");
		lines.add("  Terminal:t");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

	@Test
	public void shouldHandleRepetitionRule() {
		Rule r1 = new Terminal("t");
		new Repetition(r1).visit(visitor);
		
		List<String> lines = new ArrayList<>();
		lines.add("Repetition");
		lines.add("  Terminal:t");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

	@Test
	public void shouldHandleTerminalRule() {
		new Terminal("some", "terminal").visit(visitor);

		List<String> lines = new ArrayList<>();
		lines.add("Terminal:some");
		lines.add("Terminal:terminal");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

	@Test
	public void shouldHandleRules() {
		Rule digit = new NonTerminal("digit", new Terminal('0', '9'));
		Rule digits = new Repetition(digit);
		Rule repeat = new NonTerminal("repeat",
				new Disjunction(new Conjunction(digit, digits), new Conjunction(digits, new Terminal("*"), digits)));

		List<String> lines = new ArrayList<>();
		lines.add("NonTerminal:repeat");
		lines.add("  Disjunction");
		lines.add("    Conjunction");
		lines.add("      NonTerminal:digit");
		for (int i = 0; i <= 9; i++) {
			lines.add("        Terminal:" + i);
		}
		lines.add("      Repetition");
		lines.add("        NonTerminal:digit");
		lines.add("    Conjunction");
		lines.add("      Repetition");
		lines.add("      Terminal:*");
		lines.add("      Repetition");

		repeat.visit(visitor);
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}
	
	@Test
	public void shouldHandleCyclicRepetitionRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Repetition(r0);
		r0.addChild(r1);
		r1.visit(visitor);
		
		List<String> lines = new ArrayList<>();
		lines.add("Repetition");
		lines.add("  Conjunction");
		lines.add("    Repetition");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}
	
	@Test
	public void shouldHandleCyclicOptionalRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Optional(r0);
		r0.addChild(r1);
		r1.visit(visitor);
		
		List<String> lines = new ArrayList<>();
		lines.add("Optional");
		lines.add("  Conjunction");
		lines.add("    Optional");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}
	
	@Test
	public void shouldHandleCyclicNonTerminalRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new NonTerminal("rulename", r0);
		r0.addChild(r1);
		r1.visit(visitor);
		
		List<String> lines = new ArrayList<>();
		lines.add("NonTerminal:rulename");
		lines.add("  Conjunction");
		lines.add("    NonTerminal:rulename");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}
	
	@Test
	public void shouldHandleCyclicDisjunctionRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Disjunction(r0);
		r0.addChild(r1);
		r1.visit(visitor);
		
		List<String> lines = new ArrayList<>();
		lines.add("Disjunction");
		lines.add("  Conjunction");
		lines.add("    Disjunction");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}
	
	@Test
	public void shouldHandleCyclicConjunctionRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Conjunction(r0);
		r0.addChild(r1);
		r1.visit(visitor);
		
		List<String> lines = new ArrayList<>();
		lines.add("Conjunction");
		lines.add("  Conjunction");
		lines.add("    Conjunction");
		assertEquals(lines.stream().collect(Collectors.joining("\n")) + "\n", visitor.toString());
	}

}
