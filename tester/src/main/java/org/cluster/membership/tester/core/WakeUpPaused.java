package org.cluster.membership.tester.core;

import java.util.Set;
import java.util.TimerTask;
import java.util.TreeMap;

import org.cluster.membership.common.debug.StateInfo;

public class WakeUpPaused extends TimerTask {

	private StateInfo lastView;
	
	private TreeMap<Long, Set<String>> timeToWake;
	
	public WakeUpPaused(StateInfo lastView, TreeMap<Long, Set<String>> timeToWake) {
		this.lastView = lastView;
		this.timeToWake = timeToWake;
	}
	
	@Override
	public void run() {
		long current = System.currentTimeMillis();
		while(timeToWake.size() > 0) {
			long last= timeToWake.lastKey();
			if(current < last) break;
			
			Set<String> list = timeToWake.get(last);
			lastView.wakeUp(list);
			timeToWake.remove(last);
		}
		
	}

}
