package org.cluster.membership.protocol.net.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MembershipClientHandlerTest extends ChannelInboundHandlerAdapter {

	private Object toSend;
	private Class<?> expected;
	
	private boolean asserted;
	
	public MembershipClientHandlerTest(Object toSend, Class<?> expected) {
		this.toSend = toSend;
		this.expected = expected;
		this.asserted = true;
	}	
	
	public boolean getAsserted() {
		return asserted;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(toSend);		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		System.out.println("received object: " + msg.getClass());
	 	assert(msg.getClass().equals(expected));	 	
	 	asserted = true;
	 	ctx.close();
	}
		
    
}
