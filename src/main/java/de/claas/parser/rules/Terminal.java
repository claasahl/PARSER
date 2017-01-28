package de.claas.parser.rules;

import de.claas.parser.Rule;

/**
 * The class {@link Terminal}. It is an implementation of the {@link Rule}
 * class. It is intended to represent a terminal symbol (e.g. 'letter' of the
 * grammar's alphabet) within a grammar.
 * 
 * @author Claas Ahlrichs
 */
public abstract class Terminal extends Rule {

	@Override
	public boolean addChild(Rule rule) {
		return false;
	}

	@Override
	public boolean removeChild(Rule rule) {
		return false;
	}

}