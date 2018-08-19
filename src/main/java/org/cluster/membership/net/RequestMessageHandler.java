package org.cluster.membership.net;

import java.io.Serializable;
import java.util.TimeZone;

import org.cluster.membership.Config;
import org.cluster.membership.core.ClusterView;
import org.cluster.membership.core.MessageType;
import org.cluster.membership.model.Message;
import org.cluster.membership.model.MessageResponse;
import org.cluster.membership.model.Node;
import org.cluster.membership.net.core.MembershipClient;
import org.cluster.membership.net.core.MembershipIndirectClientHandler;
import org.cluster.membership.util.DateTime;
import org.cluster.membership.util.MathOp;

import io.netty.channel.ChannelHandlerContext;

public class RequestMessageHandler {
	
	private ClusterView clusterView;
	
	private ResponseHandler responseHandler;
	
	public MessageResponse<ClusterView> handlerSubscription(Message m) {
		Message add = new Message(MessageType.ADD_TO_CLUSTER, m.getNode(), MathOp.log2n(clusterView.getClusterSize()));
		clusterView.addToCluster(add);		
		ClusterView yourView = clusterView.yourView(m.getNode());		
		MessageResponse<ClusterView> response = new MessageResponse<>(yourView, m);		
		return response;		
	}
	
	public void handlerUnsubscription(Message m) {
		Message rem = new Message(MessageType.REMOVE_FROM_CLUSTER, m.getNode(), MathOp.log2n(clusterView.getClusterSize()));
		clusterView.removeFromCluster(rem);
	}
	
	public MessageResponse<Boolean> handlerProbe(Message m, ChannelHandlerContext ctx) {
		if(m.getNode().equals(Config.thisPeer())) {
			MessageResponse<Boolean> response = new MessageResponse<Boolean>(true, m);
			return response;
		} else {
			MembershipClient.connect(m.getNode(), 
					new MembershipIndirectClientHandler(m.getNode(),m, responseHandler,ctx));
			return null;
		}		
		
	}
		
	public void handlerSuspectDead(Message m) {			
		TimeZone remoteTimeZone = m.getGeneratedTimeZone();
		long remoteTime = (Long)m.getData();
		TimeZone localTimeZone = Config.thisPeer().getTimeZone();		
		long localTime = DateTime.localTime(remoteTime, remoteTimeZone, localTimeZone);
		clusterView.suspect(localTime, m);
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
	
	public MessageResponse<Serializable> handlerUpdateNode(Message m) {
		
		Node nd = m.getNode();
		if(clusterView.isSuspectedDead(nd)) {
			Message keepAliveMessage = new Message(MessageType.KEEP_ALIVE, nd, MathOp.log2n(clusterView.getClusterSize()));
			clusterView.keepAlive(keepAliveMessage);
		}
		
		if(clusterView.isFailing(nd)) clusterView.removeFailing(nd);
		
		long lastRumor = (long)m.getData();
		Serializable result = (Serializable)clusterView.getUpdatedView(lastRumor, m.getNode());
		
		return new MessageResponse<>(result, m);
		
	}

}
