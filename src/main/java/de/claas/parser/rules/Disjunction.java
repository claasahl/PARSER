package de.claas.parser.rules;

import de.claas.parser.Node;
import de.claas.parser.Result;
import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.State;
import de.claas.parser.results.IntermediateNode;

/**
 * 
 * The class {@link Disjunction}. It is an implementation of the {@link Rule}
 * class. It is intended to represent a disjunction of rules within a grammar.
 * <p>
 * This rule will successfully process a given state as long as any child can
 * successfully be processed.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Disjunction extends Rule {

	/**
	 * Constructs a new {@link Disjunction} with the specified parameters.
	 * 
	 * @param children
	 *            the children
	 */
	public Disjunction(Rule... children) {
		super(children);
	}

	@Override
	public Node process(State state) {
		state.beginGroup();
		try {
			// search for "greediest" rule (i.e. the rule that processes most of
			// the unprocessed data)
			int alreadyProcessedData = state.getProcessedData().length();
			Rule bestRule = null;
			for (Rule rule : this) {
				State clonedState = new State(state);
				Node node = new IntermediateNode();
				Node result = Result.get(rule, clonedState, node, null);
				if (result != null) {
					int newlyProcessedData = clonedState.getProcessedData().length();
					if (newlyProcessedData >= alreadyProcessedData) {
						alreadyProcessedData = newlyProcessedData;
						bestRule = rule;
					}
				}
			}

			// re-process the greediest rule with the "global" state object
			// (i.e. not with the local copies)
			if (bestRule != null) {
				Node node = new IntermediateNode();
				return Result.get(bestRule, state, node, null);
			} else {
				state.revert();
				return null;
			}
		} finally {
			state.endGroup();
		}
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitDisjunction(this);
	}

}