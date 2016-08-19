package de.claas.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import de.claas.parser.exceptions.ParsingException;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

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

}
