package de.claas.parser.grammars.abnf;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.GrammarTest;

/**
 * 
 * The JUnit test for class {@link AugmentedBackusNaur}. It is intended to
 * collect and document a set of test cases for the tested class. Please refer
 * to the individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 *
 */
public class AugmentedBackusNaurTest extends GrammarTest<AugmentedBackusNaur> {

	@Override
	protected AugmentedBackusNaur build() {
		return new AugmentedBackusNaur();
	}

	@Test
	public void shouldHandleConcatenation() {
		Grammar grammar = build();
		assertNotNull(grammar.parse("rule = \"hel\" \"lo\"\r\n", false));
	}

	@Test
	public void shouldHandleAlternation() {
		Grammar grammar = build();
		assertNotNull(grammar.parse("rule = \"hel\" / \"lo\"\r\n", false));
	}

	@Test
	public void shouldHandleIncrementalAlternatives() {
		Grammar grammar = build();
		assertNotNull(grammar.parse("rule = \"hel\"\r\nrule =/ \"lo\"\r\n", false));
	}

	@Test
	public void shouldHandleComment() {
		Grammar grammar = build();
		assertNotNull(grammar.parse("rule = \"R\" ; rrrrrrrr RRRR\r\n", false));
	}

	@Test
	public void shouldHandleRepetition() {
		Grammar grammar = build();
		assertNotNull(grammar.parse("rule = *\"R\"\r\n", false));
		assertNotNull(grammar.parse("rule = 2*\"R\"\r\n", false));
		assertNotNull(grammar.parse("rule = *2\"R\"\r\n", false));
	}

	@Test
	public void shouldHandleGroup() {
		Grammar grammar = build();
		assertNotNull(grammar.parse("rule = (\"hello\")\r\n", false));
	}

	@Test
	public void shouldHandleOption() {
		Grammar grammar = build();
		assertNotNull(grammar.parse("rule = [\"hello\"]\r\n", false));
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
		String data = rules.stream().collect(Collectors.joining("\r\n")) + "\r\n";
		assertNotNull(grammar.parse(data, false));
	}

}
