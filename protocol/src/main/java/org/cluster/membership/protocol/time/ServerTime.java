package org.cluster.membership.protocol.time;

public class ServerTime {
	
	private static final ITimeProvider timeProvider = new LocalToUTCTimeProvider();
	
	public static long getTime() {
		return timeProvider.getLogicalTime();
	}
	

}
