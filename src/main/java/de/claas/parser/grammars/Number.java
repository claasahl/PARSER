package de.claas.parser.grammars;

import java.util.ArrayList;
import java.util.List;

import de.claas.parser.Grammar;
import de.claas.parser.Node;
import de.claas.parser.rules.NonTerminal;

/**
 * The class {@link Number}. It is mainly intended for educational purposes. It
 * represents a grammar for numbers. Instances of this class parse sentences of
 * the below grammar and return the result as a tree of {@link Node} instances.
 * <ul>
 * <li>number = [ minus ] integer [ frac ] [ exp ]</li>
 * <li>decimal-point = %x2E ; .</li>
 * <li>digit1-9 = %x31-39 ; 1-9</li>
 * <li>digit = %x30-39 ; 0-9</li>
 * <li>e = %x65 / %x45 ; e E</li>
 * <li>exp = e [ minus / plus ] 1*digit</li>
 * <li>frac = decimal-point 1*digit</li>
 * <li>integer = zero / ( digit1-9 *digit )</li>
 * <li>minus = %x2D ; -</li>
 * <li>plus = %x2B ; +</li>
 * <li>zero = %x30 ; 0</li>
 * </ul>
 * The grammar has been written in augmented Backus Naur form (ABNF), as
 * specified in <a href="https://www.ietf.org/rfc/rfc5234.txt">RFC 5234</a> and
 * updated by <a href="https://www.ietf.org/rfc/rfc7405.txt">RFC 7405</a>.
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
		List<String> lines = new ArrayList<>();
		lines.add("number = [ minus ] integer [ frac ] [ exp ]");
		lines.add("decimal-point = %x2E ; .");
		lines.add("digit1-9 = %x31-39 ; 1-9");
		lines.add("digit = %x30-39 ; 0-9");
		lines.add("e = %x65 / %x45 ; e E");
		lines.add("exp = e [ minus / plus ] 1*digit");
		lines.add("frac = decimal-point 1*digit");
		lines.add("integer = zero / ( digit1-9 *digit )");
		lines.add("minus = %x2D ; -");
		lines.add("plus = %x2B ; +");
		lines.add("zero = %x30 ; 0");

		String data = String.join("\r\n", lines) + "\r\n";
		Node grammar = new AugmentedBackusNaur().parse(data);
		AugmentedBackusNaurInterpreter interpreter = new AugmentedBackusNaurInterpreter();
		grammar.visit(interpreter);
		return (NonTerminal) interpreter.getResult();
	}

}
