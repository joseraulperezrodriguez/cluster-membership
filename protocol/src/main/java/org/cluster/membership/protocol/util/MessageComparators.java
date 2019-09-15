package org.cluster.membership.protocol.util;

import java.util.TimeZone;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.core.MessageType;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.structures.SerializableComparator;

public class MessageComparators {
	
	/**Set the MessageType to the lowest possible value and Node to a minimal id value to make sure no real node is lower than it
	 * */
	public static Message getMinIterationTemplate(int iterations, TimeZone timeZone) {
		return new Message(MessageType.getMinPriority(), Node.getLowerNode(), iterations, timeZone);
	}
	
	/**Set the MessageType to the lowest possible value and Node to a minimal id value to make sure no real node is lower than it
	 * */
	public static Message getMinTimeTemplate(long time) {
		Message m  = new Message(MessageType.getMinPriority(), Node.getLowerNode(), 1);
		//m.setGeneratedTime(time);
		return m;
	}
	
	/**Set the MessageType to the greatest possible value and Node to a maximal id value to make sure no real node is greater than it
	 * */
	public static Message getMaxTimeTemplate(long time) {
		Message m  = new Message(MessageType.getMaxPriority(), Node.getLowerNode(), 1);
		return m;
	}
	
	private static int compareIteration(Message a, Message b) {
		int comp = a.compareTo(b);
		if(comp == 0) return 0;
		
		if(a.remainingIterations() < b.remainingIterations()) return -1;
		else if(a.remainingIterations() > b.remainingIterations()) return +1;
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
		if(a.remainingIterations() < b.remainingIterations()) return -1;
		else if(a.remainingIterations() > b.remainingIterations()) return +1;
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

}
