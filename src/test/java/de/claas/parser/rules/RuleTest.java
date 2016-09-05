package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.State;

/**
 * 
 * The JUnit test for class {@link RuleTest}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public abstract class RuleTest {

	/**
	 * Returns an instantiated {@link Rule} class with the given children. If
	 * appropriate, the instance is configured with default values.
	 *
	 * @param children
	 *            the children
	 * @return an instantiated {@link Rule} class with the given children
	 */
	protected abstract Rule build(Rule... children);

	/**
	 * Returns the default children for this rule. These children are used in
	 * combination with the states returned by {@link #processibleState()} and
	 * {@link #unprocessibleState()} in order to test whether the general
	 * contract of {@link Rule#process(State)} is maintained.
	 * 
	 * @return the default children for this rule
	 */
	protected abstract Rule[] defaultChildren();

	/**
	 * Returns an instantiated {@link State} class that can be processed with
	 * the default children. Returning <code>null</code> signals that the tested
	 * rule will always fail to process with the default children (regardless of
	 * the state).
	 * 
	 * @return an instantiated {@link State} class that can be processed with
	 *         the default children
	 */
	protected abstract State processibleState();

	/**
	 * Returns an instantiated {@link State} class that cannot be processed with
	 * the default children. Returning <code>null</code> signals that the tested
	 * rule will always successfully process with the default children
	 * (regardless of the state).
	 * 
	 * @return an instantiated {@link State} class that cannot be processed with
	 *         the default children
	 */
	protected abstract State unprocessibleState();

	/**
	 * Returns an instantiated {@link State} class with the specified tokens.
	 * The first token, that is past into this function, will also be the first
	 * token to be processed. Respectively, the last token will be processed
	 * last.
	 * 
	 * @param tokens
	 *            the (unprocessed) tokens
	 * @return an instantiated {@link State} class with the specified tokens
	 */
	protected State buildState(String pattern) {
		return new State(pattern);
	}

	/**
	 * Returns an instantiated {@link TestRule} class. Where appropriate, the
	 * instance can be configured to have an arbitrary number of children. The
	 * purpose of this function is to build {@link Rule} instances that expose
	 * their internal state and allow for easier testing.
	 * 
	 * @param name
	 *            the name
	 * @param output
	 *            the node returned by {@link #process(State)}
	 * @param children
	 *            the children
	 * @return an instantiated {@link TestRule} class
	 */
	protected TestRule buildTestRule(String name, Node output, Rule... children) {
		return new TestRule(name, output, children);
	}

	@Test
	public void shouldHaveNoChildren() {
		Rule rule = build();
		assertFalse(rule.hasChildren());
	}

	@Test
	public void shouldHaveChildren() {
		Rule rule = build();
		Rule child = build();
		assertTrue(rule.addChild(child));
		assertTrue(rule.hasChildren());
	}

	@Test
	public void shouldHaveEmptyIterator() {
		Rule rule = build();
		assertFalse(rule.iterator().hasNext());
	}

	@Test
	public void shouldHaveNonEmptyIterator() {
		Rule rule = build();
		Rule child = build();
		assertTrue(rule.addChild(child));
		assertTrue(rule.iterator().hasNext());
	}

	@Test
	public void shouldManageChildren() {
		Rule rule = build();
		Rule childA = buildTestRule("A", null);
		Rule childB = buildTestRule("B", null);
		Rule childC = buildTestRule("C", null);
		assertTrue(rule.addChild(childA));
		assertTrue(rule.addChild(childB));
		assertTrue(rule.addChild(childC));

		// everybody there?
		Iterator<Rule> iterator = rule.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(childA, iterator.next());
		assertEquals(childB, iterator.next());
		assertEquals(childC, iterator.next());
		assertFalse(iterator.hasNext());

		// remove middle child
		assertTrue(rule.removeChild(childB));
		iterator = rule.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(childA, iterator.next());
		assertEquals(childC, iterator.next());
		assertFalse(iterator.hasNext());

		// remove last child
		assertTrue(rule.removeChild(childC));
		iterator = rule.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(childA, iterator.next());
		assertFalse(iterator.hasNext());

		// remove first child
		assertTrue(rule.removeChild(childA));
		iterator = rule.iterator();
		assertFalse(iterator.hasNext());

		// cannot remove children that arn't there ;)
		assertFalse(rule.removeChild(childA));
		assertFalse(rule.removeChild(childB));
		assertFalse(rule.removeChild(childC));
	}

	@Test
	public void addChildShouldHandleNull() {
		Rule rule = build();
		assertFalse(rule.addChild(null));
	}

	@Test
	public void removeChildShouldHandleNull() {
		Rule rule = build();
		assertFalse(rule.removeChild(null));
	}

	@Test
	public void shouldProcess() {
		// see #processibleState()
		if (processibleState() == null)
			return;

		State state = processibleState();
		String processedPattern = state.getProcessedData();
		String unprocessedPattern = state.getUnprocessedData();
		int processingGroups = state.getGroups();

		Rule rule = build(defaultChildren());
		assertNotNull(rule.process(state));
		assertTrue(state.getProcessedData().startsWith(processedPattern));
		assertTrue(state.getProcessedData().length() >= processedPattern.length());
		assertTrue(unprocessedPattern.endsWith(state.getUnprocessedData()));
		assertTrue(unprocessedPattern.length() >= state.getUnprocessedData().length());
		assertEquals(processingGroups, state.getGroups());
	}

	@Test
	public void shouldNotProcess() {
		// see #unprocessibleState()
		if (unprocessibleState() == null)
			return;

		State state = unprocessibleState();
		String processedPattern = state.getProcessedData();
		String unprocessedPattern = state.getUnprocessedData();
		int processingGroups = state.getGroups();

		Rule rule = build(defaultChildren());
		assertNull(rule.process(state));
		assertEquals(processedPattern, state.getProcessedData());
		assertEquals(unprocessedPattern, state.getUnprocessedData());
		assertEquals(processingGroups, state.getGroups());
	}

	@Test
	public void shouldHandleEmptyState() {
		// see #unprocessibleState()
		if (unprocessibleState() == null)
			return;

		State state = buildState("");
		String processedPattern = state.getProcessedData();
		String unprocessedPattern = state.getUnprocessedData();
		int processingGroups = state.getGroups();

		Rule rule = build(defaultChildren());
		assertNull(rule.process(state));
		assertEquals(processedPattern, state.getProcessedData());
		assertEquals(unprocessedPattern, state.getUnprocessedData());
		assertEquals(processingGroups, state.getGroups());
	}
	
	@Test
	public void implementationOfEqualsShouldHandleNull() {
		Rule rule = build(defaultChildren());
		assertFalse(rule.equals(null));
	}
	
	@Test
	public void implementationOfEqualsShouldBeReflexive() {
		Rule rule = build(defaultChildren());
		assertTrue(rule.equals(rule));
	}
	
	@Test
	public void implementationOfEqualsShouldBeSymmetric() {
		Rule ruleA = build(defaultChildren());
		Rule ruleB = build(defaultChildren());
		assertTrue(ruleA.equals(ruleB));
		assertTrue(ruleB.equals(ruleA));
	}
	
	@Test
	public void implementationOfEqualsShouldBeTransitive() {
		Rule ruleA = build(defaultChildren());
		Rule ruleB = build(defaultChildren());
		Rule ruleC = build(defaultChildren());
		assertTrue(ruleA.equals(ruleB));
		assertTrue(ruleB.equals(ruleC));
		assertTrue(ruleA.equals(ruleC));
	}

}
