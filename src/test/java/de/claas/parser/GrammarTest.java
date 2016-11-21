package de.claas.parser;

/**
 * 
 * The JUnit test for class {@link Grammar}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 * <p>
 * The tested class is abstract or an interface. Consequently, this JUnit test
 * provides a set of test cases that apply to all concrete implementations of
 * the tested class.
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
