package de.claas.parser.grammars;

import static org.junit.Assert.*;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.exceptions.ParsingException;

public class RepeatTest {

	private Grammar build() {
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
