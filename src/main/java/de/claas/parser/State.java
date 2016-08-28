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

	private final Stack<Integer> steps;
	private final String data;
	private int offset = 0;

	/**
	 * Constructs a new State with the specified parameter.
	 * 
	 * @param data
	 *            the data
	 */
	public State(String data) {
		this.steps = new Stack<>();
		this.data = data;
	}

	/**
	 * Returns <code>true</code> if the specified token was successfully
	 * processed. Otherwise, <code>false</code> is returned.
	 * 
	 * @param token
	 *            the token
	 * @return <code>true</code> if the specified token was successfully
	 *         processed and <code>false</code> otherwise
	 */
	public boolean process(String token) {
		if (data.startsWith(token, offset)) {
			offset += token.length();
			if (!steps.isEmpty())
				steps.push(steps.pop() + token.length());

			return true;
		} else {
			return false;
		}
	}

	public String getUnprocessedData() {
		return data.substring(offset);
	}

	public String getProcessedData() {
		return data.substring(0, offset);
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
	 * Reverts the changes of the current processing group. This method marks
	 * the tokens, that were processed as part of the current processing group,
	 * as unprocessed and thus reverting the state to the beginning of the
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
		return getUnprocessedData().isEmpty();
	}

}
