package de.claas.parser.grammars.abnf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Terminal;

/**
 * 
 * The JUnit test for class {@link InterpreteDefinedAs}. It is intended to
 * collect and document a set of test cases for the tested class. Please refer
 * to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class InterpreteDefinedAsTest extends InterpreterBaseTest<InterpreteDefinedAs> {

	@Override
	protected InterpreteDefinedAs build() {
		return new InterpreteDefinedAs();
	}

	@Override
	public void shouldHandleNodes() {
		Node root = new NonTerminalNode("defined-as");
		Node cwsp = new NonTerminalNode("c-wsp");
		cwsp.addChild(new TerminalNode("ignored"));
		root.addChild(cwsp);
		root.addChild(cwsp);
		root.addChild(new TerminalNode("=/"));
		root.addChild(cwsp);
		root.addChild(cwsp);
		root.addChild(cwsp);
		Rule expected = new Terminal("=/");

		InterpreterBase visitor = build();
		root.visit(visitor);
		Rule actual = visitor.getRule();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldIgnoreLeadingWhitespace() {
		Node root = new NonTerminalNode("defined-as");
		root.addChild(new TerminalNode("="));
		Node cwsp = new NonTerminalNode("c-wsp");
		cwsp.addChild(new TerminalNode("ignored"));
		root.addChild(cwsp);
		Rule expected = new Terminal("=");

		InterpreterBase visitor = build();
		root.visit(visitor);
		Rule actual = visitor.getRule();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldIgnoreTrailingWhitespace() {
		Node root = new NonTerminalNode("defined-as");
		Node cwsp = new NonTerminalNode("c-wsp");
		cwsp.addChild(new TerminalNode("ignored"));
		root.addChild(cwsp);
		root.addChild(new TerminalNode("="));
		Rule expected = new Terminal("=");

		InterpreterBase visitor = build();
		root.visit(visitor);
		Rule actual = visitor.getRule();
		assertEquals(expected, actual);
	}

}
