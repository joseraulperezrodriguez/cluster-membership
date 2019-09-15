package org.cluster.membership.protocol.core;

import java.util.List;
import java.util.logging.Logger;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.MathOp;
import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.net.ResponseHandler;
import org.cluster.membership.protocol.net.channel.handler.MembershipClientHandler;
import org.cluster.membership.protocol.net.channel.handler.MembershipDirectClientHandler;
import org.cluster.membership.protocol.net.core.MembershipClient;
import org.cluster.membership.protocol.structures.ValuePriorityEntry;
import org.cluster.membership.protocol.time.ServerTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IterationScheduler {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private ClusterView clusterView;

	@Autowired
	private RandomService randomService;

	@Autowired
	private ResponseHandler responseReceiver;
	
	@Autowired
	private Config config;
	
	@Autowired
	private MembershipClient client;
		
	@Scheduled(fixedRateString = "${iteration.interval.ms}")
	public synchronized void  performIteration() {

		/**Selecting a random node from node List, best effort to be not suspected of failing nodes*/
		Node node = randomService.getRandom(clusterView);

		ValuePriorityEntry<Node, Long> firstFailed = clusterView.pollFailed();

		/**Send the pending rumors to a random node*/
		if(node != null) {

			logger.info("node " + node + " selected for send messages and probe");
			List<Message> messages = clusterView.getPendingRumors();

			if(messages.size() == 0) messages.add(new Message(MessageType.PROBE, node, 0));
			if(firstFailed != null) {
				messages.add(new Message(MessageType.PROBE, firstFailed.getKey(),0));
				clusterView.addFailed(new ValuePriorityEntry<Node, Long>(firstFailed.getKey(), ServerTime.getTime()));
			}

			MembershipClientHandler handler = new MembershipDirectClientHandler(clusterView.getFrameMessageCount(), responseReceiver, config.getThisPeer(), node, messages);		
			client.connect(node, handler);
		} else if(firstFailed != null){
			long nowUTC = ServerTime.getTime();
			long expTime = nowUTC + MathOp.expTime(config.getIterationIntervalMs(), clusterView.getClusterSize(), config.getCyclesForWaitKeepAlive());
			int iterations = MathOp.log2n(clusterView.getClusterSize());
			Message sm  = new Message(MessageType.SUSPECT_DEAD, firstFailed.getKey(), iterations, expTime);
			clusterView.suspect(expTime, sm);
		}
		clusterView.removeExpired();
		
	}
	

}
