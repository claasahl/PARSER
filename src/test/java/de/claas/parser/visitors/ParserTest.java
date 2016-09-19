package de.claas.parser.visitors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.State;
import de.claas.parser.exceptions.CyclicRuleException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

/**
 *
 * The JUnit test for class {@link Parser}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class ParserTest extends RuleVisitorTest {

	private static final String WORLD = "world";
	private static final String HELLO = "hello";
	private static final String DATA = HELLO + WORLD;
	private static final Rule[] CHILDREN = new Rule[] { new Terminal(HELLO), new Terminal(WORLD) };

	/**
	 * Returns an instantiated {@link Parser} class with the specified data.
	 * 
	 * @param data
	 *            the data
	 * @return an instantiated {@link Parser} class with the specified data
	 */
	private static Parser build(String data) {
		return new Parser(new State(data));
	}

	@Override
	public void shouldHandleNoRule() {
		Parser parser = build(DATA);
		assertNull(parser.getResult());
	}

	@Override
	public void shouldHandleConjunctionRule() {
		Rule rule = new Conjunction(CHILDREN);
		Parser parser = build(DATA);
		rule.visit(parser);

		Node expected = new IntermediateNode();
		expected.addChild(new TerminalNode(HELLO));
		expected.addChild(new TerminalNode(WORLD));
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void conjunctionShouldRequireChildren() {
		Rule rule = new Conjunction();
		Parser parser = build(DATA);
		rule.visit(parser);
		assertNull(parser.getResult());

		parser = build("");
		rule.visit(parser);
		assertNull(parser.getResult());
	}

	@Test
	public void conjunctionShouldFailIfAnyChildFails() {
		Rule rule = new Conjunction(CHILDREN);
		Parser parser = build(HELLO + "invalid");
		rule.visit(parser);
		assertNull(parser.getResult());

		parser = build("invalid" + WORLD);
		rule.visit(parser);
		assertNull(parser.getResult());
	}

	@Override
	public void shouldHandleDisjunctionRule() {
		Rule rule = new Disjunction(CHILDREN);
		Parser parser = build(DATA);
		rule.visit(parser);

		Node expected = new IntermediateNode();
		expected.addChild(new TerminalNode(HELLO));
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void disjunctionShouldRequireChildren() {
		Rule rule = new Disjunction();
		Parser parser = build(DATA);
		rule.visit(parser);
		assertNull(parser.getResult());

		parser = build("");
		rule.visit(parser);
		assertNull(parser.getResult());
	}

	@Test
	public void disjunctionShouldSucceedIfAnyChildSucceeds() {
		Rule rule = new Disjunction(CHILDREN);
		Parser parser = build(HELLO);
		rule.visit(parser);
		Node expected = new IntermediateNode();
		expected.addChild(new TerminalNode(HELLO));
		assertEquals(expected, parser.getResult());

		parser = build(WORLD);
		rule.visit(parser);
		expected = new IntermediateNode();
		expected.addChild(new TerminalNode(WORLD));
		assertEquals(expected, parser.getResult());
	}

	@Override
	public void shouldHandleNonTerminalRule() {
		Rule rule = new NonTerminal("some name", new Conjunction(CHILDREN));
		Parser parser = build(DATA);
		rule.visit(parser);

		Node expected = new NonTerminalNode("some name");
		Node conjunction = new IntermediateNode();
		conjunction.addChild(new TerminalNode(HELLO));
		conjunction.addChild(new TerminalNode(WORLD));
		expected.addChild(conjunction);
		assertEquals(expected, parser.getResult());
	}

	@Override
	public void shouldHandleOptionalRule() {
		Rule rule = new Optional(new Conjunction(CHILDREN));
		Parser parser = build(DATA);
		rule.visit(parser);

		Node expected = new IntermediateNode();
		Node conjunction = new IntermediateNode();
		conjunction.addChild(new TerminalNode(HELLO));
		conjunction.addChild(new TerminalNode(WORLD));
		expected.addChild(conjunction);
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void optionalShouldRepeatAtMostOnc() {
		Rule rule = new Optional(new Terminal(HELLO));
		Parser parser = build(HELLO + HELLO + WORLD);
		rule.visit(parser);

		Node expected = new IntermediateNode();
		expected.addChild(new TerminalNode(HELLO));
		assertEquals(expected, parser.getResult());

		rule.visit(parser);
		assertEquals(expected, parser.getResult());

		rule.visit(parser);
		expected = new IntermediateNode();
		assertEquals(expected, parser.getResult());
	}

	@Override
	public void shouldHandleRepetitionRule() {
		Rule rule = new Repetition(new Disjunction(CHILDREN));
		Parser parser = build(DATA);
		rule.visit(parser);

		Node expected = new IntermediateNode();
		Node disjunction1 = new IntermediateNode();
		disjunction1.addChild(new TerminalNode(HELLO));
		expected.addChild(disjunction1);
		Node disjunction2 = new IntermediateNode();
		disjunction2.addChild(new TerminalNode(WORLD));
		expected.addChild(disjunction2);
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void repetitionShouldRepeat() {
		Rule rule = new Repetition(new Terminal("re"));
		Parser parser = build("rererererererere??");
		rule.visit(parser);

		Node expected = new IntermediateNode();
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void repetitionShouldRepeatAtMostOnce() {
		Rule rule = new Repetition(new Terminal("re"), 0, 1);
		Parser parser = build("rerere??");
		rule.visit(parser);

		Node expected = new IntermediateNode();
		expected.addChild(new TerminalNode("re"));
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void repetitionShouldRepeatExactlyTwice() {
		Rule rule = new Repetition(new Terminal("re"), 2, 2);
		Parser parser = build("rerere??");
		rule.visit(parser);

		Node expected = new IntermediateNode();
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void repetitionShouldRepeatAtLeastThrice() {
		Rule rule = new Repetition(new Terminal("re"), 3, Integer.MAX_VALUE);
		Parser parser = build("rererererererere??");
		rule.visit(parser);

		Node expected = new IntermediateNode();
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void repetitionShouldRepeatWithinRange() {
		Rule rule = new Repetition(new Terminal("re"), 2, 4);
		Parser parser = build("rererererererere??");
		rule.visit(parser);

		Node expected = new IntermediateNode();
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		expected.addChild(new TerminalNode("re"));
		assertEquals(expected, parser.getResult());
	}

	@Override
	public void shouldHandleTerminalRule() {
		Rule rule = new Terminal(DATA);
		Parser parser = build(DATA);
		rule.visit(parser);

		Node expected = new TerminalNode(DATA);
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void terminalShouldSucceedIfAnyTerminalMatches() {
		Terminal rule = new Terminal(HELLO, WORLD, "b");
		Parser parser = build(HELLO);
		rule.visit(parser);
		Node expected = new TerminalNode(HELLO);
		assertEquals(expected, parser.getResult());

		parser = build(WORLD);
		rule.visit(parser);
		expected = new TerminalNode(WORLD);
		assertEquals(expected, parser.getResult());

		parser = build("b");
		rule.visit(parser);
		expected = new TerminalNode("b");
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void terminalShouldFailWithoutTerminals() {
		Terminal rule = new Terminal();
		Parser parser = build(HELLO + WORLD);
		rule.visit(parser);
		assertNull(parser.getResult());
	}
	
	@Test
	public void terminalShouldHaveCaseInsenstiveTerminals() {
		Terminal rule = new Terminal(false, "hello");
		Parser parser = build("HELLO");
		rule.visit(parser);
		Node expected = new TerminalNode("HELLO");
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void terminalShouldSucceedIfTerminalsAreWithinRange() {
		Terminal rule = new Terminal('a', 'z');
		Parser parser = build("a");
		rule.visit(parser);
		Node expected = new TerminalNode("a");
		assertEquals(expected, parser.getResult());

		parser = build("b");
		rule.visit(parser);
		expected = new TerminalNode("b");
		assertEquals(expected, parser.getResult());

		parser = build("x");
		rule.visit(parser);
		expected = new TerminalNode("x");
		assertEquals(expected, parser.getResult());

		parser = build("z");
		rule.visit(parser);
		expected = new TerminalNode("z");
		assertEquals(expected, parser.getResult());
	}

	@Test
	public void terminalShouldFailIfTerminalsAreOutsideOfRange() {
		Terminal rule = new Terminal('a', 'z');
		Parser parser = build("A");
		rule.visit(parser);
		assertNull(parser.getResult());
	}
	
	@Test
	public void terminalShouldHaveCaseInsenstiveRange() {
		Terminal rule = new Terminal(false, 'a', 'z');
		Parser parser = build("A");
		rule.visit(parser);
		Node expected = new TerminalNode("A");
		assertEquals(expected, parser.getResult());
	}

	@Override
	public void shouldHandleRules() {
		Rule plus = new Terminal("+");
		Rule digit = new NonTerminal("digit", new Terminal('0', '9'));
		Rule number = new NonTerminal("number", new Conjunction(new Optional(plus), new Repetition(digit, 1, 10)));
		Parser parser = build("+321");
		number.visit(parser);

		Node expected = new NonTerminalNode("number");
		Node conjunction = new IntermediateNode();
		Node optional = new IntermediateNode();
		optional.addChild(new TerminalNode("+"));
		conjunction.addChild(optional);
		Node repetition = new IntermediateNode();
		Node digit3 = new NonTerminalNode("digit");
		digit3.addChild(new TerminalNode("3"));
		repetition.addChild(digit3);
		Node digit2 = new NonTerminalNode("digit");
		digit2.addChild(new TerminalNode("2"));
		repetition.addChild(digit2);
		Node digit1 = new NonTerminalNode("digit");
		digit1.addChild(new TerminalNode("1"));
		repetition.addChild(digit1);
		conjunction.addChild(repetition);
		expected.addChild(conjunction);
		assertEquals(expected, parser.getResult());
	}

	@Override
	@Test(expected = CyclicRuleException.class)
	public void shouldHandleCyclicRepetitionRule() {
		Rule rule = new Repetition(new Conjunction());
		Rule child = new Repetition(rule);
		rule.addChild(child);

		Parser parser = build(DATA);
		rule.visit(parser);
	}

	@Override
	@Test(expected = CyclicRuleException.class)
	public void shouldHandleCyclicOptionalRule() {
		Rule rule = new Optional(new Conjunction());
		Rule child = new Optional(rule);
		rule.addChild(child);

		Parser parser = build(DATA);
		rule.visit(parser);
	}

	@Override
	@Test(expected = CyclicRuleException.class)
	public void shouldHandleCyclicNonTerminalRule() {
		Rule rule = new NonTerminal("rule", new Conjunction());
		Rule child = new NonTerminal("child", rule);
		rule.addChild(child);

		Parser parser = build(DATA);
		rule.visit(parser);
	}

	@Override
	@Test(expected = CyclicRuleException.class)
	public void shouldHandleCyclicDisjunctionRule() {
		Rule rule = new Disjunction();
		Rule child = new Disjunction(rule);
		rule.addChild(child);

		Parser parser = build(DATA);
		rule.visit(parser);
	}

	@Override
	@Test(expected = CyclicRuleException.class)
	public void shouldHandleCyclicConjunctionRule() {
		Rule rule = new Conjunction();
		Rule child = new Conjunction(rule);
		rule.addChild(child);

		Parser parser = build(DATA);
		rule.visit(parser);
	}

}
