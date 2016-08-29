package de.claas.parser;

public class Result {

	private Result() {
	}

	public static Node get(Rule rule, State state, Node onSuccess, Node onFailure) {
		if (rule != null) {
			Node result = rule.process(state);
			if (result != null) {
				onSuccess.addChild(result);
				return onSuccess;
			} else {
				return onFailure;
			}
		}
		return null;
	}

}
