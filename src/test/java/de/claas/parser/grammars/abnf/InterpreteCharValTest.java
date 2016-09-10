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
 * The JUnit test for class {@link InterpreteCharValTest}. It is intended to
 * collect and document a set of test cases for the tested class. Please refer
 * to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class InterpreteCharValTest extends InterpreterBaseTest<InterpreteCharVal> {

	@Override
	protected InterpreteCharVal build() {
		return new InterpreteCharVal();
	}

	@Override
	public void shouldHandleNodes() {
		Node root = new NonTerminalNode("char-val");
		Node quote = new NonTerminalNode("dQuote");
		quote.addChild(new TerminalNode("\""));
		root.addChild(quote);
		root.addChild(new TerminalNode("hello"));
		root.addChild(new TerminalNode(" "));
		root.addChild(new TerminalNode("world"));
		root.addChild(quote);
		Rule expected = new Terminal("hello world");

		InterpreterBase visitor = build();
		root.visit(visitor);
		Rule actual = visitor.getRule();
		assertEquals(expected, actual);
	}

	@Test(expected = InterpretingException.class)
	public void shouldStartWithDoubleQuote() {
		Node root = new NonTerminalNode("char-val");
		root.addChild(new TerminalNode("hello"));
		root.addChild(new TerminalNode(" "));
		root.addChild(new TerminalNode("world"));
		Node quote = new NonTerminalNode("dQuote");
		quote.addChild(new TerminalNode("\""));
		root.addChild(quote);
		Rule expected = new Terminal("hello world");

		InterpreterBase visitor = build();
		root.visit(visitor);
		Rule actual = visitor.getRule();
		assertEquals(expected, actual);
	}

	@Test(expected = InterpretingException.class)
	public void shouldEndWithDoubleQuote() {
		Node root = new NonTerminalNode("char-val");
		Node quote = new NonTerminalNode("dQuote");
		quote.addChild(new TerminalNode("\""));
		root.addChild(quote);
		root.addChild(new TerminalNode("hello"));
		root.addChild(new TerminalNode(" "));
		root.addChild(new TerminalNode("world"));

		InterpreterBase visitor = build();
		root.visit(visitor);
	}

	@Test(expected = InterpretingException.class)
	public void shouldStartWithCharVal() {
		Node root = new NonTerminalNode("char-val");
		root.addChild(new TerminalNode("hello"));
		root.addChild(new TerminalNode(" "));
		root.addChild(new TerminalNode("world"));
		Node quote = new NonTerminalNode("dQuote");
		quote.addChild(new TerminalNode("\""));
		root.addChild(quote);

		InterpreterBase visitor = build();
		root.visit(visitor);
	}

	@Test
	public void shouldHandleEmptyContent() {
		Node root = new NonTerminalNode("char-val");
		Node quote = new NonTerminalNode("dQuote");
		quote.addChild(new TerminalNode("\""));
		root.addChild(quote);
		root.addChild(quote);
		Rule expected = new Terminal("");

		InterpreterBase visitor = build();
		root.visit(visitor);
		Rule actual = visitor.getRule();
		assertEquals(expected, actual);
	}

}
