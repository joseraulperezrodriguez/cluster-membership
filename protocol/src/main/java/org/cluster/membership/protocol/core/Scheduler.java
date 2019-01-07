package org.cluster.membership.protocol.core;

import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.DateTime;
import org.cluster.membership.common.model.util.MathOp;
import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.net.ResponseHandler;
import org.cluster.membership.protocol.net.core.MembershipClient;
import org.cluster.membership.protocol.net.core.MembershipClientHandler;
import org.cluster.membership.protocol.net.core.MembershipDirectClientHandler;
import org.cluster.membership.protocol.structures.ValuePriorityEntry;
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
	private ResponseHandler responseReceiver;
	
	@Autowired
	private Config config;
	
	@Scheduled(fixedRateString = "${iteration.interval.ms}")
	public synchronized void  performIteration() {

		long now = System.currentTimeMillis();

		/**Selecting a random node from node List, best effort to be not suspected of failing nodes*/
		Node node = randomService.getRandom(clusterView);

		TimeZone currentTimeZone = config.getThisPeer().getTimeZone();

		/**Send the pending rumors to a random node*/
		if(node != null) {

			logger.info("node " + node + " selected for send messages and probe");
			List<Message> messages = clusterView.getPendingRumors();

			if(messages.size() == 0) messages.add(new Message(MessageType.PROBE, node, 0, currentTimeZone));
			ValuePriorityEntry<Node, Long> firstFailed = clusterView.pollFailed();
			if(firstFailed != null) {
				messages.add(new Message(MessageType.PROBE, firstFailed.getKey(),0, currentTimeZone));
				clusterView.addFailed(new ValuePriorityEntry<Node, Long>(firstFailed.getKey(), 
						DateTime.utcTime(now, config.getThisPeer().getTimeZone())));
			}

			MembershipClientHandler handler = new MembershipDirectClientHandler(clusterView.getFrameMessageCount(), responseReceiver, config.getThisPeer(), node, messages);		
			MembershipClient.connect(node, handler, config);
		} else {
			ValuePriorityEntry<Node, Long> firstFailed = clusterView.pollFailed();
			if(firstFailed == null) return;
			long expirTime = DateTime.utcTime(now, config.getThisPeer().getTimeZone()) + config.getFailingNodeExpirationTimeMs();
			int iterations = MathOp.log2n(clusterView.getClusterSize());
			Message sm  = new Message(MessageType.SUSPECT_DEAD, firstFailed.getKey(), 
					iterations, currentTimeZone, expirTime);
			clusterView.suspect(expirTime, sm);
		}



	}

}
