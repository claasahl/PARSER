package de.claas.parser;

/**
 * 
 * The JUnit test for class {@link Grammar}. It is intended to collect and
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

}
