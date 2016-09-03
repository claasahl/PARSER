package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.State;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The JUnit test for class {@link RepetitionTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class RepetitionTest extends DecoratorTest {

	@Override
	protected Decorator build(Rule rule) {
		return new Repetition(rule);
	}

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] { buildTestRule("decorated", new TerminalNode("decorated")) };
	}

	@Override
	protected State processibleState() {
		return buildState("decorated");
	}

	@Override
	protected State unprocessibleState() {
		return null;
	}

	@Test
	public void shouldProcessMultipleTokens() {
		State state = buildState("decorateddecoratedinvalid");
		Rule rule = build(defaultChildren());
		assertNotNull(rule.process(state));
		assertEquals("decorateddecorated", state.getProcessedData());
		assertEquals("invalid", state.getUnprocessedData());

		assertNotNull(rule.process(state));
		assertEquals("decorateddecorated", state.getProcessedData());
		assertEquals("invalid", state.getUnprocessedData());
	}
	
	@Test
	public void shouldReturnIntermediateNode() {
		Rule rule = build(defaultChildren());
		Node node = rule.process(processibleState());
		assertEquals(IntermediateNode.class, node.getClass());
	}
	
	@Test
	public void shouldReturnAppropriateNodeTree() {
		Rule rule = build(defaultChildren());
		Node node = rule.process(processibleState());
		
		Iterator<Node> children = node.iterator();
		TerminalNode child = (TerminalNode) children.next();
		assertEquals("decorated", child.getTerminal());
		assertFalse(children.hasNext());
	}
	
	@Test
	public void shouldRepeatAtMostOnce() {
		State state = buildState("rerere??");
		Rule child = buildTestRule("re", new TerminalNode("re"));
		Rule rule = new Repetition(child, 0, 1);
		assertNotNull(rule.process(state));
		assertEquals("re", state.getProcessedData());
		assertEquals("rere??", state.getUnprocessedData());
	}

	@Test
	public void shouldRepeatExactlyTwice() {
		State state = buildState("rerere??");
		Rule child = buildTestRule("re", new TerminalNode("re"));
		Rule rule = new Repetition(child, 2, 2);
		assertNotNull(rule.process(state));
		assertEquals("rere", state.getProcessedData());
		assertEquals("re??", state.getUnprocessedData());
	}
	
	@Test
	public void shouldRepeatAtLeastThrice() {
		State state = buildState("rererererererere??");
		Rule child = buildTestRule("re", new TerminalNode("re"));
		Rule rule = new Repetition(child, 2, Integer.MAX_VALUE);
		assertNotNull(rule.process(state));
		assertEquals("rererererererere", state.getProcessedData());
		assertEquals("??", state.getUnprocessedData());
	}

	@Test
	public void shouldRepeatWithinRange() {
		State state = buildState("rererererererere??");
		Rule child = buildTestRule("re", new TerminalNode("re"));
		Rule rule = new Repetition(child, 2, 4);
		assertNotNull(rule.process(state));
		assertEquals("rererere", state.getProcessedData());
		assertEquals("rererere??", state.getUnprocessedData());
	}
}
