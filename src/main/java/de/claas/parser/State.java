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

	private final Stack<String> tokens;
	private final Stack<String> processedTokens;
	private final Stack<Integer> steps;

	/**
	 * Creates an instance with default parameters.
	 */
	public State() {
		this(new Stack<>());
	}

	/**
	 * Creates an instance with the given parameter.
	 * 
	 * @param tokens
	 *            the tokens that need processing / parsing
	 */
	public State(Stack<String> tokens) {
		// TODO no null values among tokens
		this.tokens = new Stack<>();
		this.tokens.addAll(tokens);
		this.processedTokens = new Stack<>();
		this.steps = new Stack<>();
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
		if (tokens.isEmpty())
			return null;

		String token = tokens.pop();
		processedTokens.push(token);
		if (!steps.isEmpty())
			steps.push(steps.pop() + 1);
		return token;
	}

	/**
	 * Returns the token that was most recently processed and marks it as
	 * <i>unprocessed</i>. The returned token is taken from a stack of
	 * (processed) tokens and transferred back to a stack of unprocessed tokens.
	 * 
	 * @return the token that was most recently processed
	 */
	public String unprocessToken() {
		String token = processedTokens.pop();
		tokens.push(token);
		if (!steps.isEmpty())
			steps.push(steps.pop() - 1);
		return token;
	}

	/**
	 * Returns the (current) number of unprocessed tokens.
	 * 
	 * @return the (current) number of unprocessed tokens
	 */
	public int getUnprocessedTokens() {
		return tokens.size();
	}

	/**
	 * Returns the (current) number of processed tokens.
	 * 
	 * @return the (current) number of processed tokens
	 */
	public int getProcessedTokens() {
		return processedTokens.size();
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
	 * <p/>
	 * This method will not end a group. Use {@link #endGroup()} for closing /
	 * ending groups.
	 */
	public void revert() {
		for (int i = steps.pop(); i > 0; i--) {
			String tmp = processedTokens.pop();
			tokens.push(tmp);
		}
		steps.push(0);
	}

}
