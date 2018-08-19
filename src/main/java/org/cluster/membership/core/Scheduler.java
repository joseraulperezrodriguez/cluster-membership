package org.cluster.membership.core;

import java.util.List;

import org.cluster.membership.Config;
import org.cluster.membership.model.Message;
import org.cluster.membership.model.Node;
import org.cluster.membership.net.RequestReceiver;
import org.cluster.membership.net.ResponseHandler;
import org.cluster.membership.net.core.MembershipClient;
import org.cluster.membership.net.core.MembershipClientHandler;
import org.cluster.membership.net.core.MembershipDirectClientHandler;
import org.cluster.membership.structures.ValuePriorityEntry;
import org.cluster.membership.util.MathOp;

public class Scheduler {
	
	private ClusterView clusterView;
	
	private RandomService randomService;
	
	private RequestReceiver membershipServer;
	
	private ResponseHandler responseReceiver;
	
	public void performIteration() {
		
		long now = System.currentTimeMillis();
		
		/**Selecting a random node from node List, best effort to be not suspected of failing nodes*/
		Node node = randomService.getRandom(clusterView);
		
		long expectedIterations = (long)MathOp.log2n(clusterView.getClusterSize());
		boolean needsUpdate = now - membershipServer.getLastMessage() > expectedIterations * Config.ITERATION_INTERVAL_MS; 
		
		/**Send the pending rumors to a random node*/
		if(node != null) {
			
			List<Message> messages = clusterView.getPendingRumors();
									
			if(needsUpdate) messages.add(new Message(MessageType.UPDATE, Config.thisPeer(), 0, clusterView.lastRumorTime()));			
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
