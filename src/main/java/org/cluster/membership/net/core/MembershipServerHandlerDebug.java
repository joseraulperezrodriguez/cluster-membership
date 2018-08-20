package org.cluster.membership.net.core;

import org.cluster.membership.net.RequestReceiver;

import io.netty.channel.ChannelHandlerContext;

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
