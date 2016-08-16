package de.claas.parser.grammars;

import de.claas.parser.Grammar;
import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

/**
 * The class {@link Number}. It is mainly intended for educational purposes. It
 * represents a grammar for numbers. Instances of this class parse sentences of
 * the below grammar and return the result as a tree of {@link Node} instances.
 * <ul>
 * <li>rulelist = 1*( rule / (*c-wsp c-nl) )</li>
 * <li>rule = rulename defined-as elements c-nl ; continues if next line starts
 * with white space</li>
 * <li>rulename = ALPHA *(ALPHA / DIGIT / "-")</li>
 * <li>defined-as = *c-wsp ("=" / "=/") *c-wsp ; basic rules definition and
 * incremental alternatives</li>
 * <li>elements = alternation *c-wsp</li>
 * <li>c-wsp = WSP / (c-nl WSP)</li>
 * <li>c-nl = comment / CRLF ; comment or newline</li>
 * <li>comment = ";" *(WSP / VCHAR) CRLF</li>
 * <li>alternation = concatenation *(*c-wsp "/" *c-wsp concatenation)</li>
 * <li>concatenation = repetition *(1*c-wsp repetition)</li>
 * <li>repetition = [repeat] element</li>
 * <li>repeat = 1*DIGIT / (*DIGIT "*" *DIGIT)</li>
 * <li>element = rulename / group / option / char-val / num-val / prose-val</li>
 * <li>group = "(" *c-wsp alternation *c-wsp ")"</li>
 * <li>option = "[" *c-wsp alternation *c-wsp "]"</li>
 * <li>char-val = DQUOTE *(%x20-21 / %x23-7E) DQUOTE ; quoted string of SP and
 * VCHAR without DQUOTE</li>
 * <li>num-val = "%" (bin-val / dec-val / hex-val)</li>
 * <li>bin-val = "b" 1*BIT [ 1*("." 1*BIT) / ("-" 1*BIT) ] ; series of
 * concatenated bit values or single ONEOF range</li>
 * <li>dec-val = "d" 1*DIGIT [ 1*("." 1*DIGIT) / ("-" 1*DIGIT) ]</li>
 * <li>hex-val = "x" 1*HEXDIG [ 1*("." 1*HEXDIG) / ("-" 1*HEXDIG) ]</li>
 * <li>prose-val = "<" *(%x20-3D / %x3F-7E) ">" ; bracketed string of SP and
 * VCHAR without angles prose description, to be used as last resort</li>
 * <li>ALPHA = %x41-5A / %x61-7A ; A-Z / a-z</li>
 * <li>BIT = "0" / "1"</li>
 * <li>CR = %x0D ; carriage return</li>
 * <li>CRLF = CR LF ; Internet standard newline</li>
 * <li>DIGIT = %x30-39 ; 0-9</li>
 * <li>DQUOTE = %x22 ; " (Double Quote)</li>
 * <li>HEXDIG = DIGIT / "A" / "B" / "C" / "D" / "E" / "F"</li>
 * <li>HTAB = %x09 ; horizontal tab</li>
 * <li>LF = %x0A ; linefeed</li>
 * <li>SP = %x20</li>
 * <li>VCHAR = %x21-7E ; visible (printing) characters</li>
 * <li>WSP = SP / HTAB ; white space</li>
 * <li>CHAR = %x01-7F ; any 7-bit US-ASCII character, excluding NUL</li>
 * <li>CTL = %x00-1F / %x7F ; controls</li>
 * <li>LWSP = *(WSP / CRLF WSP) ; linear white space (past newline)</li>
 * <li>OCTET = %x00-FF ; 8 bits of data</li>
 * </ul>
 * The grammar has been defined as augmented Backus Naur form. Details on syntax
 * and grammar can be found in
 * <a href="https://www.ietf.org/rfc/rfc2234.txt">RFC 2234</a>.
 * 
 * @author Claas Ahlrichs
 *
 */
public class AugmentedBackusNaur extends Grammar {

	/**
	 * Creates an instance with default parameters.
	 */
	public AugmentedBackusNaur() {
		super(grammar());
	}

