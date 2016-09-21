package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.State;

/**
 * 
 * The JUnit test for class {@link Optional}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class OptionalTest extends DecoratorTest {

	@Override
	protected Decorator build(Rule rule) {
		return new Optional(rule);
	}

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] { new Terminal("decorated") };
	}

	@Override
	protected State processibleState() {
		return buildState("decorated");
	}

	@Override
	protected State unprocessibleState() {
		return null;
	}

}
