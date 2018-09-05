package org.cluster.membership.protocol.core;

import org.cluster.membership.common.model.util.DateTime;
import org.cluster.membership.protocol.ClusterNodeEntry;
import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.model.FrameMessageCount;
import org.springframework.boot.SpringApplication;

public class Global {

	private static FrameMessageCount frameMessCount;
	
	static {
		long nowUTC = DateTime.utcTime(System.currentTimeMillis(), Config.THIS_PEER.getTimeZone());
		frameMessCount = new FrameMessageCount(nowUTC,nowUTC,0);
	}
	
	public static void updateFrameMessageCount(FrameMessageCount frameMessageCount) {
		Global.frameMessCount = frameMessageCount;
	}
	
	public static FrameMessageCount getFrameMessageCount() {
		return frameMessCount;
	}
			
	public static void shutdown(long seconds) {
		new Runnable() {			
			@Override
			public void run() {
				try {
					Thread.sleep(seconds * 1000);
					SpringApplication.exit(ClusterNodeEntry.applicationContext, () -> 0);	
				} catch(Exception e) {
					SpringApplication.exit(ClusterNodeEntry.applicationContext, () -> 0);
				}
			}
		}.run();
	}

}
