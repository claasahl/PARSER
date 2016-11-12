package de.claas.parser.grammars;

import de.claas.parser.Grammar;
import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.rules.CharacterValue;
import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.NumberValue;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;

/**
 * The class {@link AugmentedBackusNaur}. It is mainly intended for educational
 * purposes. It represents a grammar for grammars in augmented Backus Naur form.
 * Instances of this class parse sentences of the below grammar and return the
 * result as a tree of {@link Node} instances.
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
 * <li>char-val = case-insensitive-string / case-sensitive-string</li>
 * <li>case-insensitive-string = [ "%i" ] quoted-string</li>
 * <li>case-sensitive-string = "%s" quoted-string</li>
 * <li>quoted-string = DQUOTE *(%x20-21 / %x23-7E) DQUOTE ; quoted string of SP
 * and VCHAR without DQUOTE</li>
 * <li>num-val = "%" (bin-val / dec-val / hex-val)</li>
 * <li>bin-val = "b" 1*BIT [ 1*("." 1*BIT) / ("-" 1*BIT) ] ; series of
 * concatenated bit values or single ONEOF range</li>
 * <li>dec-val = "d" 1*DIGIT [ 1*("." 1*DIGIT) / ("-" 1*DIGIT) ]</li>
 * <li>hex-val = "x" 1*HEXDIG [ 1*("." 1*HEXDIG) / ("-" 1*HEXDIG) ]</li>
 * <li>prose-val = "&lt;" *(%x20-3D / %x3F-7E) "&gt;" ; bracketed string of SP
 * and VCHAR without angles prose description, to be used as last resort</li>
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
 * </ul>
 * The grammar has been written in augmented Backus Naur form (ABNF), as
 * specified in <a href="https://www.ietf.org/rfc/rfc5234.txt">RFC 5234</a> and
 * updated by <a href="https://www.ietf.org/rfc/rfc7405.txt">RFC 7405</a>.
 * 
 * @author Claas Ahlrichs
 *
 */
public class AugmentedBackusNaur extends Grammar {

