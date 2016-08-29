package de.claas.parser;

import java.util.Stack;

/**
 * 
 * The class {@link State}. It is intended to be used by {@link Grammar}
 * instances during processing / parsing. The state is fed with data, that is
 * presumed to fulfill the grammar in question, and provides methods for
 * processing the given data as well as methods for querying its internal state.
 * 
 * @author Claas Ahlrichs
 *
 */
public class State {

	private final Stack<Integer> steps;
	private final String data;
	private int offset = 0;

	/**
	 * Constructs a new {@link State} with the specified parameter.
	 * 
	 * @param data
	 *            the data that will be processed by this {@link State}
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

	/**
	 * Returns the unprocessed data of this state. An empty string is returned
	 * if all data were processed. Equals {@link #getData()} if no data was
	 * processed. The returned string is the trailing part of this state's data
	 * (i.e. this state's data ends with the unprocessed data). The length of
	 * the returned string decreases as more data is being processed.
	 * 
	 * @return the unprocessed data of this state
	 */
	public String getUnprocessedData() {
		return data.substring(offset);
	}

	/**
	 * Returns the processed data of this state. An empty string is returned if
	 * no data was processed. Equals {@link #getData()} if all data were
	 * processed. The returned string is the leading part of this state's data
	 * (i.e. this state's data starts with the processed data). The length of
	 * the returned string increases as more data is being processed.
	 * 
	 * @return the processed data of this state
	 */
	public String getProcessedData() {
		return data.substring(0, offset);
	}

	/**
	 * Returns the data that is being processed by this state. The returned data
	 * corresponds to what was specified during construction. While the data is
	 * being processed (i.e. {@link #hasUnprocessedData()} is
	 * <code>true</code>), the data can be split into two parts: the processed
	 * part and the unprocessed part. See {@link #getProcessedData()} and
	 * {@link #getUnprocessedData()} for more details on their semantics.
	 * 
	 * @return the data that is being processed by this state
	 */
	public String getData() {
		return data;
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

}
