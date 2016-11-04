package de.claas.parser.builders;

import de.claas.parser.Node;
import de.claas.parser.grammars.HelloWorld;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;

/**
 * 
 * The class {@link HelloWorldBuilder}. It is a support class for building
 * node-structures the encapsulate the ubiquitous phrase "hello world" as
 * understood by {@link HelloWorld}.
 * <p>
 * By default, the encapsulated phrase is in English and thus "hello world".
 *
 * @author Claas Ahlrichs
 *
 */
public class HelloWorldBuilder {

	private String language;
	private String hello;
	private String world;

	/**
	 * Constructs a new {@link HelloWorldBuilder} with default parameters.
	 */
	public HelloWorldBuilder() {
		this.language = "en";
		this.hello = "hello";
		this.world = "world";
	}

	/**
	 * Constructs a new {@link HelloWorldBuilder} with the specified parameters.
	 * 
	 * @param language
	 *            the language
	 * @param hello
	 *            the word for "hello" in the specified language
	 * @param world
	 *            the word for "world" in the specified language
	 */
	public HelloWorldBuilder(String language, String hello, String world) {
		super();
		this.language = language;
		this.hello = hello;
		this.world = world;
	}

	/**
	 * Updates the hello-part of this builder's phrase.
	 * 
	 * @param hello
	 *            the phrase's hello-part
	 * @return this builder
	 */
	public HelloWorldBuilder hello(String hello) {
		this.hello = hello;
		return this;
	}

	/**
	 * Updates the world-part of this builder's phrase.
	 * 
	 * @param world
	 *            the phrase's hello-part
	 * @return this builder
	 */
	public HelloWorldBuilder world(String world) {
		this.world = world;
		return this;
	}

	/**
	 * Updates the hello-part of this builder's phrase.
	 * 
	 * @param language
	 *            the phrase's language
	 * @return this builder
	 */
	public HelloWorldBuilder language(String language) {
		this.language = language;
		return this;
	}

	/**
	 * Builds the node-structure that represent this builder's phrase.
	 * 
	 * @return the node-structure that represent this builder's phrase.
	 */
	public Node build() {
		Node t1 = new TerminalNode(this.hello);
		Node t2 = new TerminalNode(" ");
		Node t3 = new TerminalNode(this.world);
		Node n1 = new NonTerminalNode(this.language);
		n1.addChild(t1);
		n1.addChild(t2);
		n1.addChild(t3);
		Node expected = new NonTerminalNode("hello-world");
		expected.addChild(n1);
		return expected;
	}

}
