package org.cluster.membership.common.debug;

import java.util.HashSet;
import java.util.Set;

public class StateInfo {
	
	private Set<String> nodes;
	private Set<String> suspecting;
	private Set<String> failing;
	
	public StateInfo(Set<String> nodes, Set<String> suspecting, Set<String> failing) {
		super();
		this.nodes = nodes;
		this.suspecting = suspecting;
		this.failing = failing;
	}
	
	public StateInfo() {
		super();
		this.nodes = new HashSet<String>();
		this.suspecting = new HashSet<String>();
		this.failing = new HashSet<String>();
	}
	
	public void wakeUp(Set<String> list) {
		suspecting.removeAll(list);
		nodes.addAll(list);
	}
	
	public void sleep(String nid) {
		suspecting.add(nid);
		nodes.remove(nid);
	}
	
	public void delete(String nid) {
		suspecting.remove(nid);
		nodes.remove(nid);
		failing.remove(nid);
	}
	
	public void addNode(String node) {
		nodes.add(node);
	}
	
	public void addSuspecting(String node) {
		suspecting.add(node);
	}
	
	public void addFailing(String node) {
		failing.add(node);
	}
	
	public StateInfo snapshot() {
		Set<String> nodesSnapshot = new HashSet<String>(nodes);
		Set<String> suspectingSnapshot = new HashSet<String>(suspecting);
		Set<String> failingSnapshot = new HashSet<String>(failing);		
		return new StateInfo(nodesSnapshot, suspectingSnapshot, failingSnapshot);
	}

	public Set<String> getNodes() {
		return nodes;
	}

	public Set<String> getSuspecting() {
		return suspecting;
	}

	public Set<String> getFailing() {
		return failing;
	}
	
}
