package de.claas.parser.grammars;

import static org.junit.Assert.*;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.exceptions.ParsingException;

public class NumberTest {
	
	private Grammar build() {
		return new Number();
	}

	@Test
	public void shouldHandleIntegers() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("23"));
		assertNotNull(grammar.parse("-42"));
	}
	
	@Test
	public void shouldHandleFractionalNumbers() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("23.43"));
		assertNotNull(grammar.parse("-42.111111111111111111111111112"));
	}
	
	@Test
	public void shouldHandleExponentialNumbers() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("23e-9"));
		assertNotNull(grammar.parse("-42E+8"));
		assertNotNull(grammar.parse("23.43E777"));
		assertNotNull(grammar.parse("-42.111111111111111111111111112e-66"));
	}
	
	@Test
	public void shouldHandleZero() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("0"));
	}
	
	@Test(expected = ParsingException.class)
	public void shouldNotHandleZeroAsFirstDigit() throws ParsingException {
		Grammar grammar = build();
		grammar.parse("01");
	}
	
	@Test(expected = ParsingException.class)
	public void shouldNotHandlePlusAsFirstDigit() throws ParsingException {
		Grammar grammar = build();
		grammar.parse("+1");
	}

}
