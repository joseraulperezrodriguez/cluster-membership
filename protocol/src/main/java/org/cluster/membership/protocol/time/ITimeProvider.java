package org.cluster.membership.protocol.time;

public interface ITimeProvider {

	public long getLogicalTime();
	
	public long getPhysicalTime();
	
}
