package org.cluster.membership.protocol.net;

import java.util.TimeZone;

import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.core.ClusterView;
import org.cluster.membership.protocol.core.MessageType;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.model.MessageResponse;
import org.cluster.membership.protocol.net.core.MembershipClient;
import org.cluster.membership.protocol.net.core.MembershipIndirectClientHandler;
import org.cluster.membership.protocol.util.DateTime;
import org.cluster.membership.protocol.util.MathOp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;

@Component
public class RequestMessageHandler {
	
	@Autowired
	private ClusterView clusterView;
	
	@Autowired
	private ResponseHandler responseHandler;
		
	public void handlerUnsubscription(Message m) {
		Message rem = new Message(MessageType.REMOVE_FROM_CLUSTER, m.getNode(), MathOp.log2n(clusterView.getClusterSize()));
		clusterView.removeFromCluster(rem);
	}
	
	public MessageResponse<Boolean> handlerProbe(Message m, ChannelHandlerContext ctx) {
		if(m.getNode().equals(Config.THIS_PEER)) {
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
		TimeZone localTimeZone = Config.THIS_PEER.getTimeZone();		
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
	
}
