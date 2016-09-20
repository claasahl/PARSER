package de.claas.parser.rules;

import java.util.Iterator;

import de.claas.parser.Rule;

/**
 * 
 * The class {@link Decorator}. It is an implementation of the {@link Rule}
 * class. It is intended to decorate (or wrap) an existing rule. In doing so,
 * new functionality can be dynamically added to existing rules.
 * <p>
 * This implementation resembles the <i>decorator</i> design pattern.
 *
 * @author Claas Ahlrichs
 *
 */
public abstract class Decorator extends Rule {

	private static final String MISSING_RULE = "decorator is missing a decorated rule";
	private static final String ONLY_ONCE = "decorated rule may only be set once";
	private Rule rule;

	/**
	 * Constructs a new {@link Decorator} with default parameters.
	 */
	public Decorator() {
		this(null);
	}

	/**
	 * Creates an instance with the given parameter.
	 * 
	 * @param rule
	 *            the decorated rule
	 */
	public Decorator(Rule rule) {
		this.rule = rule;
	}

	@Override
	public boolean addChild(Rule child) {
		if (this.rule == null)
			throw new IllegalStateException(MISSING_RULE);
		return this.rule.addChild(child);
	}

	@Override
	public boolean removeChild(Rule child) {
		if (this.rule == null)
			throw new IllegalStateException(MISSING_RULE);
		return this.rule.removeChild(child);
	}

	@Override
	public boolean hasChildren() {
		if (this.rule == null)
			throw new IllegalStateException(MISSING_RULE);
		return this.rule.hasChildren();
	}

	@Override
	public Iterator<Rule> iterator() {
		if (this.rule == null)
			throw new IllegalStateException(MISSING_RULE);
		return this.rule.iterator();
	}

	/**
	 * Returns the decorated rule. The decorated rule acts like a
	 * <i>singleton</i> (i.e. once set, it will never change again).
	 * 
	 * @return the decorated rule
	 */
	public Rule getRule() {
		return this.rule;
	}

	/**
	 * Sets the decorated rule. The decorated rule is only allow to be set once.
	 * It acts like a <i>singleton</i> (i.e. once set, it will never change
	 * again).
	 * 
	 * @param rule
	 *            the decorated rule
	 */
	public void setRule(Rule rule) {
		if (this.rule == null)
			this.rule = rule;
		else
			throw new IllegalStateException(ONLY_ONCE);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && Decorator.class.isAssignableFrom(obj.getClass())) {
			boolean equality = true;
			Decorator decorator = (Decorator) obj;
			if (this.rule == null)
				equality &= decorator.rule == null;
			else
				equality &= this.rule.equals(decorator.getRule());
			equality &= super.equals(obj);
			return equality;
		}
		return false;
	}

}