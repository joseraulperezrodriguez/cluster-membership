package org.cluster.membership.protocol.net.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MembershipClientForwardHandlerTest extends ChannelInboundHandlerAdapter {

	private Object toSend;
	private ChannelHandlerContext ctx;
		
	public MembershipClientForwardHandlerTest(Object toSend, ChannelHandlerContext ctx) {
		this.toSend = toSend;
		this.ctx = ctx;
	}	


	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(toSend);		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
	 	this.ctx.writeAndFlush(msg);
	 	ctx.close();
	}
		
    
}
