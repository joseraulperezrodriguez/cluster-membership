package org.cluster.membership.protocol.net;

import java.io.Serializable;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.core.ClusterView;
import org.cluster.membership.protocol.core.Global;
import org.cluster.membership.protocol.core.MessageCategory;
import org.cluster.membership.protocol.core.MessageType;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.model.MessageResponse;
import org.cluster.membership.protocol.model.Node;
import org.cluster.membership.protocol.model.ResponseDescription;
import org.cluster.membership.protocol.net.core.MembershipClientHandler;
import org.cluster.membership.protocol.structures.ValuePriorityEntry;
import org.cluster.membership.protocol.util.MathOp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResponseHandler {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private ClusterView clusterView;
		
	public void addToFailed(Node node) {
		clusterView.addFailed(new ValuePriorityEntry<Node, Long>(node, System.currentTimeMillis()));
	}
		
	public void restoreMessages(List<Message> messages) {
		String messageRestored = "";		
		for(Message m: messages) {
			if(!m.getCategory().equals(MessageCategory.CLUSTER)) continue;
			messageRestored += m + "\n";
			m.setIterations(m.getIterations()+1);
			clusterView.addRumor(m);
		}
		logger.info("Restoring messages: \n" + messageRestored);
		
	}

	public void suspectAll(TreeSet<Message> indirectMessages) {
		long now = System.currentTimeMillis();
		int iterations = MathOp.log2n(clusterView.getClusterSize());
		String messageSusp = "";
		for(Message m : indirectMessages) {
			long expirTime = now + Config.FAILING_NODE_EXPIRATION_TIME_MS;
			Message sm  = new Message(MessageType.SUSPECT_DEAD, m.getNode(), 
					iterations, expirTime);
			clusterView.suspect(expirTime, sm);
		}
		logger.info("Supecting messages: \n" + messageSusp);
	}

	public void receive(ResponseDescription response, MembershipClientHandler membershipClientHandler) {

		Node from = membershipClientHandler.getTo();
		clusterView.removeFailing(from);
		
		logger.info("Response received from: " + from);
		
		for(MessageResponse<? extends Serializable> mr : response.getReponses()) {

			Message message = mr.getMessage();
			
			switch (message.getType()) {
				case ADD_TO_CLUSTER: break; 
				case KEEP_ALIVE: break;
				case PROBE:  clusterView.removeFailing(message.getNode()); break;				
				case REMOVE_FROM_CLUSTER:  break;
				case SUSPECT_DEAD: break;  
				case UNSUBSCRIPTION: handleUnsubscription(); break;
			}
		}
		
	}
		
	private void handleUnsubscription() {
		Global.shutdown(10);
	}
	
}