package org.cluster.membership.net;

import java.util.List;
import java.util.TreeSet;

import org.cluster.membership.Config;
import org.cluster.membership.core.ClusterView;
import org.cluster.membership.core.MessageCategory;
import org.cluster.membership.core.MessageType;
import org.cluster.membership.model.Message;
import org.cluster.membership.model.MessageResponse;
import org.cluster.membership.model.Node;
import org.cluster.membership.model.ResponseDescription;
import org.cluster.membership.net.core.MembershipClientHandler;
import org.cluster.membership.structures.ValuePriorityEntry;
import org.cluster.membership.util.MathOp;
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
		
		for(MessageResponse<?> mr : response.getReponses()) {

			Message message = mr.getMessage();
			
			switch (message.getType()) {
				case ADD_TO_CLUSTER: /*Nothing to do*/ 
				case KEEP_ALIVE: /*Nothing to do*/
				case PROBE:  clusterView.removeFailing(message.getNode());				
				case REMOVE_FROM_CLUSTER:  /*Nothing to do*/ 
				case SUBSCRIPTION: clusterView.updateMyView((ClusterView)mr.getResponse());				
				case SUSPECT_DEAD: /*Nothing to do*/  
				case UNSUBSCRIPTION:  /*Nothing to do*/
				case UPDATE: handleUpdateResponse(mr);				
			}
		}
		
	}
	
	private void handleUpdateResponse(MessageResponse<?> response) {
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