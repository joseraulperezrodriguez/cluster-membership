package org.cluster.membership.protocol.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.cluster.membership.common.model.Node;

public class RequestDescription implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Node node;
	
	private List<Message> data;
	
	private FrameMessageCount frameMessCount;

	public RequestDescription(Node node, FrameMessageCount frameMessCount, List<Message> data) {
		super();
		this.node = node;
		this.frameMessCount = frameMessCount;
		this.data = data;
	}
	
	public RequestDescription(Node receiver, FrameMessageCount frameMessCount, Message... data) {
		super();
		this.node = receiver;
		this.frameMessCount = frameMessCount;
		this.data = Arrays.asList(data);
	}
	
	public FrameMessageCount getFrameMessCount() {
		return frameMessCount;
	}

	public void setFrameMessCount(FrameMessageCount frameMessCount) {
		this.frameMessCount = frameMessCount;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public List<Message> getData() {
		return data;
	}

	public void setData(List<Message> data) {
		this.data = data;
	}
	
	
	

}
