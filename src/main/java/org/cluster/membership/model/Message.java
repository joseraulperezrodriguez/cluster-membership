package org.cluster.membership.model;

import java.io.Serializable;
import java.util.TimeZone;

import org.cluster.membership.Config;
import org.cluster.membership.core.MessageCategory;
import org.cluster.membership.core.MessageType;
import org.cluster.membership.structures.SerializableComparator;

public class Message implements Comparable<Message>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Object data;

	private MessageType type;
	
	private Node node;
	
	private int iterations;
	
	private long generatedTime;
	
	private TimeZone generatedTimeZone;

	public Message(MessageType type, Node node, int iterations) {
		assert(node != null);
		this.type = type;
		this.node = node;
		this.iterations = iterations;
		this.generatedTime = System.currentTimeMillis();
		this.generatedTimeZone = Config.thisPeer().getTimeZone();
	}
	
	public Message(MessageType type, Node node, int iterations, Object data) {
		this(type, node, iterations);
		this.data = data;		
	}
	
	public long getGeneratedTime() {
		return generatedTime;
	}

	public TimeZone getGeneratedTimeZone() {
		return generatedTimeZone;
	}

	public Message sended() {
		this.iterations--;
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

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
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
	
	
	/**Set the MessageType to the lowest possible value and Node to a minimal id value to make sure no real node is lower than it
	 * */
	public static Message getMinIterationTemplate(int iterations) {
		return new Message(MessageType.getMinPriority(), Node.getLowerNode(), iterations);
	}
	
	/**Set the MessageType to the lowest possible value and Node to a minimal id value to make sure no real node is lower than it
	 * */
	public static Message getMinTimeTemplate(long time) {
		Message m  = new Message(MessageType.getMinPriority(), Node.getLowerNode(), 1);
		m.generatedTime = time;
		return m;
	}
	
	private static int compareIteration(Message a, Message b) {
		int comp = a.compareTo(b);
		if(comp == 0) return 0;

		if(a.getIterations() < b.getIterations()) return -1; 
		else if(a.getIterations() > b.getIterations()) return +1;			
		return 0;
		
	}
	
	private static int compareGeneratedTime(Message a, Message b) {
		int comp = a.compareTo(b);
		if(comp == 0) return 0;
		
		if(a.getGeneratedTime() < b.getGeneratedTime()) return -1; 
		else if(a.getGeneratedTime() > b.getGeneratedTime()) return +1;
		return 0;
		
	}
	
	private static int comparePriorityIteration(Message a, Message b) {
		if(a.getIterations() < b.getIterations()) return -1; 
		else if(a.getIterations() > b.getIterations()) return +1;			
		return 0;		
	}
	
	private static int comparePriorityGeneratedTime(Message a, Message b) {
		if(a.getGeneratedTime() < b.getGeneratedTime()) return -1; 
		else if(a.getGeneratedTime() > b.getGeneratedTime()) return +1;
		return 0;		
	}
	
	
	public static SerializableComparator<Message> getIterationsAscComparator()  { 
		return (a,b) ->  { 
				int comp = compareIteration(a,b);
				return (comp == 0 ? a.compareTo(b) : comp);
			}; 
	}

	public static SerializableComparator<Message> getIterationsDescComparator() { 
		return (a,b) ->  { 
			int comp = compareIteration(b,a);
			return (comp == 0 ? a.compareTo(b) : comp);
		};
	}	
	
	public static SerializableComparator<Message> getGeneratedTimeAscComparator() { 
		return (a,b) ->  {
			int comp = compareGeneratedTime(a, b);
			return (comp == 0 ? a.compareTo(b) : comp);
		};
	}
	
	public static SerializableComparator<Message> getGeneratedTimeDescComparator() { 
		return (a,b) ->  {
			int comp = compareGeneratedTime(b, a);
			return (comp == 0 ? a.compareTo(b) : comp);
		};
	}
	
	
	public static SerializableComparator<Message> getGeneratedTimePriorityAscComparator() { 
		return (a,b) -> {
			int comp = comparePriorityGeneratedTime(a, b);
			return (comp == 0 ? a.compareTo(b) : comp);
		};
	}
	
	public static SerializableComparator<Message> getGeneratedTimePriorityDescComparator() { 
		return (a,b) -> {
			int comp = comparePriorityGeneratedTime(b, a);
			return (comp == 0 ? a.compareTo(b) : comp);
		};
	}
	
	public static SerializableComparator<Message> getIteratorPriorityAscComparator() { 
		return (a,b) -> {
			int comp = comparePriorityIteration(a, b);
			return (comp == 0 ? a.compareTo(b) : comp);
		};
	}
	
	public static SerializableComparator<Message> getIteratorPriorityDescComparator() { 
		return (a,b) -> { 
			int comp = comparePriorityIteration(b, a);
			return (comp == 0 ? a.compareTo(b) : comp);		
		};
	}
	
	//public static Comparator<Message> getGeneratedTimeDescComparator() { return (a,b) ->  compareGeneratedTime(b, a); }

}
