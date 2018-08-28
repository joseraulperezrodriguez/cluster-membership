package org.cluster.membership.protocol.model;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import org.cluster.membership.protocol.structures.DList;
import org.cluster.membership.protocol.structures.ValuePriorityEntry;

public class ClusterData implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private DList nodes;
	
	private Set<Message> rumorsToSend;
	
	private Set<ValuePriorityEntry<Node, Long>> suspectingNodesTimeout;
		
	public ClusterData() {
		nodes = new DList();
		rumorsToSend = new TreeSet<Message>();
		suspectingNodesTimeout = new TreeSet<ValuePriorityEntry<Node, Long>>();
		
	}

	public ClusterData(DList nodes, Set<Message> rumorsToSend,
			Set<ValuePriorityEntry<Node, Long>> suspectingNodesTimeout) {
		super();
		this.nodes = nodes;
		this.rumorsToSend = rumorsToSend;
		this.suspectingNodesTimeout = suspectingNodesTimeout;
	}

	public Set<Message> getRumorsToSend() {
		return rumorsToSend;
	}

	public void setRumorsToSend(Set<Message> rumorsToSend) {
		this.rumorsToSend = rumorsToSend;
	}

	public Set<ValuePriorityEntry<Node, Long>> getSuspectingNodesTimeout() {
		return suspectingNodesTimeout;
	}

	public void setSuspectingNodesTimeout(Set<ValuePriorityEntry<Node, Long>> suspectingNodesTimeout) {
		this.suspectingNodesTimeout = suspectingNodesTimeout;
	}

	public DList getNodes() {
		return nodes;
	}

	public void setNodes(DList nodes) {
		this.nodes = nodes;
	}
	
	@Override
	public String toString() {
		return "cluster size: " + nodes.size() + " dead nodes: " + suspectingNodesTimeout.size();
	}

}
