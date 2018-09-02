package org.cluster.membership.common.model.util;

import java.util.TimeZone;

public class DateTime {

	public static long localTime(long remoteTime, TimeZone remoteTimeZone, TimeZone localTimeZone) {		
		long offset = remoteTimeZone.getOffset(remoteTime);
		long nowUTC = remoteTime - offset;
		long nowLocal = localTimeZone.getOffset(nowUTC) + nowUTC;
		return nowLocal;
	}
	
	public static long utcTime(long localTime, TimeZone localTimeZone) {
		long offset = localTimeZone.getOffset(localTime);
		long nowUTC = localTime - offset;
		return nowUTC;
	}
	
}
