package org.cluster.membership.tester.core;

import java.util.List;

import org.cluster.membership.common.debug.StateInfo;
import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.Tuple2;
import org.cluster.membership.tester.util.Utils;

public class BasicEvaluator implements IEvaluator {

	
	@Override
	public Double evaluate(Snapshot snaphot) {
		int count = 0;

		for(int i = 0; i < snaphot.getExpected().size(); i++) {
			StateInfo cur = snaphot.getExpected().get(i);
			int size = cur.getNodes().size() / 2;
			List<Tuple2<Node, StateInfo>> states = snaphot.getResult();
			
			int nodesEquals = 0;
			for(Tuple2<Node, StateInfo> tup : states) {
				if(Utils.equals(tup.getB().getNodes(), cur.getNodes())) {
					nodesEquals++;
				}
			}
			if(nodesEquals >= size) {
				count++;
			}
			
		}
		
		return (double)count / (double)snaphot.getExpected().size();
	}

}
