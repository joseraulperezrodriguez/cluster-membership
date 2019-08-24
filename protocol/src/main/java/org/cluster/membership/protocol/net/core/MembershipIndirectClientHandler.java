package org.cluster.membership.protocol.net.core;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.model.FrameMessageCount;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.net.ResponseHandler;

import io.netty.channel.ChannelHandlerContext;

public class MembershipIndirectClientHandler extends MembershipClientHandler {

	private ChannelHandlerContext directContext;
	

	public MembershipIndirectClientHandler(FrameMessageCount frameMessageCount, Node from, Node to, Message message, ResponseHandler responseHandler, ChannelHandlerContext directContext) {
		super(frameMessageCount, responseHandler, from, to, message);		
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
