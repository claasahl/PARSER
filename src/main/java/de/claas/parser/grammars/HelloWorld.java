package de.claas.parser.grammars;

import de.claas.parser.Grammar;
import de.claas.parser.Node;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;

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
 * The grammar has been written in augmented Backus Naur form (ABNF), as
 * specified in <a href="https://www.ietf.org/rfc/rfc5234.txt">RFC 5234</a> and
 * updated by <a href="https://www.ietf.org/rfc/rfc7405.txt">RFC 7405</a>.
 * 
 * @author Claas Ahlrichs
 */
public class HelloWorld extends Grammar {

	/**
	 * Constructs a new {@link HelloWorld} with default parameters.
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
				new Conjunction(new CharacterValue("hallo"), new CharacterValue(" "), new CharacterValue("welt")));
		NonTerminal en = new NonTerminal("en",
				new Conjunction(new CharacterValue("hello"), new CharacterValue(" "), new CharacterValue("world")));
		NonTerminal se = new NonTerminal("se",
				new Conjunction(new CharacterValue("hallå"), new CharacterValue(" "), new CharacterValue("värld")));
		NonTerminal es = new NonTerminal("es",
				new Conjunction(new CharacterValue("hola"), new CharacterValue(" "), new CharacterValue("mundo")));
		return new NonTerminal("hello-world", new Disjunction(de, en, se, es));
	}

}
