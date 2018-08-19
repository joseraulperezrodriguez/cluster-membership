package org.cluster.membership.net.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MembershipServerHandlerTest extends ChannelInboundHandlerAdapter {
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("server received: " + msg.getClass().getName());
		ctx.writeAndFlush(msg);	
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		assert(false);
		super.exceptionCaught(ctx, cause);
	}

}
