package de.claas.parser.visitors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.claas.parser.Node;
import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.State;
import de.claas.parser.exceptions.CyclicRuleException;
import de.claas.parser.results.IntermediateNode;
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
 * The class {@link Parser}. It is an implementation of the interface
 * {@link RuleVisitor}. It is intended to parse data into a tree of
 * {@link Node}s.
 * <p>
 * Each rule tests if the current {@link State} object fulfills it criteria. If
 * successful (i.e. state fulfills the rule), then the state is processed and a
 * {@link Node} (that represents the processed state) is returned. If
 * unsuccessful (i.e. state does not fulfills this rule), then the state remains
 * unchanged and <code>null</code> is returned.
 * <p>
 * This visitor is meant for one-time use, only. As such, it should be used to
 * parse trees more than once.
 *
 * @author Claas Ahlrichs
 *
 */
public class Parser implements RuleVisitor {

	private final State state;
	private Node result;
	private final Map<Rule, Integer> visitedPath = new HashMap<>();

	/**
	 * Constructs a new {@link Parser} with the specified parameter.
	 * 
	 * @param state
	 *            the state
	 */
	public Parser(State state) {
		this.state = state;
	}

	/**
	 * Returns the result.
	 * 
	 * @return the result
	 */
	public Node getResult() {
		return this.result;
	}

	/**
	 * Sets the result.
	 * 
	 * @param result
	 *            the result
	 */
	private void setResult(Node result) {
		this.result = result;
	}

	/**
	 * Clears the result.
	 */
	private void clearResult() {
		this.result = null;
	}

	@Override
	public void visitConjunction(Conjunction rule) {
		if (addToPath(rule)) {
			this.state.beginGroup();
			try {
				Node node = rule.hasChildren() ? new IntermediateNode() : null;
				for (Rule child : rule) {
					child.visit(this);
					if (getResult() == null) {
						this.state.revert();
						clearResult();
						return;
					}
					if (node != null)
						node.addChild(getResult());
				}
				setResult(node);
			} finally {
				this.state.endGroup();
				removeFromPath(rule);
			}
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		if (addToPath(rule)) {
			this.state.beginGroup();
			try {
				// search for "greediest" rule (i.e. the rule that processes
				// most of the unprocessed data)
				int alreadyProcessedData = this.state.getProcessedData().length();
				Rule bestRule = null;
				for (Rule child : rule) {
					this.state.beginGroup();
					try {
						child.visit(this);
						if (getResult() != null) {
							int newlyProcessedData = this.state.getProcessedData().length();
							if (newlyProcessedData >= alreadyProcessedData) {
								alreadyProcessedData = newlyProcessedData;
								bestRule = child;
							}
						}
					} finally {
						this.state.revert();
						this.state.endGroup();
					}
				}

				// re-process the greediest rule with the "global" state object
				// (i.e. not with the local copies)
				if (bestRule != null) {
					bestRule.visit(this);
					if (getResult() != null) {
						Node node = new IntermediateNode();
						node.addChild(getResult());
						setResult(node);
					} else {
						clearResult();
					}
				} else {
					this.state.revert();
					clearResult();
				}
			} finally {
				this.state.endGroup();
				removeFromPath(rule);
			}
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		if (addToPath(rule)) {
			rule.getRule().visit(this);
			if (getResult() != null) {
				Node node = new NonTerminalNode(rule.getName());
				node.addChild(getResult());
				setResult(node);
			} else {
				clearResult();
			}
			removeFromPath(rule);
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitOptional(Optional rule) {
		if (addToPath(rule)) {
			this.state.beginGroup();
			try {
				Node node = new IntermediateNode();
				rule.getRule().visit(this);
				if (getResult() != null)
					node.addChild(getResult());
				setResult(node);
			} finally {
				this.state.endGroup();
				removeFromPath(rule);
			}
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		if (addToPath(rule)) {
			this.state.beginGroup();
			try {
				Node node = new IntermediateNode();
				for (int repetitions = 1; repetitions <= rule.getMaximumNumberOfRepetions(); repetitions++) {
					rule.getRule().visit(this);
					if (getResult() == null) {
						if (repetitions <= rule.getMinimumNumberOfRepetions()) {
							this.state.revert();
							clearResult();
							return;
						}

						setResult(node);
						return;
					}
					node.addChild(getResult());
				}
				setResult(node);
			} finally {
				this.state.endGroup();
				removeFromPath(rule);
			}
		} else {
			throw new CyclicRuleException(rule);
		}
	}

	@Override
	public void visitTerminal(Terminal rule) {
		this.state.beginGroup();
		try {
			Iterator<String> terminals = rule.getTerminals();
			while (terminals.hasNext()) {
				String terminal = terminals.next();
				if (this.state.process(terminal)) {
					setResult(new TerminalNode(terminal));
					return;
				}
			}
			clearResult();
		} finally {
			this.state.endGroup();
		}
	}

	/**
	 * A helper function that adds the specified rule to the path of visited
	 * rules (i.e. path from the root of the tree to the specified rule). The
	 * primary rational behind this function is to test for "pointless" cycles
	 * within the tree. Cycles are acceptable as long as they have an effect on
	 * the processed data (i.e. data is still being processed).
	 * 
	 * @param rule
	 *            the rule to add to the path
	 * @return <code>true</code> if the specified rule was added to the path of
	 *         visited rules, otherwise <code>false</code>
	 */
	private boolean addToPath(Rule rule) {
		int currentlyProcessed = this.state.getProcessedData().length();
		Integer previouslyProcessed = this.visitedPath.put(rule, new Integer(currentlyProcessed));
		if (previouslyProcessed != null) {
			return currentlyProcessed > previouslyProcessed.intValue();
		}
		return true;
	}

	/**
	 * A helper function that removes the specified rule from the path of
	 * visited rules.
	 * 
	 * @param rule
	 *            the rule to remove from the path
	 */
	private void removeFromPath(Rule rule) {
		this.visitedPath.remove(rule);
	}

	/**
	 * A convenience function that parses the specified state with the specified
	 * rule.
	 * 
	 * @param state
	 *            the state
	 * @param rule
	 *            the rule
	 * @return the result
	 */
	public static Node parse(State state, Rule rule) {
		Parser parser = new Parser(state);
		rule.visit(parser);
		return parser.getResult();
	}

}
