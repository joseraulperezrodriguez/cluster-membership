package org.cluster.membership.common.debug;

import java.util.List;

public class StateInfo {
	
	private List<String> nodes;
	private List<String> suspecting;
	private List<String> failing;
	
	public StateInfo(List<String> nodes, List<String> dead, List<String> failing) {
		super();
		this.nodes = nodes;
		this.suspecting = dead;
		this.failing = failing;
	}
	
	public StateInfo() {
		super();
	}
	
	public List<String> getNodes() {
		return nodes;
	}
	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}
	public List<String> getDead() {
		return suspecting;
	}
	public void setDead(List<String> dead) {
		this.suspecting = dead;
	}
	public List<String> getFailing() {
		return failing;
	}
	public void setFailing(List<String> failing) {
		this.failing = failing;
	}
	
	
	

}
