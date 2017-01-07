package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.State;

/**
 * The JUnit test for class {@link Repetition}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 */
public class RepetitionTest extends DecoratorTest {

	@Override
	protected Decorator build(Rule rule) {
		return new Repetition(rule);
	}

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] { new CharacterValue("decorated") };
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
