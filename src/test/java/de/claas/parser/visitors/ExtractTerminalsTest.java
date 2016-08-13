package de.claas.parser.visitors;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.claas.parser.rules.Conjunction;
import de.claas.parser.rules.Disjunction;
import de.claas.parser.rules.NonTerminal;
import de.claas.parser.rules.Optional;
import de.claas.parser.rules.Repetition;
import de.claas.parser.rules.Terminal;

public class ExtractTerminalsTest {

	private static final String RULE_NAME = "ruleName";
	private static final Terminal TERMINAL_RULE_ALPHA = new Terminal("A", "B", "C");
	private static final Terminal TERMINAL_RULE_NUM = new Terminal("1", "2", "3");
	private ExtractTerminals visitor;

	@Before
	public void before() {
		visitor = new ExtractTerminals();
	}
	
	@Test
	public void shouldHandleConjunctions() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Conjunction(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM));
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM);
	}
	
	@Test
	public void shouldHandleDisjunctions() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Disjunction(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM));
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_ALPHA, TERMINAL_RULE_NUM);
	}
	
	@Test
	public void shouldHandleOptionals() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Optional(TERMINAL_RULE_ALPHA));
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_ALPHA);
	}
	
	@Test
	public void shouldHandleRepetitions() {
		NonTerminal rule = new NonTerminal(RULE_NAME, new Repetition(TERMINAL_RULE_ALPHA));
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_ALPHA);
	}
	
	@Test
	public void shouldHandleTerminals() {
		NonTerminal rule = new NonTerminal(RULE_NAME, TERMINAL_RULE_ALPHA);
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_ALPHA);
	}
	
	@Test
	public void shouldHandleRecursiveRules() {
		NonTerminal rule = new NonTerminal(RULE_NAME);
		rule.setRule(new Disjunction(rule, TERMINAL_RULE_NUM));
		rule.visit(visitor);
		assertTerminals(TERMINAL_RULE_NUM);
	}
	
	private void assertTerminals(Terminal...terminals) {
		List<String> expectedTerminals = new ArrayList<>();
		for(Terminal terminal : terminals) {
			Iterator<String> iterator = terminal.getTerminals();
			while(iterator.hasNext())
				expectedTerminals.add(iterator.next());
		}
		
		List<String> actualTerminals = visitor.getTerminals();
		for(String terminal : expectedTerminals) {
			assertTrue("List of actual terminals did not include '" + terminal + "' (" + actualTerminals + ").", actualTerminals.contains(terminal));
		}
		assertEquals("The actual list of terminals is larger than expected.", expectedTerminals.size(), actualTerminals.size());
	}

}
