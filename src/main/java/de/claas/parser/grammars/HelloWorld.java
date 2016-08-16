package de.claas.parser.grammars;

import de.claas.parser.Grammar;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Terminal;

public class HelloWorld extends Grammar {

	public HelloWorld() {
		super(grammar());
	}

	private static NonTerminal grammar() {
		NonTerminal de = new NonTerminal("de", new Conjunction(new Terminal("hallo"), new Terminal("welt")));
		NonTerminal en = new NonTerminal("en", new Conjunction(new Terminal("hello"), new Terminal("world")));
		NonTerminal se = new NonTerminal("se", new Conjunction(new Terminal("hallå"), new Terminal("värld")));
		NonTerminal es = new NonTerminal("es", new Conjunction(new Terminal("hola"), new Terminal("mundo")));
		return new NonTerminal("hello-world", new Disjunction(de, en, se, es));
	}

}
