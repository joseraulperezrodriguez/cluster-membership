package org.cluster.membership.protocol.time;

import java.util.Calendar;

public class LocalToUTCTimeProvider implements ITimeProvider {

	private final long offset;  
	
	public LocalToUTCTimeProvider() {
		offset=Calendar.getInstance().getTimeZone().getOffset(System.currentTimeMillis());
	}

	@Override
	public long getLogicalTime() {
		return System.currentTimeMillis() - offset;
	}

	@Override
	public long getPhysicalTime() {
		return System.currentTimeMillis() - offset;
	}

}
