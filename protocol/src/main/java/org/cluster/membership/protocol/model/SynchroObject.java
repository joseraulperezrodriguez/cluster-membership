package org.cluster.membership.protocol.model;

import java.io.Serializable;
import java.util.List;

public class SynchroObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ClusterData clusterData;
	
	private List<Message> messages;


	public SynchroObject() {}
	
	public SynchroObject(ClusterData clusterData) {
		super();
		this.clusterData = clusterData;
	}
	
	public SynchroObject(List<Message> messages) {
		super();
		this.messages = messages;
	}
	
	public ClusterData getClusterData() {
		return clusterData;
	}

	public void setClusterData(ClusterData clusterData) {
		this.clusterData = clusterData;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	
	

}
