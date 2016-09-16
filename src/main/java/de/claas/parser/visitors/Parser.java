package de.claas.parser.visitors;

import java.util.Iterator;

import de.claas.parser.Node;
import de.claas.parser.Result;
import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.State;
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
		this.state.beginGroup();
		try {
			Node node = rule.hasChildren() ? new IntermediateNode() : null;
			setResult(node);
			for (Rule child : rule) {
				if (Result.get(child, this.state, node, null) == null) {
					this.state.revert();
					clearResult();
					return;
				}
			}
		} finally {
			this.state.endGroup();
		}
	}

	@Override
	public void visitDisjunction(Disjunction rule) {
		this.state.beginGroup();
		try {
			// search for "greediest" rule (i.e. the rule that processes most of
			// the unprocessed data)
			int alreadyProcessedData = this.state.getProcessedData().length();
			Rule bestRule = null;
			for (Rule child : rule) {
				State clonedState = new State(this.state);
				Node node = new IntermediateNode();
				if (Result.get(child, clonedState, node, null) != null) {
					int newlyProcessedData = clonedState.getProcessedData().length();
					if (newlyProcessedData >= alreadyProcessedData) {
						alreadyProcessedData = newlyProcessedData;
						bestRule = child;
					}
				}
			}

			// re-process the greediest rule with the "global" state object
			// (i.e. not with the local copies)
			if (bestRule != null) {
				Node node = new IntermediateNode();
				setResult(Result.get(bestRule, this.state, node, null));
			} else {
				this.state.revert();
				clearResult();
			}
		} finally {
			this.state.endGroup();
		}
	}

	@Override
	public void visitNonTerminal(NonTerminal rule) {
		Node node = new NonTerminalNode(rule.getName());
		setResult(Result.get(rule.getRule(), this.state, node, null));
	}

	@Override
	public void visitOptional(Optional rule) {
		this.state.beginGroup();
		try {
			Node node = new IntermediateNode();
			setResult(Result.get(rule.getRule(), this.state, node, node));
		} finally {
			this.state.endGroup();
		}
	}

	@Override
	public void visitRepetition(Repetition rule) {
		this.state.beginGroup();
		try {
			Node node = new IntermediateNode();
			for (int repetitions = 1; repetitions <= rule.getMaximumNumberOfRepetions(); repetitions++) {
				if (Result.get(rule.getRule(), this.state, node, null) == null) {
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
	
	public static Node parse(State state, Rule rule) {
		Parser parser = new Parser(state);
		rule.visit(parser);
		return parser.getResult();
	}

}
