package de.claas.parser.grammars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.grammars.AugmentedBackusNaurInterpreter;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;
import de.claas.parser.visitors.InterpreterTest;
import de.claas.parser.visitors.RuleToString;

/**
 *
 * The JUnit test for class {@link AugmentedBackusNaurInterpreter}. It is
 * intended to collect and document a set of test cases for the tested class.
 * Please refer to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
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
		NonTerminal de = new NonTerminal("de",
				new Conjunction(new Terminal("hallo"), new Terminal(" "), new Terminal("welt")));
		NonTerminal en = new NonTerminal("en",
				new Conjunction(new Terminal("hello"), new Terminal(" "), new Terminal("world")));
		NonTerminal se = new NonTerminal("se",
				new Conjunction(new Terminal("hallå"), new Terminal(" "), new Terminal("värld")));
		NonTerminal es = new NonTerminal("es",
				new Conjunction(new Terminal("hola"), new Terminal(" "), new Terminal("mundo")));
		NonTerminal expected = new NonTerminal("hello-world", new Disjunction(de, en, se, es));

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = AugmentedBackusNaurTest.generateNodes(expected, es, se, en, de);
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

	@Test
	public void shouldBeConjunction() {
		Rule hel = new Terminal("hel");
		Rule lo = new Terminal("lo");
		Rule rule = new Conjunction(hel, lo);
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = AugmentedBackusNaurTest.generateNodes(expected);
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
		Rule hel = new Terminal("hel");
		Rule lo = new Terminal("lo");
		Rule rule = new Disjunction(hel, lo);
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = AugmentedBackusNaurTest.generateNodes(expected);
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

	@Test
	public void shouldBeNonTerminal() {
		Rule hello = new Terminal("hello");
		NonTerminal rule = new NonTerminal("hello", hello);
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = AugmentedBackusNaurTest.generateNodes(expected, rule);
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

	@Test
	public void shouldBeOptional() {
		Rule hel = new Terminal("hel");
		Rule rule = new Optional(hel);
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = AugmentedBackusNaurTest.generateNodes(expected);
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

	@Test
	public void shouldBeRepetition() {
		Rule hel = new Terminal("hel");
		Rule rule = new Repetition(hel);
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = AugmentedBackusNaurTest.generateNodes(expected);
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

	@Test
	public void shouldBeTerminal() {
		Rule rule = new Terminal("hello world");
		NonTerminal expected = new NonTerminal("rule", rule);

		AugmentedBackusNaurInterpreter interpreter = build();
		Node grammar = AugmentedBackusNaurTest.generateNodes(expected);
		grammar.visit(interpreter);
		assertEquals(expected, interpreter.getResult());
	}

}
