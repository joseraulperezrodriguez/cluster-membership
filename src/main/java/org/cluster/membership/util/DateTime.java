package org.cluster.membership.util;

import java.util.TimeZone;

public class DateTime {

	public static long localTime(long remoteTime, TimeZone remoteTimeZone, TimeZone localTimeZone) {		
		long offset = remoteTimeZone.getOffset(remoteTime);
		long nowUTC = remoteTime - offset;
		long nowLocal = localTimeZone.getOffset(nowUTC) + nowUTC;
		return nowLocal;
	}
	
}
