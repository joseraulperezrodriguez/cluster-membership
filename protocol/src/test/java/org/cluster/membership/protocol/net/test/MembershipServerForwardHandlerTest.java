package org.cluster.membership.protocol.net.test;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.Config;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class MembershipServerForwardHandlerTest extends ChannelInboundHandlerAdapter {
	
	private Node to;
	private Config config;
	
	public MembershipServerForwardHandlerTest(Node to, Config config) {
		this.to = to;
		this.config = config;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MembershipClientTest.connect(to, new MembershipClientForwardHandlerTest(msg, ctx), config);	
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		assert(false);
		super.exceptionCaught(ctx, cause);
	}

}
