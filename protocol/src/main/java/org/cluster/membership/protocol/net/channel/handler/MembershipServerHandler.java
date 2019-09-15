package org.cluster.membership.protocol.net.channel.handler;

import org.cluster.membership.protocol.model.RequestDescription;
import org.cluster.membership.protocol.model.ResponseDescription;
import org.cluster.membership.protocol.net.RequestReceiver;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class MembershipServerHandler extends ChannelInboundHandlerAdapter {
	
	public RequestReceiver messageReceiver;
	
	public MembershipServerHandler(RequestReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		RequestDescription request = (RequestDescription)msg;
		ResponseDescription response = messageReceiver.receive(request, ctx);
		ctx.writeAndFlush(response);
	}
	
	@Sharable
	public static class Debug extends MembershipServerHandler {
	
		private boolean paused;
		private long pausedMillis;
		
		public Debug(RequestReceiver messageReceiver) {
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

}
