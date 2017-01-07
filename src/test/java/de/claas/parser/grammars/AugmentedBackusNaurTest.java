package de.claas.parser.grammars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.GrammarTest;
import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.builders.AugmentedBackusNaurBuilder;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.NumberValue;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;
import de.claas.parser.visitors.NodeToString;

/**
 * The JUnit test for class {@link AugmentedBackusNaur}. It is intended to
 * collect and document a set of test cases for the tested class. Please refer
 * to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 */
public class AugmentedBackusNaurTest extends GrammarTest<AugmentedBackusNaur> {

	@Override
	protected AugmentedBackusNaur build() {
		return new AugmentedBackusNaur();
	}

	@Test
	public void shouldHandleAlternation() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = \"hel\" / \"lo\"\r\n", false);

		Rule hel = new CharacterValue("hel");
		Rule lo = new CharacterValue("lo");
		Rule disjunction = new Disjunction(hel, lo);
		NonTerminal rule = new NonTerminal("rule", disjunction);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleConcatenation() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = \"hel\" \"lo\"\r\n", false);

		Rule hel = new CharacterValue("hel");
		Rule lo = new CharacterValue("lo");
		Rule conjunction = new Conjunction(hel, lo);
		NonTerminal rule = new NonTerminal("rule", conjunction);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleArbitraryRepetitions() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = *\"R\"\r\n", false);

		Rule r = new CharacterValue("R");
		Rule repetition = new Repetition(r);
		NonTerminal rule = new NonTerminal("rule", repetition);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleExactRepetitions() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = 4\"R\"\r\n", false);

		Rule r = new CharacterValue("R");
		Rule repetition = new Repetition(r, 4, 4);
		NonTerminal rule = new NonTerminal("rule", repetition);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleMinimumRepetitions() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = 23*\"R\"\r\n", false);

		Rule r = new CharacterValue("R");
		Rule repetition = new Repetition(r, 23, Integer.MAX_VALUE);
		NonTerminal rule = new NonTerminal("rule", repetition);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleMaximumRepetitions() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = *2\"R\"\r\n", false);

		Rule r = new CharacterValue("R");
		Rule repetition = new Repetition(r, 0, 2);
		NonTerminal rule = new NonTerminal("rule", repetition);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleRulename() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = \"hello\" world\r\n", false);

		Rule hello = new CharacterValue("hello");
		Rule world = new NonTerminal("world");
		Rule conjunction = new Conjunction(hello, world);
		NonTerminal rule = new NonTerminal("rule", conjunction);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleGroup() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = (\"hello\")\r\n", false);

		Rule hello = new Conjunction(new CharacterValue("hello"));
		Rule conjunction = new Conjunction(hello);
		NonTerminal rule = new NonTerminal("rule", conjunction);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleOption() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = [\"hello\"]\r\n", false);

		Rule hello = new CharacterValue("hello");
		Rule optional = new Optional(hello);
		NonTerminal rule = new NonTerminal("rule", optional);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleCaseSensitiveCharVal() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = %s\"helLO\"\r\n", false);

		Rule hello = new CharacterValue(true, "helLO");
		NonTerminal rule = new NonTerminal("rule", hello);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleCaseInsensitiveCharVal() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = \"helLO\"\r\n", false);

		Rule hello = new CharacterValue(false, "helLO");
		NonTerminal rule = new NonTerminal("rule", hello);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleSingleBinaryValue() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = %b10101\r\n", false);

		Rule value = new NumberValue(2, (char) 0b10101);
		NonTerminal rule = new NonTerminal("rule", value);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleMultipleBinaryValues() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = %b1.11.0\r\n", false);

		Rule value = new NumberValue(2, (char) 0b01, (char) 0b11, (char) 0b00);
		NonTerminal rule = new NonTerminal("rule", value);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleRangeOfBinaryValues() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = %b1-11\r\n", false);

		Rule value = new NumberValue(2, 0b1, 0b11);
		NonTerminal rule = new NonTerminal("rule", value);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleSingleDecimalValue() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = %d123\r\n", false);

		Rule value = new NumberValue(10, (char) 123);
		NonTerminal rule = new NonTerminal("rule", value);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleMultipleDecimalValues() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = %d12.3\r\n", false);

		Rule value = new NumberValue(10, new char[] { 12, 3 });
		NonTerminal rule = new NonTerminal("rule", value);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();

		NodeToString r = new NodeToString();
		actual.visit(r);
		String a = r.toString();
		r = new NodeToString();
		expected.visit(r);
		String b = r.toString();

		assertEquals(b, a);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleRangeOfDecimalValues() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = %d2-4\r\n", false);

		Rule value = new NumberValue(10, 2, 4);
		NonTerminal rule = new NonTerminal("rule", value);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleSingleHexValue() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = %xff\r\n", false);

		Rule value = new NumberValue(16, (char) 0xff);
		NonTerminal rule = new NonTerminal("rule", value);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleMultipleHexValues() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = %xa.bb\r\n", false);

		Rule value = new NumberValue(16, new char[] { 0xa, 0xbb });
		NonTerminal rule = new NonTerminal("rule", value);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();

		NodeToString r = new NodeToString();
		actual.visit(r);
		String a = r.toString();
		r = new NodeToString();
		expected.visit(r);
		String b = r.toString();

		assertEquals(b, a);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleRangeOfHexValues() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = %xf0-ff\r\n", false);

		Rule value = new NumberValue(16, 0xf0, 0xff);
		NonTerminal rule = new NonTerminal("rule", value);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleIncrementalAlternatives() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = \"hel\"\r\nrule =/ \"lo\"\r\n", false);

		Terminal hel = new CharacterValue("hel");
		NonTerminal rule1 = new NonTerminal("rule", hel);
		Terminal lo = new CharacterValue("lo");
		NonTerminal rule2 = new NonTerminal("rule", lo);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule1).rule(rule2).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleComment() {
		Grammar grammar = build();
		Node actual = grammar.parse("rule = \"R\"; rrrrrrrr RRRR\r\n", false);

		Rule r = new CharacterValue("R");
		NonTerminal rule = new NonTerminal("rule", "rrrrrrrr RRRR", r);
		Node expected = new AugmentedBackusNaurBuilder().rule(rule).build();
		assertEquals(expected, actual);
	}

	@Test
	public void shouldHandleABNFSpecification() {
		List<String> rules = new ArrayList<>();
		rules.add("rulelist       =  1*( rule / (*c-wsp c-nl) )");
		rules.add(
				"rule           =  rulename defined-as elements c-nl ; continues if next line starts with white space");
		rules.add("rulename       =  ALPHA *(ALPHA / DIGIT / \"-\")");
		rules.add(
				"defined-as     =  *c-wsp (\"=\" / \"=/\") *c-wsp ; basic rules definition and incremental alternatives");
		rules.add("elements       =  alternation *c-wsp");
		rules.add("c-wsp          =  WSP / (c-nl WSP)");
		rules.add("c-nl           =  comment / CRLF ; comment or newline");
		rules.add("comment        =  \";\" *(WSP / VCHAR) CRLF");
		rules.add("alternation    =  concatenation *(*c-wsp \"/\" *c-wsp concatenation)");
		rules.add("concatenation  =  repetition *(1*c-wsp repetition)");
		rules.add("repetition     =  [repeat] element");
		rules.add("repeat         =  1*DIGIT / (*DIGIT \"*\" *DIGIT)");
		rules.add("element        =  rulename / group / option / char-val / num-val / prose-val");
		rules.add("group          =  \"(\" *c-wsp alternation *c-wsp \")\"");
		rules.add("option         =  \"[\" *c-wsp alternation *c-wsp \"]\"");
		rules.add(
				"char-val       =  DQUOTE *(%x20-21 / %x23-7E) DQUOTE ; quoted string of SP and VCHAR without DQUOTE");
		rules.add("num-val        =  \"%\" (bin-val / dec-val / hex-val)");
		rules.add(
				"bin-val        =  \"b\" 1*BIT [ 1*(\".\" 1*BIT) / (\"-\" 1*BIT) ]	; series of concatenated bit values or single ONEOF range");
		rules.add("dec-val        =  \"d\" 1*DIGIT [ 1*(\".\" 1*DIGIT) / (\"-\" 1*DIGIT) ]");
		rules.add("hex-val        =  \"x\" 1*HEXDIG [ 1*(\".\" 1*HEXDIG) / (\"-\" 1*HEXDIG) ]");
		rules.add(
				"prose-val      =  \"<\" *(%x20-3D / %x3F-7E) \">\" ; bracketed string of SP and VCHAR without angles prose description, to be used as last resort");
		rules.add("");
		rules.add("ALPHA          =  %x41-5A / %x61-7A   ; A-Z / a-z");
		rules.add("BIT            =  \"0\" / \"1\"");
		rules.add("CR             =  %x0D ; carriage return");
		rules.add("CRLF           =  CR LF ; Internet standard newline");
		rules.add("DIGIT          =  %x30-39 ; 0-9");
		rules.add("DQUOTE         =  %x22 ; \" (Double Quote)");
		rules.add("HEXDIG         =  DIGIT / \"A\" / \"B\" / \"C\" / \"D\" / \"E\" / \"F\"");
		rules.add("HTAB           =  %x09 ; horizontal tab");
		rules.add("LF             =  %x0A ; linefeed");
		rules.add("SP             =  %x20");
		rules.add("VCHAR          =  %x21-7E ; visible (printing) characters");
		rules.add("WSP            =  SP / HTAB ; white space");
		rules.add("");
		rules.add("");
		rules.add("CHAR           =  %x01-7F ; any 7-bit US-ASCII character, excluding NUL");
		rules.add("CTL            =  %x00-1F / %x7F ; controls");
		rules.add("LWSP           =  *(WSP / CRLF WSP) ; linear white space (past newline)");
		rules.add("OCTET          =  %x00-FF ; 8 bits of data");

		Grammar grammar = build();
		String data = String.join("\r\n", rules) + "\r\n";
		assertNotNull(grammar.parse(data, false));
	}

}
