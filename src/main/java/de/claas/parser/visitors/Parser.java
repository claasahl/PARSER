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

public class Parser implements RuleVisitor {

	private final State state;
	private Node result;
	private final Map<Rule, Integer> visitedPath = new HashMap<>();

	public Parser(State state) {
		this.state = state;
	}

	public Node getResult() {
		return this.result;
	}

	private void setResult(Node result) {
		this.result = result;
	}

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
					if (process(child, node, null) == null) {
						this.state.revert();
						clearResult();
						return;
					}
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
				// most of
				// the unprocessed data)
				int alreadyProcessedData = this.state.getProcessedData().length();
				Rule bestRule = null;
				for (Rule child : rule) {
					this.state.beginGroup();
					try {
						Node node = new IntermediateNode();
						if (process(child, node, null) != null) {
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
					Node node = new IntermediateNode();
					setResult(process(bestRule, node, null));
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
			Node node = new NonTerminalNode(rule.getName());
			setResult(process(rule.getRule(), node, null));
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
				setResult(process(rule.getRule(), node, node));
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
					if (process(rule.getRule(), node, null) == null) {
						if (repetitions <= rule.getMinimumNumberOfRepetions()) {
							this.state.revert();
							clearResult();
							return;
						}

						setResult(node);
						return;
					}
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

	private boolean addToPath(Rule rule) {
		int currentlyProcessed = this.state.getProcessedData().length();
		Integer previouslyProcessed = this.visitedPath.put(rule, new Integer(currentlyProcessed));
		if(previouslyProcessed != null) {
			return currentlyProcessed > previouslyProcessed.intValue();
		}
		return true;
	}

	private void removeFromPath(Rule rule) {
		this.visitedPath.remove(rule);
	}

	public static Node parse(State state, Rule rule) {
		Parser parser = new Parser(state);
		rule.visit(parser);
		return parser.getResult();
	}

	private Node process(Rule rule, Node onSuccess, Node onFailure) {
		if (rule != null) {
			rule.visit(this);
			if (getResult() != null) {
				onSuccess.addChild(getResult());
				return onSuccess;
			}
			return onFailure;
		}
		return null;
	}

}
