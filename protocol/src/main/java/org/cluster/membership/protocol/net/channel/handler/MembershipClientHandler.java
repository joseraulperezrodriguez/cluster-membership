package org.cluster.membership.protocol.net.channel.handler;

import java.util.Arrays;
import java.util.List;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.model.FrameMessageCount;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.model.RequestDescription;
import org.cluster.membership.protocol.model.ResponseDescription;
import org.cluster.membership.protocol.net.core.ResponseHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class MembershipClientHandler extends ChannelInboundHandlerAdapter {

	private Node to;
	private Node from;
	private List<Message> messages;
	private int messagesReaded;
	private long startTime;
	private long endTime;
	private int totalExpectedMessages;
	
	private ResponseHandler responseHandler;
	
	private FrameMessageCount frameMessageCount;
	
	public MembershipClientHandler(FrameMessageCount frameMessageCount, ResponseHandler responseHandler, Node from, Node to, List<Message> messages) {
		build(frameMessageCount, responseHandler,from, to, messages);
	}
	
	public MembershipClientHandler(FrameMessageCount frameMessageCount, ResponseHandler responseHandler, Node from, Node to, Message... messages) {
		build(frameMessageCount, responseHandler, from, to, Arrays.asList(messages));
	}
	
	public void build(FrameMessageCount frameMessageCount, ResponseHandler responseHandler, Node from, Node to, List<Message> messages) {
		this.frameMessageCount = frameMessageCount;
		this.responseHandler = responseHandler;
		this.from = from;
		this.to = to;
		this.messages = messages;
	}
	
	public abstract void handleError();

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(new RequestDescription(from, this.frameMessageCount, messages));
		startTime = System.currentTimeMillis();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		handleError();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		this.setEndTime(System.currentTimeMillis());
		ResponseDescription response = (ResponseDescription)msg;
		response.setResponseTime(getEndTime() - getStartTime());
		increaseMessageReaded();
		getResponseHandler().receive(response, this);
		if(getMessagesReaded() == totalExpectedMessages) 
			ctx.close();
				
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		handleError();
		cause.printStackTrace();
		ctx.close();
	}
	
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public void increaseMessageReaded() {
		this.messagesReaded++;
	}

	public int getMessagesReaded() {
		return messagesReaded;
	}

	public Node getTo() {
		return to;
	}

	public ResponseHandler getResponseHandler() {
		return responseHandler;
	}

	public int getTotalExpectedMessages() {
		return totalExpectedMessages;
	}

	public void setTotalExpectedMessages(int totalExpectedMessages) {
		this.totalExpectedMessages = totalExpectedMessages;
	}

	public List<Message> getMessages() {
		return messages;
	}
		
    
}
