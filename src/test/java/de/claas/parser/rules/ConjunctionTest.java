package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.RuleTest;
import de.claas.parser.State;

/**
 * 
 * The JUnit test for class {@link Conjunction}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
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
		return new Rule[] { new CharacterValue("hello"), new CharacterValue("world") };
	}

	@Override
	protected State processibleState() {
		return buildState("helloworld");
	}

	@Override
	protected State unprocessibleState() {
		return buildState("hello");
	}

}
