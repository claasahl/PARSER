package de.claas.parser.grammars;

import java.util.ArrayList;
import java.util.List;

import de.claas.parser.Grammar;
import de.claas.parser.Node;
import de.claas.parser.grammars.abnf.AugmentedBackusNaur;
import de.claas.parser.grammars.abnf.AugmentedBackusNaurInterpreter;
import de.claas.parser.rules.NonTerminal;

/**
 * The class {@link Repeat}. It is mainly intended for educational purposes and
 * debugging purposes. It represents a grammar-excerpt for repetitions from the
 * augmented Backus Naur grammar (see {@link AugmentedBackusNaur}). Instances of
 * this class parse sentences of the below grammar and return the result as a
 * tree of {@link Node} instances.
 * <ul>
 * <li>repeat = 1*digit / (*digit \"*\" *digit)</li>
 * <li>digit = %x30-39 ; 0-9</li>
 * </ul>
 * This excerpt is highlighted here because it is particularly ambiguous and
 * initial implementations of this library were not able to handle it (out of
 * the box). They required the latter alternative
 * <code>*digit \"*\" *digit</code> to be specified first.
 * <p>
 * The grammar has been written in augmented Backus Naur form (ABNF), as
 * specified in <a href="https://www.ietf.org/rfc/rfc5234.txt">RFC 5234</a> and
 * updated by <a href="https://www.ietf.org/rfc/rfc7405.txt">RFC 7405</a>.
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
		List<String> lines = new ArrayList<>();
		lines.add("repeat = 1*digit / (*digit \"*\" *digit)");
		lines.add("digit = %x30-39 ; 0-9");
		
		String data = String.join("\r\n", lines) + "\r\n";
		Node grammar = new AugmentedBackusNaur().parse(data);
		AugmentedBackusNaurInterpreter interpreter = new AugmentedBackusNaurInterpreter();
		grammar.visit(interpreter);
		return (NonTerminal) interpreter.getResult();
	}

}
