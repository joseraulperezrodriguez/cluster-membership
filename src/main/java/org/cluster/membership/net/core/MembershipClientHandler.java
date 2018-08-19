package org.cluster.membership.net.core;

import java.util.Arrays;
import java.util.List;

import org.cluster.membership.Config;
import org.cluster.membership.model.Message;
import org.cluster.membership.model.Node;
import org.cluster.membership.model.RequestDescription;
import org.cluster.membership.model.ResponseDescription;
import org.cluster.membership.net.ResponseHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class MembershipClientHandler extends ChannelInboundHandlerAdapter {

	private Node to;
	private List<Message> messages;
	private int messagesReaded;
	private long startTime;
	private long endTime;
	private int totalExpectedMessages;
	
	private ResponseHandler responseHandler;
	
	
	public MembershipClientHandler(ResponseHandler responseHandler, Node to, List<Message> messages) {
		this.responseHandler = responseHandler;
		this.to = to;
		this.messages = messages;
		this.messagesReaded = 0; 
		this.totalExpectedMessages = 0;
	}
	
	public MembershipClientHandler(ResponseHandler responseHandler, Node to, Message... messages) {
		this.responseHandler = responseHandler;
		this.to = to;
		this.messages = Arrays.asList(messages);
		this.messagesReaded = 0; 
		this.totalExpectedMessages = 0;
	}
	
	public abstract void handleError();

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(new RequestDescription(Config.thisPeer(), messages));
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
