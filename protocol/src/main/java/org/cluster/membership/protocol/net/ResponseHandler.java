package org.cluster.membership.protocol.net;

import java.io.Serializable;
import java.util.List;
import java.util.TreeSet;

import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.core.ClusterView;
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

	@Autowired
	private ClusterView clusterView;

	public void addToFailed(Node node) {
		clusterView.addFailed(new ValuePriorityEntry<Node, Long>(node, System.currentTimeMillis()));
	}
	
	public void restoreMessages(List<Message> messages) {
		for(Message m: messages) {
			if(!m.getCategory().equals(MessageCategory.CLUSTER)) continue;			
			m.setIterations(m.getIterations()+1);
			clusterView.addRumor(m);
		}
	}

	public void suspectAll(TreeSet<Message> indirectMessages) {
		long now = System.currentTimeMillis();
		int iterations = MathOp.log2n(clusterView.getClusterSize());
		for(Message m : indirectMessages) { 								 
			Message sm  = new Message(MessageType.SUSPECT_DEAD, m.getNode(), 
					iterations, now + Config.FAILING_NODE_EXPIRATION_TIME_MS);
			clusterView.suspect(now, sm);
		}					
	}

	public void receive(ResponseDescription response, MembershipClientHandler membershipClientHandler) {

		Node from = membershipClientHandler.getTo();
		clusterView.removeFailing(from);
		
		for(MessageResponse<? extends Serializable> mr : response.getReponses()) {

			Message message = mr.getMessage();
			
			switch (message.getType()) {
				case ADD_TO_CLUSTER: break; 
				case KEEP_ALIVE: break;
				case PROBE:  clusterView.removeFailing(message.getNode()); break;				
				case REMOVE_FROM_CLUSTER:  break;
				case SUBSCRIPTION: clusterView.updateMyView((ClusterView)mr.getResponse()); break;				
				case SUSPECT_DEAD: break;  
				case UNSUBSCRIPTION:   break;
				case UPDATE: handleUpdateResponse(mr); break;				
			}
		}
		
	}
	
	private void handleUpdateResponse(MessageResponse<? extends Serializable> response) {
		Object ans = response.getResponse();
		
		if(ans == null) return;
		
		if(ans instanceof ClusterView) clusterView.updateMyView((ClusterView)ans);
		else if(ans instanceof List){
			@SuppressWarnings({ "unchecked" })
			List<Message> missingMessages = (List<Message>)ans;			
			clusterView.updateMyView(missingMessages);			
		}
	}
	
	
	
}