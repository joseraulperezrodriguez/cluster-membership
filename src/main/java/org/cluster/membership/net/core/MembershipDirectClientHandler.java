package org.cluster.membership.net.core;

import java.util.List;
import java.util.TreeSet;

import org.cluster.membership.core.MessageType;
import org.cluster.membership.model.Message;
import org.cluster.membership.model.Node;
import org.cluster.membership.model.ResponseDescription;
import org.cluster.membership.net.ResponseHandler;

import io.netty.channel.ChannelHandlerContext;

public class MembershipDirectClientHandler extends MembershipClientHandler {
	
	private TreeSet<Message> indirectMessages;
	private boolean directReceived;
	
	public MembershipDirectClientHandler(ResponseHandler responseReceiver, Node to, List<Message> messages) {
		super(responseReceiver, to, messages);
		
		indirectMessages = new TreeSet<Message>();
		
		for(Message m : messages) 
			if(isIndirectMessage(m)) 
				indirectMessages.add(m);
			
		
		this.directReceived = (indirectMessages.size() == messages.size());
		this.setTotalExpectedMessages(indirectMessages.size() + (directReceived ? 0 : 1));
		
	}
	
	public boolean isIndirectMessage(Message m) {
		return m.getType() == MessageType.PROBE && !m.getNode().equals(getTo());
	}
	
	public void checkDirectReceived(Object msg) {
		ResponseDescription response = (ResponseDescription)msg;		
		
		if(response.getReponses().size() != 1 || !isIndirectMessage(response.getReponses().get(0).getMessage()))
			directReceived = true; 
		else 
			indirectMessages.remove(response.getReponses().get(0).getMessage());
		
	}

	@Override
	public void handleError() {
		if(getTotalExpectedMessages() == getMessagesReaded()) return;
		
		if(!directReceived) {
			getResponseHandler().addToFailed(getTo());
			getResponseHandler().restoreMessages(getMessages());
		}
		
		getResponseHandler().suspectAll(indirectMessages);					
		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {		
		super.channelRead(ctx, msg);
		checkDirectReceived(msg);
	}

}
