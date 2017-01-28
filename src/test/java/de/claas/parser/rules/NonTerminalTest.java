package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.State;

/**
 * The JUnit test for class {@link NonTerminal}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 */
public class NonTerminalTest extends DecoratorTest {

	private static final String DEFAULT_NAME = "hello world!";

	@Override
	protected Decorator build(Rule rule) {
		return new NonTerminal(DEFAULT_NAME, rule);
	}

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] { new CharacterValue("nonTerminal") };
	}

	@Override
	protected State processibleState() {
		return buildState("nonTerminal");
	}

	@Override
	protected State unprocessibleState() {
		return buildState("invlid");
	}

}
