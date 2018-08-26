package org.cluster.membership.protocol.core;

import java.util.List;
import java.util.logging.Logger;

import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.model.ClusterData;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.model.Node;
import org.cluster.membership.protocol.model.SynchronTypeWrapper;
import org.cluster.membership.protocol.net.RequestReceiver;
import org.cluster.membership.protocol.net.ResponseHandler;
import org.cluster.membership.protocol.net.RestClient;
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

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private ClusterView clusterView;

	@Autowired
	private RandomService randomService;

	@Autowired
	private RequestReceiver requestReceiver;

	@Autowired
	private ResponseHandler responseReceiver;
	
	@Autowired
	private RestClient restClient;
	

	@Scheduled(fixedRateString = "${iteration.interval.ms}")
	public void performIteration() {
		if(Global.isSynchronizing()) return;
		
		long now = System.currentTimeMillis();

		/**Selecting a random node from node List, best effort to be not suspected of failing nodes*/
		Node node = randomService.getRandom(clusterView);

		long expectedIterations = (long)MathOp.log2n(clusterView.getClusterSize());
		
		long iddleTime = now - requestReceiver.getLastMessage();
		
		boolean needsUpdate = iddleTime > (expectedIterations * Config.ITERATION_INTERVAL_MS * Config.READ_IDDLE_ITERATIONS_FACTOR) ||
				iddleTime > Config.FAILING_NODE_EXPIRATION_TIME_MS; 

		/**Send the pending rumors to a random node*/
	
		//logger.info("shedule iteration, node: " + node + " needs update: " + needsUpdate);
				
		if(node != null) {
			
			if(needsUpdate) {
				logger.info("node " + node + " selected for synchronize this state");
				Global.setSynchronizing(true);
				requestReceiver.setLastMessage(now);
				
				boolean expired = (now - requestReceiver.getLastMessage()) > Config.FAILING_NODE_EXPIRATION_TIME_MS;

				if(expired) {
					ClusterData subscription = restClient.subscribe(node, Config.THIS_PEER);
					if(subscription != null) {
						clusterView.updateMyView(subscription);
					}					
				} else {
					SynchronTypeWrapper syncData = restClient.synchronize(node, Config.THIS_PEER, clusterView.lastRumorTime());
					if(syncData != null) {
						if(syncData.getClusterData() != null) 
							clusterView.updateMyView(syncData.getClusterData());						
						else {
							List<Message> messages = syncData.getMessages();
							clusterView.updateMyView(messages);						
						}
					}
				}
				Global.setSynchronizing(false);

			} else {
				logger.info("node " + node + " selected for send messages and probe");
				List<Message> messages = clusterView.getPendingRumors();

				if(messages.size() == 0) messages.add(new Message(MessageType.PROBE, node, 0));
				ValuePriorityEntry<Node, Long> firstFailed = clusterView.pollFailed();
				if(firstFailed != null) {
					messages.add(new Message(MessageType.PROBE, firstFailed.getKey(),0));
					clusterView.addFailed(new ValuePriorityEntry<Node, Long>(firstFailed.getKey(), now));
				}

				MembershipClientHandler handler = new MembershipDirectClientHandler(responseReceiver, node, messages);		
				MembershipClient.connect(node, handler);
			}
		} else {
			ValuePriorityEntry<Node, Long> firstFailed = clusterView.pollFailed();
			if(firstFailed == null) return;
			long expirTime = now + Config.FAILING_NODE_EXPIRATION_TIME_MS;
			int iterations = MathOp.log2n(clusterView.getClusterSize());
			Message sm  = new Message(MessageType.SUSPECT_DEAD, firstFailed.getKey(), 
					iterations, expirTime);
			clusterView.suspect(expirTime, sm);
		}
		
		

	}

}
