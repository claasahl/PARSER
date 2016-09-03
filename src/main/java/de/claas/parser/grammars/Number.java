package de.claas.parser.grammars;

import de.claas.parser.Grammar;
import de.claas.parser.Node;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

/**
 * The class {@link Number}. It is mainly intended for educational purposes. It
 * represents a grammar for numbers. Instances of this class parse sentences of
 * the below grammar and return the result as a tree of {@link Node} instances.
 * <ul>
 * <li>number = [ minus ] integer [ frac ] [ exp ]</li>
 * <li>decimal-point = %x2E ; .</li>
 * <li>DIGIT1-9 = %x31-39 ; 1-9</li>
 * <li>DIGIT = %x30-39 ; 0-9</li>
 * <li>e = %x65 / %x45 ; e E</li>
 * <li>exp = e [ minus / plus ] +DIGIT</li>
 * <li>frac = decimal-point +DIGIT</li>
 * <li>integer = zero / ( DIGIT1-9 *DIGIT )</li>
 * <li>minus = %x2D ; -</li>
 * <li>plus = %x2B ; +</li>
 * <li>zero = %x30 ; 0</li>
 * </ul>
 * The grammar has been defined as augmented Backus Naur form. Details on syntax
 * and grammar can be found in
 * <a href="https://www.ietf.org/rfc/rfc2234.txt">RFC 2234</a>.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Number extends Grammar {

	/**
	 * Constructs a new {@link Number} with default parameters.
	 */
	public Number() {
		super(grammar());
	}

	/**
	 * Returns the above described grammar.
	 * 
	 * @return the above described grammar
	 */
	private static NonTerminal grammar() {
		NonTerminal minus = new NonTerminal("minus", new Terminal("-"));
		NonTerminal plus = new NonTerminal("plus", new Terminal("+"));
		NonTerminal zero = new NonTerminal("zero", new Terminal("0"));
		NonTerminal e = new NonTerminal("e", new Terminal("e", "E"));
		NonTerminal decimalPoint = new NonTerminal("decimal-point", new Terminal("."));
		NonTerminal digit = new NonTerminal("digit", new Terminal('0', '9'));
		NonTerminal digit19 = new NonTerminal("digit1-9", new Terminal('1', '9'));
		NonTerminal exp = new NonTerminal("exp", new Conjunction(e, new Optional(new Disjunction(plus, minus)),
				new Repetition(digit, 1, Integer.MAX_VALUE)));
		NonTerminal frac = new NonTerminal("frac",
				new Conjunction(decimalPoint, new Repetition(digit, 1, Integer.MAX_VALUE)));
		NonTerminal integer = new NonTerminal("integer",
				new Disjunction(zero, new Conjunction(digit19, new Repetition(digit))));
		NonTerminal number = new NonTerminal("number",
				new Conjunction(new Optional(minus), integer, new Optional(frac), new Optional(exp)));
		return number;
	}

}