	/**
	 * Constructs a new {@link AugmentedBackusNaur} with default parameters.
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
		int max = Integer.MAX_VALUE;
		Rule slash = new CharacterValue("/");
		Rule s = new CharacterValue("*");
		Rule l = new CharacterValue("(");
		Rule r = new CharacterValue(")");
		Rule ll = new CharacterValue("[");
		Rule rr = new CharacterValue("]");

		Rule tmpAlternation = new Conjunction();

		// option = "[" *c-wsp alternation *c-wsp "]"
		NonTerminal option = new NonTerminal("option",
				new Conjunction(ll, new Repetition(cWSP()), tmpAlternation, new Repetition(cWSP()), rr));

		// group = "(" *c-wsp alternation *c-wsp ")"
		NonTerminal group = new NonTerminal("group",
				new Conjunction(l, new Repetition(cWSP()), tmpAlternation, new Repetition(cWSP()), r));

		// element = rulename / group / option / char-val / num-val / prose-val
		NonTerminal element = new NonTerminal("element",
				new Disjunction(rulename(), group, option, charVal(), numVal(max), proseVal()));

		// repeat = 1*DIGIT / (*DIGIT "*" *DIGIT)
		NonTerminal repeat = new NonTerminal("repeat", new Disjunction(
				new Conjunction(new Repetition(digit()), s, new Repetition(digit())), new Repetition(digit(), 1, max)));

		// repetition = [repeat] element
		NonTerminal repetition = new NonTerminal("repetition", new Conjunction(new Optional(repeat), element));

		// concatenation = repetition *(1*c-wsp repetition)
		NonTerminal concatenation = new NonTerminal("concatenation", new Conjunction(repetition,
				new Repetition(new Conjunction(new Repetition(cWSP(), 1, max), repetition))));

		// alternation = concatenation *(*c-wsp "/" *c-wsp concatenation)
		NonTerminal alternation = new NonTerminal("alternation", new Conjunction(concatenation,
				new Repetition(new Conjunction(new Repetition(cWSP()), slash, new Repetition(cWSP()), concatenation))));
		tmpAlternation.addChild(alternation);

		// elements = alternation *c-wsp
		NonTerminal elements = new NonTerminal("elements", new Conjunction(tmpAlternation, new Repetition(cWSP())));

		// rule = rulename defined-as elements c-nl ; continues if next line
		// starts with white space
		NonTerminal rule = new NonTerminal("rule", new Conjunction(rulename(), definedAs(), elements, cNL()));

		// rulelist = 1*( rule / (*c-wsp c-nl) )
		Rule rule34 = new Disjunction(rule, new Conjunction(new Repetition(cWSP()), cNL()));
		NonTerminal ruleList = new NonTerminal("rulelist", new Repetition(rule34, 1, max));
		return ruleList;
	}

	private static NonTerminal definedAs() {
		Rule eq = CharacterValue.alternatives(false, "=", "=/");

		// defined-as = *c-wsp ("=" / "=/") *c-wsp ; basic rules definition and
		// incremental alternatives
		return new NonTerminal("defined-as", new Conjunction(new Repetition(cWSP()), eq, new Repetition(cWSP())));
	}

	private static NonTerminal rulename() {
		// rulename = ALPHA *(ALPHA / DIGIT / "-")
		return new NonTerminal("rulename",
				new Conjunction(alpha(), new Repetition(new Disjunction(alpha(), digit(), new CharacterValue("-")))));
	}

	private static NonTerminal charVal() {
		// quoted-string = DQUOTE *(%x20-21 / %x23-7E) DQUOTE ; quoted string of
		// SP and VCHAR without DQUOTE
		NonTerminal quotedString = new NonTerminal("quoted-string",
				new Conjunction(dQuote(),
						new Repetition(
								new Disjunction(new NumberValue(16, 0x20, 0x21), new NumberValue(16, 0x23, 0x7e))),
						dQuote()));

		// case-insensitive-string = [ "%i" ] quoted-string
		NonTerminal caseInsensitiveString = new NonTerminal("case-insensitive-string",
				new Conjunction(new Optional(new CharacterValue("%i")), quotedString));

		// case-sensitive-string = "%s" quoted-string
		NonTerminal caseSensitiveString = new NonTerminal("case-sensitive-string",
				new Conjunction(new CharacterValue("%s"), quotedString));

		// char-val = case-insensitive-string / case-sensitive-string
		return new NonTerminal("char-val", new Disjunction(caseInsensitiveString, caseSensitiveString));
	}

	private static NonTerminal proseVal() {
		Rule lll = new CharacterValue("<");
		Rule rrr = new CharacterValue(">");

		// prose-val = "<" *(%x20-3D / %x3F-7E) ">" ; bracketed string of SP and
		// VCHAR without angles prose description, to be used as last resort
		return new NonTerminal("prose-val",
				new Conjunction(lll,
						new Repetition(
								new Disjunction(new NumberValue(16, 0x20, 0x3d), new NumberValue(16, 0x3f, 0x7e))),
						rrr));
	}

	private static NonTerminal numVal(int max) {
		Rule dash = new CharacterValue("-");
		Rule dot = new CharacterValue(".");
		Rule bNum = new CharacterValue("b");
		Rule dNum = new CharacterValue("d");
		Rule xNum = new CharacterValue("x");

		// hex-val = "x" 1*HEXDIG [ 1*("." 1*HEXDIG) / ("-" 1*HEXDIG) ]
		Rule rule31 = new Repetition(hexdig(), 1, max);
		NonTerminal hexVal = new NonTerminal("hex-val", new Conjunction(xNum, rule31,
				new Optional(new Disjunction(
						new Conjunction(new Conjunction(dot, rule31), new Repetition(new Conjunction(dot, rule31))),
						new Conjunction(dash, rule31)))));

		// dec-val = "d" 1*DIGIT [ 1*("." 1*DIGIT) / ("-" 1*DIGIT) ]
		Rule rule32 = new Repetition(digit(), 1, max);
		NonTerminal decVal = new NonTerminal("dec-val", new Conjunction(dNum, rule32,
				new Optional(new Disjunction(
						new Conjunction(new Conjunction(dot, rule32), new Repetition(new Conjunction(dot, rule32))),
						new Conjunction(dash, rule32)))));

		// bin-val = "b" 1*BIT [ 1*("." 1*BIT) / ("-" 1*BIT) ] ; series of
		// concatenated bit values or single ONEOF range
		Rule rule33 = new Repetition(bit(), 1, max);
		NonTerminal binVal = new NonTerminal("bin-val", new Conjunction(bNum, rule33,
				new Optional(new Disjunction(
						new Conjunction(new Conjunction(dot, rule33), new Repetition(new Conjunction(dot, rule33))),
						new Conjunction(dash, rule33)))));

		// num-val = "%" (bin-val / dec-val / hex-val)
		return new NonTerminal("num-val",
				new Conjunction(new CharacterValue("%"), new Disjunction(binVal, decVal, hexVal)));
	}

	private static NonTerminal cWSP() {
		// c-wsp = WSP / (c-nl WSP)
		return new NonTerminal("c-wsp", new Disjunction(wsp(), new Conjunction(cNL(), wsp())));
	}

	private static NonTerminal cNL() {
		// c-nl = comment / CRLF ; comment or newline
		return new NonTerminal("c-nl", new Disjunction(comment(), crlf()));
	}

	private static NonTerminal comment() {
		// comment = ";" *(WSP / VCHAR) CRLF
		return new NonTerminal("comment",
				new Conjunction(new CharacterValue(";"), new Repetition(new Disjunction(wsp(), vchar())), crlf()));
	}

	private static NonTerminal alpha() {
		// ALPHA = %x41-5A / %x61-7A ; A-Z / a-z
		return new NonTerminal("alpha", new Disjunction(new NumberValue(16, 'A', 'Z'), new NumberValue(16, 'a', 'z')));
	}

	private static NonTerminal bit() {
		// BIT = "0" / "1"
		return new NonTerminal("bit", CharacterValue.alternatives(false, "0", "1"));
	}

	private static NonTerminal digit() {
		// DIGIT = %x30-39 ; 0-9
		return new NonTerminal("digit", new NumberValue(16, '0', '9'));
	}

	private static NonTerminal hexdig() {
		// HEXDIG = DIGIT / "A" / "B" / "C" / "D" / "E" / "F"
		return new NonTerminal("hexdig", CharacterValue.alternatives(false, "0", "1", "2", "3", "4", "5", "6", "7", "8",
				"9", "A", "B", "C", "D", "E", "F"));
	}

	private static NonTerminal dQuote() {
		// DQUOTE = %x22 ; " (Double Quote)
		return new NonTerminal("dQuote", new CharacterValue("\""));
	}

	private static NonTerminal crlf() {
		// CRLF = CR LF ; Internet standard newline
		return new NonTerminal("crlf", new CharacterValue("\r\n"));
	}

	private static NonTerminal vchar() {
		// VCHAR = %x21-7E ; visible (printing) characters
		return new NonTerminal("vchar", new NumberValue(16, 0x21, 0x7e));
	}

	private static NonTerminal wsp() {
		// WSP = SP / HTAB ; white space
		return new NonTerminal("wsp", CharacterValue.alternatives(false, "" + (char) 0x20, "" + (char) 0x09));
	}

	/**
	 * A support function that returns the ABFN marker for the specified radix.
	 * Only radix 16, 10 and 2 are supported! Any other radix will return an
	 * empty string.
	 * 
	 * @param radix
	 *            the radix
	 * @return the ABFN marker for the specified radix, an empty string if the
	 *         radix is not valid / supported
	 */
	protected static String marker(int radix) {
		switch (radix) {
		case 16:
			return "x";
		case 10:
			return "d";
		case 2:
			return "b";
		default:
			return "";
		}
	}

}
