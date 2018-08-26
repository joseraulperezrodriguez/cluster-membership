package org.cluster.membership.protocol.net.core;

import org.cluster.membership.protocol.net.RequestReceiver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class MembershipServerHandlerDebug extends MembershipServerHandler {
	
	private boolean paused;
	private long pausedMillis;
	
	public MembershipServerHandlerDebug(RequestReceiver messageReceiver) {
		super(messageReceiver);
		this.paused = false;
	}
	
	public void pause(long millis) {
		this.paused = true;
		this.pausedMillis = millis;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(paused)Thread.sleep(pausedMillis);
		this.paused = false;
		super.channelRead(ctx, msg);	
	}

}
