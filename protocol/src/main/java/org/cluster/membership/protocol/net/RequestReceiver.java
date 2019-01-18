package org.cluster.membership.protocol.net;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;

@Component
public class RequestReceiver {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private RequestMessageHandler messageHandler;
	
	@Autowired
	private ClusterView clusterView;
	
	@Autowired
	private Config config;
	
	public ResponseDescription receive(RequestDescription requestDescription, ChannelHandlerContext ctx) {
		Node from = requestDescription.getNode();		
		List<Message> messages = requestDescription.getData();
		
		List<MessageResponse<?>> mResponses = new ArrayList<MessageResponse<?>>();
						
		if(clusterView.isFailing(from)) clusterView.removeFailing(from);
		
		if(clusterView.isSuspectedDead(from)) {
			Message keepAlive = new Message(MessageType.KEEP_ALIVE, from, MathOp.log2n(clusterView.getClusterSize()), config.getThisPeer().getTimeZone());
			clusterView.keepAlive(keepAlive);
		}
		
		logger.info("Messages received from " + from);
		
		for(Message m : messages) {
			MessageType mt = m.getType();
		
			/**If message is SUSPECT_DEAD we insert again to allow the minimal time will
			 * be prioritized, in this way we ensure all the nodes have the same 
			 * expiration time for a node*/
			if(clusterView.isRumor(m) && !mt.equals(MessageType.SUSPECT_DEAD)) { 
				logger.info("Avoiding handling " + m + " message");
				continue;			
			}
			
			switch (mt) {
				case UNSUBSCRIPTION: {
					messageHandler.handlerUnsubscription(m);
					break;
				}
				case PROBE: {
					MessageResponse<Boolean> mr = messageHandler.handlerProbe(m, ctx);
					if(mr != null) mResponses.add(mr);
					break;
				} 
				case SUSPECT_DEAD: {
					messageHandler.handlerSuspectDead(m);
					break;
				}
				case KEEP_ALIVE: {
					messageHandler.handlerKeepAlive(m);
					break;
				}
				case ADD_TO_CLUSTER: {
					messageHandler.handlerAddToCluster(m);
					break;
				}
				case REMOVE_FROM_CLUSTER: {
					messageHandler.handlerRemoveFromCluster(m);
					break;
				} 
			}
			
		}
		
		clusterView.updateFrameMessageCount();
		
		SynchroObject syncData = clusterView.getSyncObject(requestDescription.getNode(), 
				requestDescription.getFrameMessCount());
		
		return new ResponseDescription(syncData, mResponses);
		
	}

	public ClusterView getClusterView() {
		return clusterView;
	}

}
