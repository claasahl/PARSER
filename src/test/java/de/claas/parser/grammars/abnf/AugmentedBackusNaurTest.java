package de.claas.parser.grammars.abnf;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.claas.parser.Grammar;
import de.claas.parser.GrammarTest;
import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.results.NonTerminalNode;
import de.claas.parser.results.TerminalNode;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

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
		Node actual = grammar.parse("rule = \"hel\" \"lo\"\r\n", false);

		Node expected = generateNodes(new NonTerminal("rule", new Conjunction(new Terminal("hel"), new Terminal("lo"))));
		assertEquals(expected, actual);
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
		String data = String.join("\r\n", rules) + "\r\n";
		assertNotNull(grammar.parse(data, false));
	}

	private static Node generateNodes(NonTerminal actualRule) {
		Node rulename = new NonTerminalNode("rulename");
		String name = actualRule.getName();
		for (int index = 0; index < name.length(); index++) {
			Node alpha = new NonTerminalNode("alpha");
			String terminal = name.substring(index, index+1);
			alpha.addChild(new TerminalNode(terminal));
			rulename.addChild(alpha);
		}

		Node definedAs = new NonTerminalNode("defined-as");
		Node wsp = new NonTerminalNode("wsp");
		wsp.addChild(new TerminalNode(" "));
		Node cwsp = new NonTerminalNode("c-wsp");
		cwsp.addChild(wsp);
		definedAs.addChild(cwsp);
		definedAs.addChild(new TerminalNode("="));
		definedAs.addChild(cwsp);

		Node elements = new NonTerminalNode("elements");
		elements.addChild(generateAlternation(actualRule.getRule()));

		Node CRLF = new NonTerminalNode("crlf");
		CRLF.addChild(new TerminalNode("\r\n"));
		Node NL = new NonTerminalNode("c-nl");
		NL.addChild(CRLF);

		Node rule = new NonTerminalNode("rule");
		rule.addChild(rulename);
		rule.addChild(definedAs);
		rule.addChild(elements);
		rule.addChild(NL);

		Node rulelist = new NonTerminalNode("rulelist");
		rulelist.addChild(rule);
		return rulelist;
	}

	private static Node generateAlternation(Rule actualRule) {
		Node alternation = new NonTerminalNode("alternation");
		if (actualRule.getClass().isAssignableFrom(Disjunction.class)) {
			boolean firstChild = true;
			for (Rule child : actualRule) {
				if (!firstChild)
					alternation.addChild(new TerminalNode("/"));
				Node concatenation = generateConcatenation(child);
				alternation.addChild(concatenation);
				firstChild = false;
			}
		} else if (actualRule.getClass().isAssignableFrom(Terminal.class)) {
			Terminal rule = (Terminal) actualRule;
			Iterator<String> terminals = rule.getTerminals();
			boolean firstTerminal = true;
			
			while(terminals.hasNext()) {
				if (!firstTerminal)
					alternation.addChild(new TerminalNode("/"));
				
				String terminal = terminals.next();
				Node concatenation = generateConcatenation(new Terminal(terminal));
				alternation.addChild(concatenation);
				firstTerminal = false;				
			}
		} else {
			alternation.addChild(generateConcatenation(actualRule));
		}
		return alternation;
	}

	private static Node generateConcatenation(Rule actualRule) {
		Node concatenation = new NonTerminalNode("concatenation");
		if (actualRule.getClass().isAssignableFrom(Conjunction.class)) {
			boolean firstChild = true;
			for (Rule child : actualRule) {
				if (!firstChild) {
					Node wsp = new NonTerminalNode("wsp");
					wsp.addChild(new TerminalNode(" "));
					Node cwsp = new NonTerminalNode("c-wsp");
					cwsp.addChild(wsp);
					concatenation.addChild(cwsp);
				}
				
				Node repetition = generateRepetition(child);
				concatenation.addChild(repetition);
				firstChild = false;			
			}
		} else {
			concatenation.addChild(generateRepetition(actualRule));
		}
		return concatenation;
	}

	private static Node generateRepetition(Rule actualRule) {
		Node repetition = new NonTerminalNode("repetition");
		if (actualRule.getClass().isAssignableFrom(Repetition.class)) {
			Repetition rule = (Repetition) actualRule;

			Node repeat = new NonTerminalNode("repeat");
			int min = rule.getMinimumNumberOfRepetions();
			int max = rule.getMaximumNumberOfRepetions();
			if (min == max) {
				repeat.addChild(new TerminalNode(Integer.toString(min)));
			} else {
				if (min != 0)
					repeat.addChild(new TerminalNode(Integer.toString(min)));
				repeat.addChild(new TerminalNode("*"));
				if (max != Integer.MAX_VALUE)
					repeat.addChild(new TerminalNode(Integer.toString(max)));
			}
			repetition.addChild(repeat);
			repetition.addChild(generateElement(rule.getRule()));
		} else {
			repetition.addChild(generateElement(actualRule));
		}
		return repetition;
	}

	private static Node generateElement(Rule actualRule) {
		Node element = new NonTerminalNode("element");
		if (actualRule.getClass().isAssignableFrom(NonTerminal.class)) {
			NonTerminal rule = (NonTerminal) actualRule;
			Node rulename = new NonTerminalNode("rulename");
			String name = rule.getName();
			for (int index = 0; index < name.length(); index++) {
				Node alpha = new NonTerminalNode("alpha");
				String terminal = name.substring(index, index);
				alpha.addChild(new TerminalNode(terminal));
				rulename.addChild(alpha);
			}
			element.addChild(rulename);
		} else if (actualRule.getClass().isAssignableFrom(Terminal.class)) {
			Terminal rule = (Terminal) actualRule;
			Iterator<String> terminals = rule.getTerminals();
			String terminal = terminals.next();

			Node charVal = new NonTerminalNode("char-val");
			Node caseInsensitiveAtring = new NonTerminalNode("case-insensitive-string");
			charVal.addChild(caseInsensitiveAtring);
			Node quotedString = new NonTerminalNode("quoted-string");
			caseInsensitiveAtring.addChild(quotedString);
			
			Node dQuote = new NonTerminalNode("dQuote");
			dQuote.addChild(new TerminalNode("\""));
			quotedString.addChild(dQuote);
			for (int index = 0; index < terminal.length(); index++) {
				String character = terminal.substring(index, index+1);
				quotedString.addChild(new TerminalNode(character));
			}
			quotedString.addChild(dQuote);
			element.addChild(charVal);

			if (terminals.hasNext()) {
				throw new IllegalStateException("only one terminal is currently support!");
			}
		} else if (actualRule.getClass().isAssignableFrom(Optional.class)) {
			Optional rule = (Optional) actualRule;

			Node option = new NonTerminalNode("option");
			option.addChild(new TerminalNode("["));
			option.addChild(generateAlternation(rule.getRule()));
			option.addChild(new TerminalNode("["));
			element.addChild(option);
		} else {
			Node option = new NonTerminalNode("group");
			option.addChild(new TerminalNode("("));
			option.addChild(generateAlternation(actualRule));
			option.addChild(new TerminalNode(")"));
			element.addChild(option);
		}
		return element;
	}

}