	/**
	 * Returns the above described grammar.
	 * 
	 * @return the above described grammar
	 */
	private static NonTerminal grammar() {
		Terminal dash = new Terminal("-");
		Terminal eq = new Terminal("=", "=/");
		Terminal c = new Terminal(";");
		Terminal slash = new Terminal("/");
		Terminal s = new Terminal("*");
		Terminal d = new Terminal(".");
		Terminal l = new Terminal("(");
		Terminal r = new Terminal(")");
		Terminal ll = new Terminal("[");
		Terminal rr = new Terminal("]");
		Terminal lll = new Terminal("<");
		Terminal rrr = new Terminal(">");
		Terminal p = new Terminal("%");
		Terminal bNum = new Terminal("b");
		Terminal dNum = new Terminal("d");
		Terminal xNum = new Terminal("x");

		// ALPHA = %x41-5A / %x61-7A ; A-Z / a-z
		NonTerminal alpha = new NonTerminal("alpha", new Disjunction(new Terminal('A', 'Z'), new Terminal('a', 'z')));
		// DIGIT = %x30-39 ; 0-9
		NonTerminal digit = new NonTerminal("digit", new Terminal('0', '9'));
		// WSP = SP / HTAB ; white space
		NonTerminal wsp = new NonTerminal("wsp", new Terminal("" + (char) 0x20, "" + (char) 0x09));
		// CRLF = CR LF ; Internet standard newline
		NonTerminal crlf = new NonTerminal("crlf", new Terminal((char) 0x0a, (char) 0x0d));
		// VCHAR = %x21-7E ; visible (printing) characters
		NonTerminal vchar = new NonTerminal("vchar", new Terminal((char) 0x21, (char) 0x7e));
		// DQUOTE = %x22 ; " (Double Quote)
		NonTerminal dQuote = new NonTerminal("dQuote", new Terminal("\""));
		// BIT = "0" / "1"
		NonTerminal bit = new NonTerminal("bit", new Terminal('0', '1'));
		// HEXDIG = DIGIT / "A" / "B" / "C" / "D" / "E" / "F"
		NonTerminal hexdig = new NonTerminal("hexdig", new Terminal("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"A", "B", "C", "D", "E", "F", "a", "b", "c", "d", "e", "f"));

		Rule tmpRulename = new Conjunction();
		Rule tmpAlternation = new Conjunction();

		// comment = ";" *(WSP / VCHAR) CRLF
		NonTerminal comment = new NonTerminal("comment",
				new Conjunction(c, new Repetition(new Disjunction(wsp, vchar)), crlf));

		// c-nl = comment / CRLF ; comment or newline
		NonTerminal cNL = new NonTerminal("c-nl", new Disjunction(comment, crlf));

		// c-wsp = WSP / (c-nl WSP)
		NonTerminal cWSP = new NonTerminal("c-wsp", new Disjunction(wsp, new Conjunction(cNL, wsp)));

		// hex-val = "x" 1*HEXDIG [ 1*("." 1*HEXDIG) / ("-" 1*HEXDIG) ]
		Rule rule31 = new Conjunction(hexdig, new Repetition(hexdig));
		NonTerminal hexVal = new NonTerminal("hex-val",
				new Conjunction(xNum, rule31,
						new Optional(new Disjunction(
								new Conjunction(new Conjunction(d, rule31), new Repetition(new Conjunction(d, rule31))),
								new Conjunction(dash, rule31)))));

		// prose-val = "<" *(%x20-3D / %x3F-7E) ">" ; bracketed string of SP and
		// VCHAR without angles prose description, to be used as last resort
		NonTerminal proseVal = new NonTerminal("prose-val", new Conjunction(lll, new Repetition(
				new Disjunction(new Terminal((char) 0x20, (char) 0x3d), new Terminal((char) 0x3f, (char) 0x7e))), rrr));

		// dec-val = "d" 1*DIGIT [ 1*("." 1*DIGIT) / ("-" 1*DIGIT) ]
		Rule rule32 = new Conjunction(digit, new Repetition(digit));
		NonTerminal decVal = new NonTerminal("dec-val",
				new Conjunction(dNum, rule32,
						new Optional(new Disjunction(
								new Conjunction(new Conjunction(d, rule32), new Repetition(new Conjunction(d, rule32))),
								new Conjunction(dash, rule32)))));

		// bin-val = "b" 1*BIT [ 1*("." 1*BIT) / ("-" 1*BIT) ] ; series of
		// concatenated bit values or single ONEOF range
		Rule rule33 = new Conjunction(bit, new Repetition(bit));
		NonTerminal binVal = new NonTerminal("bin-val",
				new Conjunction(bNum, rule33,
						new Optional(new Disjunction(
								new Conjunction(new Conjunction(d, rule33), new Repetition(new Conjunction(d, rule33))),
								new Conjunction(dash, rule33)))));

		// num-val = "%" (bin-val / dec-val / hex-val)
		NonTerminal numVal = new NonTerminal("num-val", new Conjunction(p, new Disjunction(binVal, decVal, hexVal)));

		// char-val = DQUOTE *(%x20-21 / %x23-7E) DQUOTE ; quoted string of SP
		// and VCHAR without DQUOTE
		NonTerminal charVal = new NonTerminal("char-val", new Conjunction(dQuote, new Repetition(
				new Disjunction(new Terminal((char) 0x20, (char) 0x21), new Terminal((char) 0x23, (char) 0x7e))),
				dQuote));

		// option = "[" *c-wsp alternation *c-wsp "]"
		NonTerminal option = new NonTerminal("option",
				new Conjunction(ll, new Repetition(cWSP), tmpAlternation, new Repetition(cWSP), rr));

		// group = "(" *c-wsp alternation *c-wsp ")"
		NonTerminal group = new NonTerminal("group",
				new Conjunction(l, new Repetition(cWSP), tmpAlternation, new Repetition(cWSP), r));

		// element = rulename / group / option / char-val / num-val / prose-val
		NonTerminal element = new NonTerminal("element",
				new Disjunction(tmpRulename, group, option, charVal, numVal, proseVal));

		// repeat = 1*DIGIT / (*DIGIT "*" *DIGIT)
		NonTerminal repeat = new NonTerminal("repeat",
				new Disjunction(new Conjunction(new Repetition(digit), s, new Repetition(digit)),
						new Conjunction(digit, new Repetition(digit))));

		// repetition = [repeat] element
		NonTerminal repetition = new NonTerminal("repetition", new Conjunction(new Optional(repeat), element));

		// concatenation = repetition *(1*c-wsp repetition)
		NonTerminal concatenation = new NonTerminal("concatenation",
				new Conjunction(repetition, new Repetition(new Conjunction(cWSP, new Repetition(cWSP), repetition))));

		// alternation = concatenation *(*c-wsp "/" *c-wsp concatenation)
		NonTerminal alternation = new NonTerminal("alternation", new Conjunction(concatenation,
				new Repetition(new Conjunction(new Repetition(cWSP), slash, new Repetition(cWSP), concatenation))));
		tmpAlternation.addChild(alternation);

		// elements = alternation *c-wsp
		NonTerminal elements = new NonTerminal("elements", new Conjunction(tmpAlternation, new Repetition(cWSP)));

		// defined-as = *c-wsp ("=" / "=/") *c-wsp ; basic rules definition and
		// incremental alternatives
		NonTerminal definedAs = new NonTerminal("defined-as",
				new Conjunction(new Repetition(cWSP), eq, new Repetition(cWSP)));

		// rulename = ALPHA *(ALPHA / DIGIT / "-")
		NonTerminal rulename = new NonTerminal("rulename",
				new Conjunction(alpha, new Repetition(new Disjunction(alpha, digit, dash))));
		tmpRulename.addChild(rulename);

		// rule = rulename defined-as elements c-nl ; continues if next line
		// starts with white space
		NonTerminal rule = new NonTerminal("rule", new Conjunction(tmpRulename, definedAs, elements, cNL));

		// rulelist = 1*( rule / (*c-wsp c-nl) )
		Rule rule34 = new Disjunction(rule, new Conjunction(new Repetition(cWSP), cNL));
		NonTerminal ruleList = new NonTerminal("rulelist", new Conjunction(rule34, new Repetition(rule34)));
		return ruleList;
	}

}
