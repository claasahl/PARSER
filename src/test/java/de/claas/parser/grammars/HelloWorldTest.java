package de.claas.parser.grammars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.GrammarTest;
import de.claas.parser.Node;
import de.claas.parser.builders.HelloWorldBuilder;
import de.claas.parser.exceptions.ParserException;

/**
 * The JUnit test for class {@link HelloWorld}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 */
public class HelloWorldTest extends GrammarTest<HelloWorld> {

	@Override
	protected HelloWorld build() {
		return new HelloWorld();
	}

	@Test
	public void shouldHandleDE() {
		Grammar grammar = build();
		Node actual = grammar.parse("hallo welt");
		Node expected = new HelloWorldBuilder("de", "hallo", "welt").build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleEN() {
		Grammar grammar = build();
		Node actual = grammar.parse("hello world");
		Node expected = new HelloWorldBuilder("en", "hello", "world").build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleSE() {
		Grammar grammar = build();
		Node actual = grammar.parse("hallå värld");
		Node expected = new HelloWorldBuilder("se", "hallå", "värld").build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleES() {
		Grammar grammar = build();
		Node actual = grammar.parse("hola mundo");
		Node expected = new HelloWorldBuilder("es", "hola", "mundo").build();
		assertEquals(expected, actual);
	}

	@Test(expected = ParserException.class)
	public void shouldNotHandleMixedLanguages() {
		Grammar grammar = build();
		grammar.parse("hallo mundo");
	}

}
