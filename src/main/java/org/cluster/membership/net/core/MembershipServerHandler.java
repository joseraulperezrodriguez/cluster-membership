package org.cluster.membership.net.core;

import java.util.List;

import org.cluster.membership.model.MessageResponse;
import org.cluster.membership.model.RequestDescription;
import org.cluster.membership.model.ResponseDescription;
import org.cluster.membership.net.RequestReceiver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MembershipServerHandler extends ChannelInboundHandlerAdapter {
	
	public RequestReceiver messageReceiver;
	
	public MembershipServerHandler(RequestReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		RequestDescription request = (RequestDescription)msg;
		
		List<MessageResponse<?>> messages = messageReceiver.receive(request.getNode(), request.getData(), ctx);
				
		ResponseDescription response = new ResponseDescription(messages);
		
		ctx.writeAndFlush(response);
	
	}

}
