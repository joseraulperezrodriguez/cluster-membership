package org.cluster.membership.protocol.model;

import java.util.List;

public class SynchronizationObjectWrapper {
	
	private ClusterData clusterData;
	
	private List<Message> messages;


	public SynchronizationObjectWrapper() {}
	
	public SynchronizationObjectWrapper(ClusterData clusterData, List<Message> messages) {
		super();
		this.clusterData = clusterData;
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
