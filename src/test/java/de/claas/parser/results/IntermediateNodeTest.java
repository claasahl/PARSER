package de.claas.parser.results;

import de.claas.parser.Node;
import de.claas.parser.NodeTest;

/**
 * The JUnit test for class {@link IntermediateNode}. It is intended to collect
 * and document a set of test cases for the tested class. Please refer to the
 * individual tests for more detailed information.
 *
 * @author Claas Ahlrichs
 */
public class IntermediateNodeTest extends NodeTest {

	@Override
	protected Node build(Node... children) {
		IntermediateNode node = new IntermediateNode();
		for (Node child : children) {
			node.addChild(child);
		}
		return node;
	}

}
