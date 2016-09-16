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

	@Override
	public void shouldHandleDisjunctionRule() {
		Rule rule = new Disjunction(CHILDREN);
		Parser parser = build(DATA);
		rule.visit(parser);

		Node expected = new IntermediateNode();
		expected.addChild(new TerminalNode(HELLO));
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

	@Override
	public void shouldHandleTerminalRule() {
		Rule rule = new Terminal(DATA);
		Parser parser = build(DATA);
		rule.visit(parser);

		Node expected = new TerminalNode(DATA);
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
