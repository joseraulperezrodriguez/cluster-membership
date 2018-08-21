package org.cluster.membership.protocol.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class RequestDescription implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Node node;
	
	private List<Message> data;

	public RequestDescription(Node node, List<Message> data) {
		super();
		this.node = node;
		this.data = data;
	}
	
	public RequestDescription(Node receiver, Message... data) {
		super();
		this.node = receiver;
		this.data = Arrays.asList(data);
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
