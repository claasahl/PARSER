package de.claas.parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import de.claas.parser.exceptions.ParsingException;
import de.claas.parser.visitors.NodeToString;

/**
 * 
 * The JUnit test for class {@link GrammarTest}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public abstract class GrammarTest<R extends Grammar> {

	/**
	 * Returns an instantiated {@link Grammar} class that has been instantiated
	 * with default parameters.
	 * 
	 * @return an instantiated {@link Grammar} class
	 */
	protected abstract R build();

	@Test
	public void shouldTokenizePattern() throws ParsingException {
		R grammar = build();
		String pattern = "abcd";
		Set<String> terminals = new HashSet<>(Arrays.asList("d", "a", "bc"));
		List<String> tokens = Arrays.asList("a", "bc", "d");
		Assert.assertEquals(tokens, grammar.tokenize(terminals, pattern));
	}

	@Test(expected = ParsingException.class)
	public void shouldNotTokenizePatternWithUnrecognizedToken() throws ParsingException {
		R grammar = build();
		String pattern = "abcd";
		Set<String> terminals = new HashSet<>(Arrays.asList("d", "a"));
		grammar.tokenize(terminals, pattern);
	}

	@Test(expected = ParsingException.class)
	public void shouldNotTokenizePatternWithUnrecognizedWhitespace() throws ParsingException {
		R grammar = build();
		String pattern = "a bcd\n";
		Set<String> terminals = new HashSet<>(Arrays.asList("d", "a", "bc", " "));
		grammar.tokenize(terminals, pattern);
	}

	@Test
	public void shouldHandleAmbigiousTerminals() throws ParsingException {
		R grammar = build();
		String pattern = "bc";
		Set<String> terminals = new HashSet<>(Arrays.asList("bc", "b", "c"));
		List<String> tokens = Arrays.asList("b", "c");
		Assert.assertEquals(tokens, grammar.tokenize(terminals, pattern));
	}

	/**
	 * A convenience method for asserting the equality of nodes.
	 * 
	 * @param expected
	 *            the expected tree of nodes
	 * @param actual
	 *            the actual tree of nodes
	 */
	protected static void assertEquals(Node expected, Node actual) {
		NodeToString exp = new NodeToString();
		expected.visit(exp);
		NodeToString act = new NodeToString();
		actual.visit(act);
		Assert.assertEquals(exp.toString(), act.toString());
	}

}
