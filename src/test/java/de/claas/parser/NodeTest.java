package de.claas.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

/**
 * The JUnit test for class {@link Node}. It is intended to collect and document
 * a set of test cases for the tested class. Please refer to the individual
 * tests for more detailed information.
 * <p>
 * The tested class is abstract or an interface. Consequently, this JUnit test
 * provides a set of test cases that apply to all concrete implementations of
 * the tested class.
 *
 * @author Claas Ahlrichs
 */
public abstract class NodeTest {

	/**
	 * Returns an instantiated {@link Node} class with the given children. If
	 * appropriate, the instance is configured with default values.
	 *
	 * @param children
	 *            the children
	 * @return an instantiated {@link Node} class
	 */
	protected abstract Node build(Node... children);

	@Test
	public void shouldHaveNoChildren() {
		Node node = build();
		assertFalse(node.hasChildren());
	}

	@Test
	public void shouldHaveChildren() {
		Node node = build();
		Node child = build();
		assertTrue(node.addChild(child));
		assertTrue(node.hasChildren());
	}

	@Test
	public void shouldHaveEmptyIterator() {
		Node node = build();
		assertFalse(node.iterator().hasNext());
	}

	@Test
	public void shouldHaveNonEmptyIterator() {
		Node node = build();
		Node child = build();
		assertTrue(node.addChild(child));
		assertTrue(node.iterator().hasNext());
	}

	@Test
	public void shouldManageChildren() {
		Node node = build();
		Node childA = build();
		Node childB = build();
		Node childC = build();
		assertTrue(node.addChild(childA));
		assertTrue(node.addChild(childB));
		assertTrue(node.addChild(childC));

		// everybody there?
		Iterator<Node> iterator = node.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(childA, iterator.next());
		assertEquals(childB, iterator.next());
		assertEquals(childC, iterator.next());
		assertFalse(iterator.hasNext());

		// remove middle child
		assertTrue(node.removeChild(childB));
		iterator = node.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(childA, iterator.next());
		assertEquals(childC, iterator.next());
		assertFalse(iterator.hasNext());

		// remove last child
		assertTrue(node.removeChild(childC));
		iterator = node.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(childA, iterator.next());
		assertFalse(iterator.hasNext());

		// remove first child
		assertTrue(node.removeChild(childA));
		iterator = node.iterator();
		assertFalse(iterator.hasNext());

		// cannot remove children that arn't there ;)
		assertFalse(node.removeChild(childA));
		assertFalse(node.removeChild(childB));
		assertFalse(node.removeChild(childC));
	}

	@Test
	public void addChildShouldHandleNull() {
		Node node = build();
		assertFalse(node.addChild(null));
	}

	@Test
	public void removeChildShouldHandleNull() {
		Node node = build();
		assertFalse(node.removeChild(null));
	}

	@Test
	public void implementationOfEqualsShouldHandleNull() {
		Node node = build();
		assertNotNull(node);
	}

	@Test
	public void implementationOfEqualsShouldBeReflexive() {
		Node node = build();
		assertTrue(node.equals(node));
	}

	@Test
	public void implementationOfEqualsShouldBeSymmetric() {
		Node nodeA = build();
		Node nodeB = build();
		assertTrue(nodeA.equals(nodeB));
		assertTrue(nodeB.equals(nodeA));
	}

	@Test
	public void implementationOfEqualsShouldBeTransitive() {
		Node nodeA = build();
		Node nodeB = build();
		Node nodeC = build();
		assertTrue(nodeA.equals(nodeB));
		assertTrue(nodeB.equals(nodeC));
		assertTrue(nodeA.equals(nodeC));
	}

	@Test
	public void implementationOfHashCodeShouldBeReproducible() {
		Node node = build();
		int hashCode1 = node.hashCode();
		int hashCode2 = node.hashCode();
		assertEquals(hashCode1, hashCode2);
	}

	@Test
	public void implementationOfHashCodeShouldBeConsistent() {
		Node nodeA = build();
		Node nodeB = build();
		assertEquals(nodeA.hashCode(), nodeB.hashCode());
	}

}
