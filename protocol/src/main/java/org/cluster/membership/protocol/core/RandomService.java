package org.cluster.membership.protocol.core;

import java.util.Random;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RandomService {
	
	//private Logger logger = Logger.getLogger(RandomService.class.getName());
	
	private Random random;
	
	@Autowired
	private Config config;
	
	public RandomService() {
		this.random = new Random();
	}
	
	public RandomService(Config config) {
		this.config = config;
		this.random = new Random();
	}
	
	public Node getRandom(ClusterView clusterView) {
		int size = clusterView.getClusterSize();
		int doubleSize = size * 2;

		if(size - 1 == 
				clusterView.getFailedSize() + 
				clusterView.getSuspectedSize()) return null;
		
		int pow = 2;
		int iterations = 0;
		int index = -1;
		while((pow *= 2) < doubleSize || (iterations < size && iterations < config.getMaxExpectedNodeLog2Size())) {
			iterations++;
			index = random.nextInt(size);
			Node nd = clusterView.getNodeAt(index);
			if(clusterView.isFailing(nd) || 
			   clusterView.isSuspectedDead(nd) ||
			   nd.equals(config.getThisPeer())) continue;
						
			return nd;			
		}
		
		return null;		
	}

}
