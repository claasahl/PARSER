package de.claas.parser.grammars;

import de.claas.parser.Grammar;
import de.claas.parser.Node;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

/**
 * The class {@link Repeat}. It is mainly intended for educational purposes and
 * debugging purposes. It represents a grammar-excerpt for repetitions from the
 * augmented Backus Naur grammar (see {@link AugmentedBackusNaur}). Instances of
 * this class parse sentences of the below grammar and return the result as a
 * tree of {@link Node} instances.
 * <ul>
 * <li>repeat = 1*DIGIT / (*DIGIT \"*\" *DIGIT)</li>
 * <li>DIGIT = %x30-39 ; 0-9</li>
 * </ul>
 * This excerpt is highlighted here because it is particularly ambiguous and
 * initial implementations of this library were not able to handle it (out of
 * the box). They required the latter alternative
 * <code>*DIGIT \"*\" *DIGIT</code> to be specified first.
 * <p>
 * The grammar has been defined as augmented Backus Naur form. Details on syntax
 * and grammar can be found in
 * <a href="https://www.ietf.org/rfc/rfc2234.txt">RFC 2234</a>.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Repeat extends Grammar {

	/**
	 * Constructs a new {@link Repeat} with default parameters.
	 */
	public Repeat() {
		super(grammar());
	}

	/**
	 * Returns the above described grammar.
	 * 
	 * @return the above described grammar
	 */
	private static NonTerminal grammar() {
		NonTerminal digit = new NonTerminal("digit", new Terminal('0', '9'));
		Repetition digits = new Repetition(digit);
		NonTerminal repeat = new NonTerminal("repeat", new Disjunction(new Repetition(digit, 1, Integer.MAX_VALUE),
				new Conjunction(digits, new Terminal("*"), digits)));
		return repeat;
	}

}
