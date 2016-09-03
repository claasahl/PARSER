package de.claas.parser.rules;

import de.claas.parser.Node;
import de.claas.parser.Result;
import de.claas.parser.Rule;
import de.claas.parser.RuleVisitor;
import de.claas.parser.State;
import de.claas.parser.results.IntermediateNode;

/**
 * 
 * The class {@link Repetition}. It is an implementation of the
 * {@link Decorator} class. It is intended to represent a repeatable rule within
 * a grammar. The minimum and maximum number of repetitions can be limited to
 * any positive integer. The only constraint is the upper limit of integer
 * values (i.e. {@value Integer#MAX_VALUE}).
 * <p>
 * This rules default settings are such that it will successfully process a
 * given state regardless of how often the decorated rule can be processed (i.e.
 * minimum number of repetitions is {@value 0} and maximum number of repetitions
 * is {@value Integer#MAX_VALUE}). Making the decorated rule optional and
 * repeatable at the same time.
 * 
 * @author Claas Ahlrichs
 *
 */
public class Repetition extends Decorator {

	private final int minRepetitions;
	private final int maxRepetitions;

	/**
	 * 
	 * Constructs a new {@link Repetition} with default parameters. Calling this
	 * constructor is equivalent to calling
	 * <code>{@link Repetition#Repetition(Rule, int, int)}</code> with at least
	 * {@value 0} repetitions and at most {@value Integer#MAX_VALUE}.
	 * 
	 * @param rule
	 *            the repeatable rule
	 */
	public Repetition(Rule rule) {
		this(rule, 0, Integer.MAX_VALUE);
	}

	/**
	 * 
	 * Constructs a new {@link Repetition} with the specified parameters.
	 * 
	 * @param rule
	 *            the repeatable rule
	 * @param minRepetitions
	 *            the minimum number of repetitions
	 * @param maxRepetitions
	 *            the maximum number of repetitions
	 */
	public Repetition(Rule rule, int minRepetitions, int maxRepetitions) {
		super(rule);
		this.minRepetitions = minRepetitions;
		this.maxRepetitions = maxRepetitions;
	}

	/**
	 * Returns the minimum number of repetitions. By default, this is set to
	 * {@value 0}.
	 * 
	 * @return the minimum number of repetitions
	 */
	public int getMinimumNumberOfRepetions() {
		return minRepetitions;
	}

	/**
	 * Returns the maximum number of repetitions. By default, this is set to
	 * {@value Integer#MAX_VALUE}.
	 * 
	 * @return the maximum number of repetitions
	 */
	public int getMaximumNumberOfRepetions() {
		return maxRepetitions;
	}

	@Override
	public Node process(State state) {
		state.beginGroup();
		try {
			Node node = new IntermediateNode();
			for (int repetitions = 1; repetitions <= maxRepetitions; repetitions++) {
				if (Result.get(getRule(), state, node, null) == null) {
					if (repetitions < minRepetitions) {
						state.revert();
						return null;
					} else {
						return node;
					}
				}
			}
			return node;
		} finally {
			state.endGroup();
		}
	}

	@Override
	public void visit(RuleVisitor visitor) {
		visitor.visitRepetition(this);
	}

}