package de.claas.parser.grammars;

import de.claas.parser.Grammar;
import de.claas.parser.Node;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Terminal;

/**
 * The class {@link HelloWorld}. It is mainly intended for educational purposes.
 * It represents a grammar for parsing "hello world" in several languages.
 * Instances of this class parse sentences of the below grammar and return the
 * result as a tree of {@link Node} instances.
 * <ul>
 * <li>hello-world = de / en / se / es</li>
 * <li>de = "hallo" " " "welt"</li>
 * <li>en = "hello" " " "world"</li>
 * <li>se = "hallå" " " "värld"</li>
 * <li>es = "hola" " " "mundo"</li>
 * </ul>
 * The grammar has been defined as augmented Backus Naur form. Details on syntax
 * and grammar can be found in
 * <a href="https://www.ietf.org/rfc/rfc2234.txt">RFC 2234</a>.
 * 
 * @author Claas Ahlrichs
 *
 */
public class HelloWorld extends Grammar {

	/**
	 * Creates an instance with default parameters.
	 */
	public HelloWorld() {
		super(grammar());
	}

	/**
	 * Returns the above described grammar.
	 * 
	 * @return the above described grammar
	 */
	private static NonTerminal grammar() {
		NonTerminal de = new NonTerminal("de",
				new Conjunction(new Terminal("hallo"), new Terminal(" "), new Terminal("welt")));
		NonTerminal en = new NonTerminal("en",
				new Conjunction(new Terminal("hello"), new Terminal(" "), new Terminal("world")));
		NonTerminal se = new NonTerminal("se",
				new Conjunction(new Terminal("hallå"), new Terminal(" "), new Terminal("värld")));
		NonTerminal es = new NonTerminal("es",
				new Conjunction(new Terminal("hola"), new Terminal(" "), new Terminal("mundo")));
		return new NonTerminal("hello-world", new Disjunction(de, en, se, es));
	}

}
