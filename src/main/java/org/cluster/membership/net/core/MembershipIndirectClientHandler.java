package org.cluster.membership.net.core;

import org.cluster.membership.model.Message;
import org.cluster.membership.model.Node;
import org.cluster.membership.net.ResponseHandler;

import io.netty.channel.ChannelHandlerContext;

public class MembershipIndirectClientHandler extends MembershipClientHandler {

	private ChannelHandlerContext directContext;
	

	public MembershipIndirectClientHandler(Node to, Message message, ResponseHandler responseHandler, ChannelHandlerContext directContext) {
		super(responseHandler, to, message);		
		this.directContext = directContext;
	}

	@Override
	public void handleError() {
		if(this.getMessagesReaded() == 0) 
			this.getResponseHandler().addToFailed(getTo());		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		super.channelRead(ctx, msg);
		directContext.writeAndFlush(msg);		
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.channel().close();
	}

}
