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

	private static final int MAX_VALUE = Integer.MAX_VALUE;
	private static final Rule DASH = new CharacterValue("-");

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
		return ruleList();
	}

	/**
	 * A support method that creates and returns the rule "rulelist" as defined
	 * in the above grammar.
	 * 
	 * @return the rule "rulelist" as defined in the above grammar
	 */
	private static NonTerminal ruleList() {
		Rule whitespace = new Conjunction(new Repetition(cWsp()), cNl());
		Rule rule = new Disjunction(rule(), whitespace);
		Rule rules = new Repetition(rule, 1, MAX_VALUE);
		NonTerminal ruleList = new NonTerminal("rulelist", rules);
		return ruleList;
	}

	/**
	 * A support method that creates and returns the rule "rule" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "rule" as defined in the above grammar
	 */
	private static NonTerminal rule() {
		Rule rule = new Conjunction(rulename(), definedAs(), elements(), cNl());
		return new NonTerminal("rule", rule);
	}

	/**
	 * A support method that creates and returns the rule "rulename" as defined
	 * in the above grammar.
	 * 
	 * @return the rule "rulename" as defined in the above grammar
	 */
	private static NonTerminal rulename() {
		Disjunction suffix = new Disjunction(alpha(), digit(), DASH);
		Rule rulename = new Conjunction(alpha(), new Repetition(suffix));
		return new NonTerminal("rulename", rulename);
	}

	/**
	 * A support method that creates and returns the rule "defined-as" as
	 * defined in the above grammar.
	 * 
	 * @return the rule "defined-as" as defined in the above grammar
	 */
	private static NonTerminal definedAs() {
		Rule eq = CharacterValue.alternatives(false, "=", "=/");
		Rule definedAs = new Conjunction(new Repetition(cWsp()), eq, new Repetition(cWsp()));
		return new NonTerminal("defined-as", definedAs);
	}

	/**
	 * A support method that creates and returns the rule "elements" as defined
	 * in the above grammar.
	 * 
	 * @return the rule "elements" as defined in the above grammar
	 */
	private static NonTerminal elements() {
		Rule elements = new Conjunction(alternation(), new Repetition(cWsp()));
		return new NonTerminal("elements", elements);
	}

	/**
	 * A support method that creates and returns the rule "c-wsp" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "c-wsp" as defined in the above grammar
	 */
	private static NonTerminal cWsp() {
		Rule whitespace = new Disjunction(wsp(), new Conjunction(cNl(), wsp()));
		return new NonTerminal("c-wsp", whitespace);
	}

	/**
	 * A support method that creates and returns the rule "c-nl" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "c-nl" as defined in the above grammar
	 */
	private static NonTerminal cNl() {
		Rule cNl = new Disjunction(comment(), crlf());
		return new NonTerminal("c-nl", cNl);
	}

	/**
	 * A support method that creates and returns the rule "comment" as defined
	 * in the above grammar.
	 * 
	 * @return the rule "comment" as defined in the above grammar
	 */
	private static NonTerminal comment() {
		Rule colon = new CharacterValue(";");
		Rule content = new Repetition(new Disjunction(wsp(), vchar()));
		Rule comment = new Conjunction(colon, content, crlf());
		return new NonTerminal("comment", comment);
	}

	/**
	 * A support method that creates and returns the rule "alternation" as
	 * defined in the above grammar.
	 * 
	 * @return the rule "alternation" as defined in the above grammar
	 */
	private static NonTerminal alternation() {
		Rule slash = new CharacterValue("/");
		Rule whitespace = new Repetition(cWsp());
		Rule wspConcatenation = new Conjunction(whitespace, slash, whitespace, concatenation());
		Rule alternation = new Conjunction(concatenation(), new Repetition(wspConcatenation));
		return new NonTerminal("alternation", alternation);
	}

	/**
	 * A support method that creates and returns the rule "concatenation" as
	 * defined in the above grammar.
	 * 
	 * @return the rule "concatenation" as defined in the above grammar
	 */
	private static NonTerminal concatenation() {
		Rule whitespace = new Repetition(cWsp(), 1, MAX_VALUE);
		Rule wspRepetition = new Conjunction(whitespace, repetition());
		Rule concatenation = new Conjunction(repetition(), new Repetition(wspRepetition));
		return new NonTerminal("concatenation", concatenation);
	}

	/**
	 * A support method that creates and returns the rule "repetition" as
	 * defined in the above grammar.
	 * 
	 * @return the rule "repetition" as defined in the above grammar
	 */
	private static NonTerminal repetition() {
		Rule repetition = new Conjunction(new Optional(repeat()), element());
		return new NonTerminal("repetition", repetition);
	}

	/**
	 * A support method that creates and returns the rule "repeat" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "repeat" as defined in the above grammar
	 */
	private static NonTerminal repeat() {
		Rule asterix = new CharacterValue("*");
		Rule digits = new Repetition(digit());
		Rule range = new Conjunction(digits, asterix, digits);
		Rule fixed = new Repetition(digit(), 1, MAX_VALUE);
		Rule repeat = new Disjunction(range, fixed);
		return new NonTerminal("repeat", repeat);
	}

	/**
	 * A support method that creates and returns the rule "element" as defined
	 * in the above grammar.
	 * 
	 * @return the rule "element" as defined in the above grammar
	 */
	private static NonTerminal element() {
		Rule element = new Disjunction(rulename(), group(), option(), charVal(), numVal(), proseVal());
		return new NonTerminal("element", element);
	}

	/**
	 * A support method that creates and returns the rule "group" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "group" as defined in the above grammar
	 */
	private static NonTerminal group() {
		Rule left = new CharacterValue("(");
		Rule right = new CharacterValue(")");
		Rule whitespace = new Repetition(cWsp());
		Rule group = new Conjunction(left, whitespace, alternation(), whitespace, right);
		return new NonTerminal("group", group);
	}

	/**
	 * A support method that creates and returns the rule "option" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "option" as defined in the above grammar
	 */
	private static NonTerminal option() {
		Rule left = new CharacterValue("[");
		Rule right = new CharacterValue("]");
		Rule whitespace = new Repetition(cWsp());
		Rule option = new Conjunction(left, whitespace, alternation(), whitespace, right);
		return new NonTerminal("option", option);
	}

	/**
	 * A support method that creates and returns the rule "char-val" as defined
	 * in the above grammar.
	 * 
	 * @return the rule "char-val" as defined in the above grammar
	 */
	private static NonTerminal charVal() {
		Rule charVal = new Disjunction(caseInsensitiveString(), caseSensitiveString());
		return new NonTerminal("char-val", charVal);
	}

	/**
	 * A support method that creates and returns the rule
	 * "case-insensitive-string" as defined in the above grammar.
	 * 
	 * @return the rule "case-insensitive-string" as defined in the above
	 *         grammar
	 */
	private static NonTerminal caseInsensitiveString() {
		Rule insensitive = new Optional(new CharacterValue("%i"));
		Rule caseInsensitiveString = new Conjunction(insensitive, quotedString());
		return new NonTerminal("case-insensitive-string", caseInsensitiveString);
	}

	/**
	 * A support method that creates and returns the rule
	 * "case-sensitive-string" as defined in the above grammar.
	 * 
	 * @return the rule "case-sensitive-string" as defined in the above grammar
	 */
	private static NonTerminal caseSensitiveString() {
		Rule sensitive = new CharacterValue("%s");
		Rule caseSensitiveString = new Conjunction(sensitive, quotedString());
		return new NonTerminal("case-sensitive-string", caseSensitiveString);
	}

	/**
	 * A support method that creates and returns the rule "quotedString" as
	 * defined in the above grammar.
	 * 
	 * @return the rule "quotedString" as defined in the above grammar
	 */
	private static NonTerminal quotedString() {
		Rule lowerRange = new NumberValue(16, 0x20, 0x21);
		Rule upperRange = new NumberValue(16, 0x23, 0x7e);
		Rule content = new Repetition(new Disjunction(lowerRange, upperRange));
		Rule quotedString = new Conjunction(dQuote(), content, dQuote());
		return new NonTerminal("quoted-string", quotedString);
	}

	/**
	 * A support method that creates and returns the rule "num-val" as defined
	 * in the above grammar.
	 * 
	 * @return the rule "num-val" as defined in the above grammar
	 */
	private static NonTerminal numVal() {
		Rule value = new Disjunction(binVal(), decVal(), hexVal());
		Rule numVal = new Conjunction(new CharacterValue("%"), value);
		return new NonTerminal("num-val", numVal);
	}

	/**
	 * A support method that creates and returns the rule "bin-val" as defined
	 * in the above grammar.
	 * 
	 * @return the rule "bin-val" as defined in the above grammar
	 */
	private static NonTerminal binVal() {
		return numericValue(marker(2), "bin-val", bit());
	}

	/**
	 * A support method that creates and returns the rule "dec-val" as defined
	 * in the above grammar.
	 * 
	 * @return the rule "dec-val" as defined in the above grammar
	 */
	private static NonTerminal decVal() {
		return numericValue(marker(10), "dec-val", digit());
	}

	/**
	 * A support method that creates and returns the rule "hex-val" as defined
	 * in the above grammar.
	 * 
	 * @return the rule "hex-val" as defined in the above grammar
	 */
	private static NonTerminal hexVal() {
		return numericValue(marker(16), "hex-val", hexdig());
	}

	/**
	 * A support method that creates and returns the rule "prose-val" as defined
	 * in the above grammar.
	 * 
	 * @return the rule "prose-val" as defined in the above grammar
	 */
	private static NonTerminal proseVal() {
		Rule left = new CharacterValue("<");
		Rule right = new CharacterValue(">");
		Rule lowerRange = new NumberValue(16, 0x20, 0x3d);
		Rule upperRange = new NumberValue(16, 0x3f, 0x7e);
		Rule value = new Disjunction(lowerRange, upperRange);
		Rule proseVal = new Conjunction(left, new Repetition(value), right);
		return new NonTerminal("prose-val", proseVal);
	}

	/**
	 * A support method that creates and returns the rule "alpha" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "alpha" as defined in the above grammar
	 */
	private static NonTerminal alpha() {
		NumberValue upperCase = new NumberValue(16, 'A', 'Z');
		NumberValue lowerCase = new NumberValue(16, 'a', 'z');
		Rule alpha = new Disjunction(upperCase, lowerCase);
		return new NonTerminal("alpha", alpha);
	}

	/**
	 * A support method that creates and returns the rule "bit" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "bit" as defined in the above grammar
	 */
	private static NonTerminal bit() {
		Rule bit = CharacterValue.alternatives(false, "0", "1");
		return new NonTerminal("bit", bit);
	}

	/**
	 * A support method that creates and returns the rule "crlf" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "crlf" as defined in the above grammar
	 */
	private static NonTerminal crlf() {
		Rule crlf = new CharacterValue("\r\n");
		return new NonTerminal("crlf", crlf);
	}

	/**
	 * A support method that creates and returns the rule "digit" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "digit" as defined in the above grammar
	 */
	private static NonTerminal digit() {
		Rule digit = new NumberValue(16, '0', '9');
		return new NonTerminal("digit", digit);
	}

	/**
	 * A support method that creates and returns the rule "dQuote" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "dQuote" as defined in the above grammar
	 */
	private static NonTerminal dQuote() {
		Rule dQuote = new CharacterValue("\"");
		return new NonTerminal("dQuote", dQuote);
	}

	/**
	 * A support method that creates and returns the rule "hexdig" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "hexdig" as defined in the above grammar
	 */
	private static Rule hexdig() {
		Rule hexdig = CharacterValue.alternatives(false, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B",
				"C", "D", "E", "F");
		return new NonTerminal("hexdig", hexdig);
	}

	/**
	 * A support method that creates and returns the rule "vchar" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "vchar" as defined in the above grammar
	 */
	private static NonTerminal vchar() {
		Rule vchar = new NumberValue(16, 0x21, 0x7e);
		return new NonTerminal("vchar", vchar);
	}

	/**
	 * A support method that creates and returns the rule "wsp" as defined in
	 * the above grammar.
	 * 
	 * @return the rule "wsp" as defined in the above grammar
	 */
	private static NonTerminal wsp() {
		Rule wsp = CharacterValue.alternatives(false, "" + (char) 0x20, "" + (char) 0x09);
		return new NonTerminal("wsp", wsp);
	}

	/**
	 * A support function that returns ...
	 * 
	 * @param identifier
	 * @param ruleName
	 * @param digit
	 * @return
	 */
	private static NonTerminal numericValue(String identifier, String ruleName, Rule digit) {
		Rule dot = new CharacterValue(".");
		Rule marker = new CharacterValue(identifier);
		Rule value = new Repetition(digit, 1, MAX_VALUE);
		Rule series = new Repetition(new Conjunction(dot, value), 1, MAX_VALUE);
		Rule range = new Conjunction(DASH, value);
		Rule numericVal = new Conjunction(marker, value, new Optional(new Disjunction(series, range)));
		return new NonTerminal(ruleName, numericVal);
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
