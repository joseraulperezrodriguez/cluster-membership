package org.cluster.membership.protocol.net.test;

import org.cluster.membership.protocol.model.Node;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MembershipServerForwardHandlerTest extends ChannelInboundHandlerAdapter {
	
	private Node to;
	
	public MembershipServerForwardHandlerTest(Node to) {
		this.to = to;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MembershipClientTest.connect(to, new MembershipClientForwardHandlerTest(msg, ctx));	
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		assert(false);
		super.exceptionCaught(ctx, cause);
	}

}
