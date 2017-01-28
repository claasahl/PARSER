package de.claas.parser.grammars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.builders.AugmentedBackusNaurBuilder;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.visitors.InterpreterTest;
import de.claas.parser.visitors.RuleToString;

/**
 * The JUnit test for class {@link AugmentedBackusNaurInterpreter}. It is
 * intended to collect and document a set of test cases for the tested class.
 * Please refer to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 */
public class AugmentedBackusNaurInterpreterTest extends InterpreterTest<Rule> {

	@Override
	protected AugmentedBackusNaurInterpreter build() {
		return new AugmentedBackusNaurInterpreter();
	}

	@Override
	protected NonTerminalNode getNonTerminalNode() {
		return new NonTerminalNode("rulelist");
	}

	@Override
	public void shouldHandleNodes() {
		NonTerminal de = helloWorld("de", "hallo", "welt");
		NonTerminal en = helloWorld("en", "hello", "world");
		NonTerminal se = helloWorld("se", "hallå", "värld");
		NonTerminal es = helloWorld("es", "hola", "mundo");
		NonTerminal expected = new NonTerminal("hello-world", new Disjunction(de, en, se, es));

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = new AugmentedBackusNaurBuilder().rule(expected).rule(es).rule(se).rule(en).rule(de).build();
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

	@Test
	public void shouldBeConjunction() {
		Rule hel = new CharacterValue("hel");
		Rule lo = new CharacterValue("lo");
		Rule rule = new Conjunction(hel, lo);
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = new AugmentedBackusNaurBuilder().rule(expected).build();
		grammar.visit(interpreter);

		RuleToString r1 = new RuleToString();
		expected.visit(r1);
		RuleToString r2 = new RuleToString();
		interpreter.getResult().visit(r2);
		assertEquals(r1.toString(), r2.toString());

		assertEquals(expected, interpreter.getResult());
	}

	@Test
	public void shouldBeDisjunction() {
		Rule hel = new CharacterValue("hel");
		Rule lo = new CharacterValue("lo");
		Rule rule = new Disjunction(hel, lo);
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = new AugmentedBackusNaurBuilder().rule(expected).build();
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

	@Test
	public void shouldBeNonTerminal() {
		Rule hello = new CharacterValue("hello");
		NonTerminal rule = new NonTerminal("hello", hello);
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = new AugmentedBackusNaurBuilder().rule(expected).rule(rule).build();
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

	@Test
	public void shouldBeOptional() {
		Rule hel = new CharacterValue("hel");
		Rule rule = new Optional(hel);
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = new AugmentedBackusNaurBuilder().rule(expected).build();
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

	@Test
	public void shouldBeRepetition() {
		Rule hel = new CharacterValue("hel");
		Rule rule = new Repetition(hel);
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = new AugmentedBackusNaurBuilder().rule(expected).build();
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

	@Test
	public void shouldBeTerminal() {
		Rule rule = new CharacterValue("hello world");
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = new AugmentedBackusNaurBuilder().rule(expected).build();
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

	/**
	 * A support function that returns a "hello world"-rule for the specified
	 * language.
	 * 
	 * @param language
	 *            the language
	 * @param hello
	 *            the word "hello" in the specified language
	 * @param world
	 *            the word "world" in the specified language
	 * @return a "hello world"-rule for the specified language
	 */
	private static NonTerminal helloWorld(String language, String hello, String world) {
		Rule part1 = new CharacterValue(hello);
		Rule space = new CharacterValue(" ");
		Rule part2 = new CharacterValue(world);
		Rule rule = new Conjunction(part1, space, part2);
		return new NonTerminal(language, rule);
	}

}
