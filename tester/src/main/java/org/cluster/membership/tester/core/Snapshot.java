package org.cluster.membership.tester.core;

import java.util.ArrayList;
import java.util.List;

import org.cluster.membership.common.debug.StateInfo;
import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.Tuple2;

public class Snapshot {
	
	private List<StateInfo> expected;
	
	private List<Tuple2<Node,StateInfo>> result;
	
	public Snapshot() {
		this.expected = new ArrayList<StateInfo>();
		this.result = new ArrayList<Tuple2<Node, StateInfo>>();
	}
	
	public void addExpected(StateInfo stateInfo) {
		expected.add(stateInfo);
	}
	
	public void addResult(Node node, StateInfo stateInfo) {
		result.add(new Tuple2<>(node, stateInfo));
	}

	public List<StateInfo> getExpected() {
		return expected;
	}

	public List<Tuple2<Node, StateInfo>> getResult() {
		return result;
	}

	

}
