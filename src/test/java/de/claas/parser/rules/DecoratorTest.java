package de.claas.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import de.claas.parser.Rule;

/**
 * 
 * The JUnit test for class {@link Decorator}. It is intended to collect and
 * document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public abstract class DecoratorTest extends RuleTest {

	@Override
	protected Decorator build(Rule... children) {
		if (children.length == 0)
			return build(new Conjunction());

		Decorator rule = build(children[0]);
		for (int i = 1; i < children.length; i++) {
			rule.addChild(children[i]);
		}
		return rule;
	}

	/**
	 * Returns an {@link Decorator} class that was instantiated with the given
	 * parameter.
	 * 
	 * @param rule
	 *            the decorated rule
	 *
	 * @return an instantiated {@link Decorator} class
	 */
	protected abstract Decorator build(Rule rule);

	@Test
	public void shouldHaveDecoratedRule() {
		Rule decorated = new Conjunction();
		Decorator rule = build(decorated);
		assertNotNull(rule.getRule());
	}

	@Test
	public void shouldNotHaveDecoratedRule() {
		Decorator rule = build((Rule) null);
		assertNull(rule.getRule());
	}

	@Test(expected = IllegalStateException.class)
	public void addChildShouldRaiseExceptionWithtoutDecoratedRule() {
		Decorator rule = build((Rule) null);
		rule.addChild(null);
	}

	@Test
	public void addChildShouldBeForwardedToDecoratedRule() {
		Rule decorated = new Conjunction();
		Decorator rule = build(decorated);
		Rule child = new Terminal("child");

		assertFalse(decorated.hasChildren());
		assertTrue(rule.addChild(child));
		assertTrue(decorated.hasChildren());
	}

	@Test(expected = IllegalStateException.class)
	public void removeChildShouldRaiseExceptionWithtoutDecoratedRule() {
		Decorator rule = build((Rule) null);
		rule.removeChild(null);
	}

	@Test
	public void removeChildShouldBeForwardedToDecoratedRule() {
		Rule decorated = new Conjunction();
		Decorator rule = build(decorated);
		Rule child = new Terminal("child");

		assertTrue(decorated.addChild(child));
		assertTrue(rule.removeChild(child));
		assertFalse(decorated.hasChildren());
	}

	@Test(expected = IllegalStateException.class)
	public void hasChildrenShouldRaiseExceptionWithtoutDecoratedRule() {
		Decorator rule = build((Rule) null);
		rule.hasChildren();
	}

	@Test
	public void hasChildrenShouldBeForwardedToDecoratedRule() {
		Rule decorated = new Conjunction();
		Decorator rule = build(decorated);
		Rule child = new Terminal("child");

		assertFalse(decorated.hasChildren());
		assertFalse(rule.hasChildren());
		assertTrue(decorated.addChild(child));
		assertTrue(decorated.hasChildren());
		assertTrue(rule.hasChildren());
	}

	@Test(expected = IllegalStateException.class)
	public void iteratorShouldRaiseExceptionWithtoutDecoratedRule() {
		Decorator rule = build((Rule) null);
		rule.iterator();
	}

	@Test
	public void iteratorShouldBeForwardedToDecoratedRule() {
		Rule decorated = new Conjunction();
		Decorator rule = build(decorated);
		Rule child = new Terminal("child");

		assertFalse(decorated.iterator().hasNext());
		assertFalse(rule.iterator().hasNext());

		assertTrue(decorated.addChild(child));
		Iterator<Rule> iterator = decorated.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(child, iterator.next());
		assertFalse(iterator.hasNext());

		iterator = rule.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(child, iterator.next());
		assertFalse(iterator.hasNext());
	}

}
