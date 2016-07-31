package de.claas.parser;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * The JUnit test for class {@link DisjunctionTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class DisjunctionTest extends RuleTest {

	@Override
	protected Rule build(Rule...children) {
		return new Disjunction(children);
	}
	
	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] { buildTestRule("hello", new TerminalNode("hello")),
				buildTestRule("world", new TerminalNode("world")) };
	}

	@Override
	protected State processibleState() {
		return buildState("world");
	}

	@Override
	protected State unprocessibleState() {
		return buildState("invalid");
	}

	@Test
	public void shouldNotProcessWithoutChildren() {
		Rule rule = build();
		assertNull(rule.process(processibleState()));
		assertNull(rule.process(unprocessibleState()));
	}
	
	@Test
	public void shouldProcessIfAnyChildProcesses() {
		Rule rule = build(defaultChildren());
		assertNotNull(rule.process(buildState("hello")));
		assertNotNull(rule.process(buildState("world")));
	}

}
