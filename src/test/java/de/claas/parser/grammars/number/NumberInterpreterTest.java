package de.claas.parser.grammars.number;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.claas.parser.Node;
import de.claas.parser.exceptions.CyclicNodeException;
import de.claas.parser.exceptions.InterpretingException;
import de.claas.parser.results.IntermediateNode;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.visitors.NodeVisitorTest;

/**
 *
 * The JUnit test for class {@link NumberInterpreter}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 * <p>
 * The tested class is abstract or an interface. Consequently, this JUnit test
 * provides a set of test cases that apply to all concrete implementations of
 * the tested class.
 *
 * @author Claas Ahlrichs
 *
 */
public class NumberInterpreterTest extends NodeVisitorTest {

	private NumberInterpreter visitor;

	@Before
	public void before() {
		this.visitor = new NumberInterpreter();
	}

	@Override
	public void shouldHandleNoNode() {
		assertNull(this.visitor.getResult());
	}

	@Override
	@Test(expected=InterpretingException.class)
	public void shouldHandleTerminalNode() {
		Node node = new TerminalNode("42");
		node.visit(this.visitor);
	}

	@Override
	@Test(expected=InterpretingException.class)
	public void shouldHandleIntermediateNode() {
		Node node = new IntermediateNode();
		node.visit(this.visitor);
	}

	@Override
	@Test(expected=InterpretingException.class)
	public void shouldHandleNonTerminalNode() {
		Node node = new NonTerminalNode("number");
		node.visit(this.visitor);
	}

	@Override
	public void shouldHandleNodes() {
		Node number = NumberTest.generateTree(true, "42", "5", null, null, null);
		number.visit(this.visitor);
		assertEquals(new Double(-42.5), this.visitor.getResult());
	}

	@Override
	@Test(expected = CyclicNodeException.class)
	public void shouldHandleCyclicNonTerminalNode() {
		NonTerminalNode node = new NonTerminalNode("number");
		node.addChild(node);
		node.visit(this.visitor);
	}

	@Override
	@Test(expected = InterpretingException.class)
	public void shouldHandleCyclicIntermediateNode() {
		IntermediateNode node = new IntermediateNode();
		node.addChild(node);
		node.visit(this.visitor);
	}
	
	@Test
	public void shouldBePositveInteger() {
		Node number = NumberTest.generateTree(false, "23", null, null, null, null);
		number.visit(this.visitor);
		assertEquals(new Integer(23), this.visitor.getResult());
	}
	
	@Test
	public void shouldBeNegativeInteger() {
		Node number = NumberTest.generateTree(true, "23", null, null, null, null);
		number.visit(this.visitor);
		assertEquals(new Integer(-23), this.visitor.getResult());
	}
	
	@Test
	public void shouldBePositveFractionalNumber() {
		Node number = NumberTest.generateTree(false, "12", "01", null, null, null);
		number.visit(this.visitor);
		assertEquals(new Double(12.01), this.visitor.getResult());
	}
	
	@Test
	public void shouldBeNegativeFractionalNumber() {
		Node number = NumberTest.generateTree(true, "12", "01", null, null, null);
		number.visit(this.visitor);
		assertEquals(new Double(-12.01), this.visitor.getResult());
	}
	
	@Test
	public void shouldHavePositveExponent() {
		Node number = NumberTest.generateTree(false, "1", null, "e", "+", "2");
		number.visit(this.visitor);
		assertEquals(new Double(100), this.visitor.getResult());
	}
	
	@Test
	public void shouldHaveNegativeExponent() {
		Node number = NumberTest.generateTree(false, "1", null, "e", "-", "2");
		number.visit(this.visitor);
		assertEquals(new Double(0.01), this.visitor.getResult());
	}

}
