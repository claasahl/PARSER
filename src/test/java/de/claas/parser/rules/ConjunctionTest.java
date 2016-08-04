package de.claas.parser.rules;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.claas.parser.Rule;
import de.claas.parser.State;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Conjunction;

/**
 * 
 * The JUnit test for class {@link ConjunctionTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class ConjunctionTest extends RuleTest {

	@Override
	protected Rule build(Rule... children) {
		return new Conjunction(children);
	}

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] { buildTestRule("hello", new TerminalNode("hello")),
				buildTestRule("world", new TerminalNode("world")) };
	}

	@Override
	protected State processibleState() {
		return buildState("hello", "world");
	}

	@Override
	protected State unprocessibleState() {
		return buildState("hello");
	}

	@Test
	public void shouldNotProcessWithoutChildren() {
		Rule rule = build();
		assertNull(rule.process(processibleState()));
		assertNull(rule.process(unprocessibleState()));
	}
	
	@Test
	public void shouldNotProcessIfAnyChildFailsToProcess() {
		Rule rule = build(defaultChildren());
		assertNull(rule.process(buildState("hello", "invalid")));
		assertNull(rule.process(buildState("invalid", "world")));
		assertNull(rule.process(buildState("hello")));
		assertNull(rule.process(buildState("world")));
	}

}
