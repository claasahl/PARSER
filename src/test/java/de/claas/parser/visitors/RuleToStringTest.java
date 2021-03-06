package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.NumberValue;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;

/**
 * The JUnit test for class {@link RuleToString}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 */
public class RuleToStringTest extends RuleVisitorTest {

	private RuleVisitor visitor;

	@Before
	public void before() {
		this.visitor = new RuleToString("  ", "\n");
	}

	@Override
	public void shouldHandleNoRule() {
		assertEquals("", this.visitor.toString());
	}

	@Override
	public void shouldHandleConjunctionRule() {
		Rule r1 = new CharacterValue("t");
		new Conjunction(r1).visit(this.visitor);

		List<String> lines = new ArrayList<>();
		lines.add("Conjunction");
		lines.add("  CharacterValue:t");
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

	@Override
	public void shouldHandleDisjunctionRule() {
		Rule r1 = new CharacterValue("t");
		new Disjunction(r1).visit(this.visitor);

		List<String> lines = new ArrayList<>();
		lines.add("Disjunction");
		lines.add("  CharacterValue:t");
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

	@Override
	public void shouldHandleNonTerminalRule() {
		Rule r1 = new CharacterValue("t");
		new NonTerminal("some rule", r1).visit(this.visitor);

		List<String> lines = new ArrayList<>();
		lines.add("NonTerminal:some rule");
		lines.add("  CharacterValue:t");
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

	@Override
	public void shouldHandleOptionalRule() {
		Rule r1 = new CharacterValue("t");
		new Optional(r1).visit(this.visitor);

		List<String> lines = new ArrayList<>();
		lines.add("Optional");
		lines.add("  CharacterValue:t");
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

	@Override
	public void shouldHandleRepetitionRule() {
		Rule r1 = new CharacterValue("t");
		new Repetition(r1).visit(this.visitor);

		List<String> lines = new ArrayList<>();
		lines.add("Repetition");
		lines.add("  CharacterValue:t");
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

	@Override
	public void shouldHandleTerminalRule() {
		new CharacterValue("some-terminal").visit(this.visitor);

		List<String> lines = new ArrayList<>();
		lines.add("CharacterValue:some-terminal");
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

	@Override
	public void shouldHandleRules() {
		Rule asterics = new CharacterValue("*");
		Rule digit = new NonTerminal("digit", new NumberValue(16, '0', '9'));
		Rule digits = new Repetition(digit);
		Rule repeat = new NonTerminal("repeat",
				new Disjunction(new Conjunction(digit, digits), new Conjunction(digits, asterics, digits)));

		List<String> lines = new ArrayList<>();
		lines.add("NonTerminal:repeat");
		lines.add("  Disjunction");
		lines.add("    Conjunction");
		lines.add("      NonTerminal:digit");
		lines.add("        NumberValue:%x30-39");
		lines.add("      Repetition");
		lines.add("        NonTerminal:digit");
		lines.add("          NumberValue:%x30-39");
		lines.add("    Conjunction");
		lines.add("      Repetition");
		lines.add("        NonTerminal:digit");
		lines.add("          NumberValue:%x30-39");
		lines.add("      CharacterValue:*");
		lines.add("      Repetition");
		lines.add("        NonTerminal:digit");
		lines.add("          NumberValue:%x30-39");

		repeat.visit(this.visitor);
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

	@Override
	public void shouldHandleCyclicRepetitionRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Repetition(r0);
		r0.addChild(r1);
		r1.visit(this.visitor);

		List<String> lines = new ArrayList<>();
		lines.add("Repetition");
		lines.add("  Conjunction");
		lines.add("    Repetition");
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

	@Override
	public void shouldHandleCyclicOptionalRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Optional(r0);
		r0.addChild(r1);
		r1.visit(this.visitor);

		List<String> lines = new ArrayList<>();
		lines.add("Optional");
		lines.add("  Conjunction");
		lines.add("    Optional");
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

	@Override
	public void shouldHandleCyclicNonTerminalRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new NonTerminal("rulename", r0);
		r0.addChild(r1);
		r1.visit(this.visitor);

		List<String> lines = new ArrayList<>();
		lines.add("NonTerminal:rulename");
		lines.add("  Conjunction");
		lines.add("    NonTerminal:rulename");
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

	@Override
	public void shouldHandleCyclicDisjunctionRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Disjunction(r0);
		r0.addChild(r1);
		r1.visit(this.visitor);

		List<String> lines = new ArrayList<>();
		lines.add("Disjunction");
		lines.add("  Conjunction");
		lines.add("    Disjunction");
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

	@Override
	public void shouldHandleCyclicConjunctionRule() {
		Rule r0 = new Conjunction();
		Rule r1 = new Conjunction(r0);
		r0.addChild(r1);
		r1.visit(this.visitor);

		List<String> lines = new ArrayList<>();
		lines.add("Conjunction");
		lines.add("  Conjunction");
		assertEquals(String.join("\n", lines) + "\n", this.visitor.toString());
	}

}
