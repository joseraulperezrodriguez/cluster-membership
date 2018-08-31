package org.cluster.membership.protocol.core;

import org.cluster.membership.protocol.ClusterNodeEntry;
import org.springframework.boot.SpringApplication;

public class Global {

	private static boolean synchronizing;
	
	private static boolean ready;
	
	protected static synchronized void setSynchronizing(boolean value) {
		synchronizing = value;
		if(value) setReady(false);
	}
	
	public static boolean isSynchronizing() {
		return synchronizing;
	}
	
	public static synchronized void setReady(boolean value) {
		ready = value;
	}
	
	public static boolean isReady() {
		return ready;
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
