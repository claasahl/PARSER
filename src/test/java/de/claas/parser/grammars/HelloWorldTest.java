package de.claas.parser.grammars;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.GrammarTest;
import de.claas.parser.exceptions.ParsingException;

/**
 * 
 * The JUnit test for class {@link HelloWorldTest}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class HelloWorldTest extends GrammarTest<HelloWorld> {

	@Override
	protected HelloWorld build() {
		return new HelloWorld();
	}

	@Test
	public void shouldHandleDE() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("hallo welt"));
	}

	@Test
	public void shouldHandleEN() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("hello world"));
	}

	@Test
	public void shouldHandleSE() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("hallå värld"));
	}

	@Test
	public void shouldHandleES() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("hola mundo"));
	}

	@Test
	public void shouldRequireTrimmingOfWhitespace() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("hola mundo", false, false));
	}

	@Test
	public void shouldNotRequireTrimmingOfWhitespace() throws ParsingException {
		Grammar grammar = build();
		assertNotNull(grammar.parse("holamundo", false, true));
		assertNotNull(grammar.parse("holamundo", false, false));
	}

	@Test(expected = ParsingException.class)
	public void shouldNotHandleMixedLanguages() throws ParsingException {
		Grammar grammar = build();
		grammar.parse("hallo mundo");
	}

}
