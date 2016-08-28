package de.claas.parser;

import org.junit.Assert;

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
