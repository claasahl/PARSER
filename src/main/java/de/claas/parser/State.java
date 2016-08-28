package de.claas.parser;

import java.util.Stack;

/**
 * 
 * The class {@link State}. It is intended to represent the state while
 * processing / parsing tokens.
 * 
 * @author Claas Ahlrichs
 *
 */
public class State {

	private final String pattern;
	private int offset = 0;
	private final Stack<Integer> steps;

	public State(String pattern) {
		this.steps = new Stack<>();
		this.pattern = pattern;
	}

	/**
	 * Returns the next token and marks it as <i>processed</i>. The returned
	 * token is taken from a stack of (unprocessed) tokens and transferred to a
	 * stack of processed tokens. If no more tokens are available, then
	 * <code>null</code> is returned.
	 * 
	 * @return the next token. <code>null</code> if no more tokens are available
	 */
	public String processToken() {
		return null;
	}

	public boolean process(String token) {
		if (getUnprocessedPattern().startsWith(token)) {
			offset += token.length();
			if (!steps.isEmpty())
				steps.push(steps.pop() + token.length());

			return true;
		} else {
			return false;
		}
	}

	public String getUnprocessedPattern() {
		return pattern.substring(offset);
	}

	public String getProcessedPattern() {
		return pattern.substring(0, offset);
	}

	/**
	 * Signals the beginning of a processing group. Tokens that were processed
	 * after calling this method can be reverted with {@link #revert()}.
	 * Furthermore, processing groups need to be properly ended by calling
	 * {@link #endGroup()}.
	 */
	public void beginGroup() {
		steps.push(0);
	}

	/**
	 * Signals the end of a processing group. This will effectively merge the
	 * current processing group with the previous one. Thus calling
	 * {@link #revert()} will not only revert the processing that was just
	 * closed, but the previous one as well.
	 */
	public void endGroup() {
		if (steps.size() >= 2) {
			int sum = steps.pop() + steps.pop();
			steps.push(sum);
		} else if (!steps.isEmpty()) {
			steps.pop();
		}
	}

	/**
	 * Returns the (current) number of processing groups.
	 * 
	 * @return the (current) number of processing groups
	 */
	public int getGroups() {
		return steps.size();
	}

	/**
	 * Reverts the changes of the current processing group. This method
	 * transfers the number of tokens, that were processed as part of the
	 * current processing group, from the processed tokens back to the
	 * (remaining) tokens. Thus reverting the state to the beginning of the
	 * processing group.
	 * <p>
	 * This method will not end a group. Use {@link #endGroup()} for closing /
	 * ending groups.
	 */
	public void revert() {
		offset -= steps.pop();
		steps.push(0);
	}

	public boolean isFullyProcessed() {
		return getUnprocessedPattern().isEmpty();
	}

}
