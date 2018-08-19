package org.cluster.membership.core;

import java.util.Random;

import org.cluster.membership.Config;
import org.cluster.membership.model.Node;

public class RandomService {
	
	private Random random;
	
	public RandomService() {
		this.random = new Random();
	}
	
	public Node getRandom(ClusterView clusterView) {
		int size = clusterView.getClusterSize() - 1;
		int doubleSize = size * 2;

		if(size == 
				clusterView.getFailedSize() + 
				clusterView.getSuspectedSize()) return null;
		
		int pow = 2;
		int iterations = 0;
		int index = -1;
		while((pow *= 2) < doubleSize || (iterations < size && iterations < Config.MAX_EXPECTED_NODE_LOG_2_SIZE)) {
			iterations++;
			index = random.nextInt(size);
			Node nd = clusterView.getNodeAt(index);
			if(clusterView.isFailing(nd) || 
			   clusterView.isSuspectedDead(nd)) continue;
						
			return nd;			
		}
		
		return null;		
	}

}
