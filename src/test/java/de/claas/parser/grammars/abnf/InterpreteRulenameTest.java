package de.claas.parser.grammars.abnf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The JUnit test for class {@link InterpreteRulenameTest}. It is intended to
 * collect and document a set of test cases for the tested class. Please refer
 * to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class InterpreteRulenameTest extends InterpreterBaseTest<InterpreteRulename> {

	@Override
	protected InterpreteRulename build() {
		return new InterpreteRulename();
	}

	@Override
	public void shouldHandleNodes() {
		Node root = new NonTerminalNode("rulename");
		Node alpha = new NonTerminalNode("alpha");
		alpha.addChild(new TerminalNode("hello"));
		root.addChild(alpha);
		Node digit = new NonTerminalNode("digit");
		digit.addChild(new TerminalNode("321"));
		root.addChild(digit);
		root.addChild(new TerminalNode("-"));
		Rule expected = new Terminal("hello321-");

		InterpreterBase visitor = build();
		root.visit(visitor);
		Rule actual = visitor.getRule();
		assertEquals(expected, actual);
	}

	@Test(expected = InterpretingException.class)
	public void shouldStartWithAlpha() {
		Node root = new NonTerminalNode("rulename");
		Node digit = new NonTerminalNode("digit");
		digit.addChild(new TerminalNode("321"));
		root.addChild(digit);
		root.addChild(new TerminalNode("-"));

		InterpreterBase visitor = build();
		root.visit(visitor);
	}

}
