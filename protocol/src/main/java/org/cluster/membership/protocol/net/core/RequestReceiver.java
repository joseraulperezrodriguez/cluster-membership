package org.cluster.membership.protocol.net.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.MathOp;
import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.core.ClusterView;
import org.cluster.membership.protocol.core.MessageType;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.model.MessageResponse;
import org.cluster.membership.protocol.model.RequestDescription;
import org.cluster.membership.protocol.model.ResponseDescription;
import org.cluster.membership.protocol.model.SynchroObject;
import org.cluster.membership.protocol.net.MembershipClient;
import org.cluster.membership.protocol.net.channel.handler.MembershipIndirectClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;

@Component
public class RequestReceiver {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private ResponseHandler responseHandler;
	
	@Autowired
	private Config config;
	
	@Autowired
	private MembershipClient client; 
	
	@Autowired
	private ClusterView clusterView;
	
	public ResponseDescription receive(RequestDescription requestDescription, ChannelHandlerContext ctx) {
		Node from = requestDescription.getNode();		
		List<Message> messages = requestDescription.getData();
		
		List<MessageResponse<?>> mResponses = new ArrayList<MessageResponse<?>>();
						
		if(clusterView.isFailing(from)) clusterView.removeFailing(from);
		
		if(clusterView.isSuspectedDead(from)) {
			Message keepAlive = new Message(MessageType.KEEP_ALIVE, from, MathOp.log2n(clusterView.getClusterSize()));
			clusterView.keepAlive(keepAlive);
		}
		
		logger.info("Messages received from " + from);
		
		for(Message m : messages) {
			Message adjustedM = m.adjustIteration(clusterView.getClusterSize());
			MessageType mt = m.getType();
		
            /**If this node has already registered m as a rumor, then avoid to reinserted, except the SUSPECT_DEAD messages, as you can see later*/
			if(clusterView.containsRumor(m) && !mt.equals(MessageType.SUSPECT_DEAD)) { 
				logger.info("Avoiding handling " + m + " message");
				continue;			
			}
			
			switch (mt) {
				case UNSUBSCRIPTION: {
					handlerUnsubscription(adjustedM);
					break;
				}
				case PROBE: {
					MessageResponse<Boolean> mr = handlerProbe(adjustedM, ctx);
					if(mr != null) mResponses.add(mr);
					break;
				}
				/**If message is SUSPECT_DEAD we insert again to allow the minimal time will
				 * be prioritized, in this way we ensure all the nodes have the same 
				 * expiration time for a node*/
				case SUSPECT_DEAD: {
					handlerSuspectDead(adjustedM);
					break;
				}
				case KEEP_ALIVE: {
					handlerKeepAlive(adjustedM);
					break;
				}
				case ADD_TO_CLUSTER: {
					handlerAddToCluster(adjustedM);
					break;
				}
				case REMOVE_FROM_CLUSTER: {
					handlerRemoveFromCluster(adjustedM);
					break;
				}
			}
		}
		
		clusterView.updateFrameMessageCount();
		
		SynchroObject syncData = clusterView.getSyncObject(requestDescription.getNode(), requestDescription.getFrameMessCount());
		
		return new ResponseDescription(syncData, mResponses);
		
	}

	public void handlerUnsubscription(Message m) {
		Message rem = new Message(MessageType.REMOVE_FROM_CLUSTER, m.getNode(), MathOp.log2n(clusterView.getClusterSize()));
		clusterView.removeFromCluster(rem);
	}

	public MessageResponse<Boolean> handlerProbe(Message m, ChannelHandlerContext ctx) {
		if(m.getNode().equals(config.getThisPeer())) {
			MessageResponse<Boolean> response = new MessageResponse<Boolean>(true, m);
			return response;
		} else {
			client.connect(m.getNode(),
					new MembershipIndirectClientHandler(clusterView.getFrameMessageCount(), config.getThisPeer(), m.getNode(),m, responseHandler,ctx));
			return null;
		}
	}

	public void handlerSuspectDead(Message m) {
		long time = (Long)m.getData();
		clusterView.suspect(time, m);
	}

	public void handlerKeepAlive(Message m) {
		clusterView.keepAlive(m);
	}

	public void handlerAddToCluster(Message m) {
		clusterView.addToCluster(m);
	}

	public void handlerRemoveFromCluster(Message m) {
		clusterView.removeFromCluster(m);
	}

	public ClusterView getClusterView() {
		return clusterView;
	}

}
