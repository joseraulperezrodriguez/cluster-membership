package org.cluster.membership.protocol.model;

import java.io.Serializable;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.MathOp;
import org.cluster.membership.protocol.core.MessageCategory;
import org.cluster.membership.protocol.core.MessageType;
import org.cluster.membership.protocol.time.ServerTime;

public class Message implements Comparable<Message>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Object data;

	private MessageType type;
	
	private Node node;
	
	private int iterations, iteration;
	
	private long generatedTime;
	
	public Message(MessageType type, Node node, int iterations) {
		assert(node != null);
		this.type = type;
		this.node = node;
		this.iterations = iterations;
		this.iteration = 0;
		this.generatedTime = ServerTime.getTime();
	}
	
	public Message(MessageType type, Node node, int iterations, Object data) {
		this(type, node, iterations);
		this.data = data;		
	}
	
	public Message() {}
	
	public Message adjustIteration(int size) {
		int cI = MathOp.log2n(size);
		Message ans = new Message(type, node, Math.max(cI, iterations), data);
		ans.iteration = iteration;
		return ans;
	}
	
	public long getGeneratedTime() {
		return generatedTime;
	}

	public Message sent() {
		this.iteration++;
		return this;
	}
		
	public MessageCategory getCategory() {
		return type.getCategory();
	}
	
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public boolean hasNextIteration() {
		return iteration < iterations;
	}
	
	public int remainingIterations() {
		return iterations - iteration;
	}
	
	public void rollbackPreviousIteration() {
		this.iteration--;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}
	
	@Override
	public int hashCode() {
	    int result = (int) (node.hashCode() ^ (node.hashCode() >>> 32));
	    result = 31 * result + type.name().hashCode();
	    return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Message)) return false;		
		Message other = (Message)obj;				
		return this.compareTo(other) == 0;
	}

	@Override
	public int compareTo(Message o) {
		if(this.type.getPriority() < o.getType().getPriority()) return -1;
		else if(this.type.getPriority() > o.getType().getPriority()) return 1;
				
		if(node.compareTo(o.getNode()) < 0) return -1;
		else if(node.compareTo(o.getNode()) > 0) return +1;
				
		if(data == null && o.getData() == null) return 0;
		else return (data.equals(o.getData()) ? 0 : data.hashCode() - o.getData().hashCode());
	}
	
	@Override
	public String toString() {
		return "(" + type + " " + node.getId() + " " + iterations + ")";
	}
	
}
