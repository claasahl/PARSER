package de.claas.parser.grammars;

import static org.junit.Assert.*;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.GrammarTest;
import de.claas.parser.exceptions.ParsingException;

/**
 * 
 * The JUnit test for class {@link RepeatTest}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class RepeatTest extends GrammarTest<Repeat> {

	@Override
	protected Repeat build() {
		return new Repeat();
	}

	@Test
	public void shouldHandleUnconstraintRepetitions() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("*"));
	}

	@Test
	public void shouldHandleMinimumRepetitions() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("1*"));
	}

	@Test
	public void shouldHandleMaximumRepetitions() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("*1"));
	}

	@Test
	public void shouldHandleRangeRepetitions() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("1*2"));
	}

	@Test
	public void shouldHandleFixedRepetitions() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("1"));
	}

	@Test(expected = ParsingException.class)
	public void shouldNotHandle() throws ParsingException {
		Grammar grammar = build();
		grammar.parse("**");
	}

}
