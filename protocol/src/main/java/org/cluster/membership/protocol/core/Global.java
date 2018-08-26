package org.cluster.membership.protocol.core;

import org.cluster.membership.protocol.ClusterNodeEntry;
import org.springframework.boot.SpringApplication;

public class Global {

	private static boolean synchronizing;
	
	protected static synchronized void setSynchronizing(boolean value) {
		synchronizing = value;
	}
	
	public static boolean isSynchronizing() {
		return synchronizing;
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
	
	static {
		Global.synchronizing = false;
	}

}
