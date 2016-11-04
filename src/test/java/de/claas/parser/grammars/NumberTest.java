package de.claas.parser.grammars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.GrammarTest;
import de.claas.parser.builders.NumberBuilder;
import de.claas.parser.exceptions.ParserException;

/**
 * 
 * The JUnit test for class {@link Number}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class NumberTest extends GrammarTest<Number> {

	@Override
	protected Number build() {
		return new Number();
	}

	@Test
	public void shouldHandleIntegers() {
		Grammar grammar = build();
		assertEquals(new NumberBuilder("23").build(), grammar.parse("23"));
		assertEquals(new NumberBuilder("42").negative().build(), grammar.parse("-42"));
	}

	@Test
	public void shouldHandleFractionalNumbers() {
		Grammar grammar = build();
		assertEquals(new NumberBuilder("23").fraction("43").build(), grammar.parse("23.43"));
		assertEquals(new NumberBuilder("42").fraction("111111111111111111111111112").negative().build(),
				grammar.parse("-42.111111111111111111111111112"));
	}

	@Test
	public void shouldHandleExponentialNumbers() {
		Grammar grammar = build();
		assertEquals(new NumberBuilder("23").exponent("e", "-", "9").build(), grammar.parse("23e-9"));
		assertEquals(new NumberBuilder("42").exponent("E", "+", "8").negative().build(), grammar.parse("-42E+8"));
		assertEquals(new NumberBuilder("23").exponent("E", null, "777").fraction("43").build(),
				grammar.parse("23.43E777"));
		assertEquals(new NumberBuilder("42").exponent("e", "-", "66").fraction("111111111111111111111111112").negative()
				.build(), grammar.parse("-42.111111111111111111111111112e-66"));
	}

	@Test
	public void shouldHandleZero() {
		Grammar grammar = build();
		assertEquals(new NumberBuilder("0").build(), grammar.parse("0"));
	}

	@Test(expected = ParserException.class)
	public void shouldNotHandleZeroAsFirstDigit() {
		Grammar grammar = build();
		grammar.parse("01");
	}

	@Test(expected = ParserException.class)
	public void shouldNotHandlePlusAsFirstDigit() {
		Grammar grammar = build();
		grammar.parse("+1");
	}

}
