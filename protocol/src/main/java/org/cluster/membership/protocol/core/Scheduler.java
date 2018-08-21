package org.cluster.membership.protocol.core;

import java.util.List;

import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.model.Node;
import org.cluster.membership.protocol.net.RequestReceiver;
import org.cluster.membership.protocol.net.ResponseHandler;
import org.cluster.membership.protocol.net.core.MembershipClient;
import org.cluster.membership.protocol.net.core.MembershipClientHandler;
import org.cluster.membership.protocol.net.core.MembershipDirectClientHandler;
import org.cluster.membership.protocol.structures.ValuePriorityEntry;
import org.cluster.membership.protocol.util.MathOp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
	
	@Autowired
	private ClusterView clusterView;
	
	@Autowired
	private RandomService randomService;
	
	@Autowired
	private RequestReceiver membershipServer;
	
	@Autowired
	private ResponseHandler responseReceiver;
	
	@Scheduled(fixedRateString = "${iteration-interval-ms}")
	public void performIteration() {
		long now = System.currentTimeMillis();
		
		/**Selecting a random node from node List, best effort to be not suspected of failing nodes*/
		Node node = randomService.getRandom(clusterView);
		
		long expectedIterations = (long)MathOp.log2n(clusterView.getClusterSize());
		boolean needsUpdate = now - membershipServer.getLastMessage() > expectedIterations * Config.ITERATION_INTERVAL_MS; 
		
		/**Send the pending rumors to a random node*/
		if(node != null) {
			
			List<Message> messages = clusterView.getPendingRumors();
									
			if(needsUpdate) messages.add(new Message(MessageType.UPDATE, Config.THIS_PEER, 0, clusterView.lastRumorTime()));			
			if(messages.size() == 0) messages.add(new Message(MessageType.PROBE, node, 0));
			ValuePriorityEntry<Node, Long> firstFailed = clusterView.pollFailed();
			if(firstFailed != null) {
				messages.add(new Message(MessageType.PROBE, firstFailed.getKey(),0));
				clusterView.addFailed(new ValuePriorityEntry<Node, Long>(firstFailed.getKey(), now));
			}
			
			MembershipClientHandler handler = new MembershipDirectClientHandler(responseReceiver, node, messages);		
			MembershipClient.connect(node, handler);
			
		}
		
	}

}
