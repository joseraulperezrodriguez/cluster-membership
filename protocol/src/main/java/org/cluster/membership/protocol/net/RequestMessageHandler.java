package org.cluster.membership.protocol.net;

import org.cluster.membership.common.model.util.MathOp;
import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.core.ClusterView;
import org.cluster.membership.protocol.core.MessageType;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.model.MessageResponse;
import org.cluster.membership.protocol.net.core.MembershipClient;
import org.cluster.membership.protocol.net.core.MembershipIndirectClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;

@Component
public class RequestMessageHandler {
	
	@Autowired
	private ClusterView clusterView;
	
	@Autowired
	private ResponseHandler responseHandler;
		
	@Autowired
	private Config config;
	
	public void handlerUnsubscription(Message m) {
		Message rem = new Message(MessageType.REMOVE_FROM_CLUSTER, m.getNode(), MathOp.log2n(clusterView.getClusterSize()), config.getThisPeer().getTimeZone());
		clusterView.removeFromCluster(rem);
	}
	
	public MessageResponse<Boolean> handlerProbe(Message m, ChannelHandlerContext ctx) {
		if(m.getNode().equals(config.getThisPeer())) {
			MessageResponse<Boolean> response = new MessageResponse<Boolean>(true, m);
			return response;
		} else {
			MembershipClient.connect(m.getNode(), 
					new MembershipIndirectClientHandler(clusterView.getFrameMessageCount(), config.getThisPeer(), m.getNode(),m, responseHandler,ctx),
					config);
			return null;
		}
		
	}
		
	public void handlerSuspectDead(Message m) {
		/*TimeZone remoteTimeZone = m.getGeneratedTimeZone();
		long remoteTime = (Long)m.getData();
		TimeZone localTimeZone = Config.THIS_PEER.getTimeZone();		
		long localTime = DateTime.localTime(remoteTime, remoteTimeZone, localTimeZone);
		clusterView.suspect(localTime, m);*/
		
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
	
}
