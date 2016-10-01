package de.claas.parser.rules;

import de.claas.parser.Rule;
import de.claas.parser.RuleTest;
import de.claas.parser.State;

/**
 * 
 * The JUnit test for class {@link Disjunction}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class DisjunctionTest extends RuleTest {

	@Override
	protected Rule build(Rule... children) {
		return new Disjunction(children);
	}

	@Override
	protected Rule[] defaultChildren() {
		return new Rule[] { new CharacterValue("hello"), new CharacterValue("world") };
	}

	@Override
	protected State processibleState() {
		return buildState("world");
	}

	@Override
	protected State unprocessibleState() {
		return buildState("invalid");
	}

}
