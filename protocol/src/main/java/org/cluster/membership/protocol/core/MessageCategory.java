package org.cluster.membership.protocol.core;

import java.io.Serializable;

public enum MessageCategory implements Serializable {
	
	/**message with the intention to test if node A can reach node B
	 * */
	PROBE(1),
	
	/**message with the intention of any in the cluster get the message
	 * */
	CLUSTER(2),
	
	/**message with the intension of spread the message over all the nodes
	 * */
	RUMOR(3);
	
	
	
	
	/**A priority to sort message, a way to know the importance of message type, has no effective implications until now 
	 * because of the asynchronous nature of communication
	 * */
	private final int priority;
	
	MessageCategory(int priority) {
		this.priority = priority;
	}
	
	public int getPriority() {
		return priority;
	}

}
