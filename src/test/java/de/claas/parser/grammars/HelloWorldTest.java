package de.claas.parser.grammars;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.GrammarTest;
import de.claas.parser.Node;
import de.claas.parser.exceptions.ParsingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

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
		Node actual = grammar.parse("hallo welt");
		Node expected = generateTree("de", "hallo", "welt");
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleEN() throws ParsingException {
		Grammar grammar = build();
		Node actual = grammar.parse("hello world");
		Node expected = generateTree("en", "hello", "world");
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleSE() throws ParsingException {
		Grammar grammar = build();
		Node actual = grammar.parse("hallå värld");
		Node expected = generateTree("se", "hallå", "värld");
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleES() throws ParsingException {
		Grammar grammar = build();
		Node actual = grammar.parse("hola mundo");
		Node expected = generateTree("es", "hola", "mundo");
		assertEquals(expected, actual);
	}

	@Test
	public void shouldRequireTrimmingOfWhitespace() throws ParsingException {
		Grammar grammar = build();
		Node actual = grammar.parse("hola mundo", false, false);
		Node expected = generateTree("es", "hola", "mundo");
		assertEquals(expected, actual);
	}

	@Test
	public void shouldNotRequireTrimmingOfWhitespace() throws ParsingException {
		Grammar grammar = build();
		Node actual = grammar.parse("holamundo", false, true);
		Node expected = generateTree("es", "hola", "mundo");
		assertEquals(expected, actual);
		
		actual = grammar.parse("holamundo", false, false);
		expected = generateTree("es", "hola", "mundo");
		assertEquals(expected, actual);
	}

	@Test(expected = ParsingException.class)
	public void shouldNotHandleMixedLanguages() throws ParsingException {
		Grammar grammar = build();
		grammar.parse("hallo mundo");
	}

	/**
	 * Returns a tree of nodes for the specified language.
	 * 
	 * @param language
	 *            the language
	 * @param hello
	 *            the word for "hello" in the specified language
	 * @param world
	 *            the word for "world" in the specified language
	 * @return a tree of nodes for the specified language
	 */
	private Node generateTree(String language, String hello, String world) {
		Node t1 = new TerminalNode(hello);
		Node t2 = new TerminalNode(world);
		Node n1 = new NonTerminalNode(language);
		n1.addChild(t1);
		n1.addChild(t2);
		Node expected = new NonTerminalNode("hello-world");
		expected.addChild(n1);
		return expected;
	}

}
