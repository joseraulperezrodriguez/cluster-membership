package org.cluster.membership.tester.core;

import java.util.List;

public class NodesDebug {
	
	private List<String> nodes;
	private List<String> dead;
	private List<String> failing;
	
	public NodesDebug() {}
	
	public NodesDebug(List<String> nodes, List<String> dead, List<String> failing) {
		super();
		this.nodes = nodes;
		this.dead = dead;
		this.failing = failing;
	}
	public List<String> getNodes() {
		return nodes;
	}
	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}
	public List<String> getDead() {
		return dead;
	}
	public void setDead(List<String> dead) {
		this.dead = dead;
	}
	public List<String> getFailing() {
		return failing;
	}
	public void setFailing(List<String> failing) {
		this.failing = failing;
	}
	
	
	

}
