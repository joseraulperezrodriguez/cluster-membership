package org.cluster.membership.common.model;

import java.io.Serializable;

public class Node implements Comparable<Node>, Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String address;
	private int protocolPort;
	private int servicePort;
	
	public Node(String id, String address, int protocolPort, int servicePort) {
		super();
		this.id = id;
		this.address = address;
		this.protocolPort = protocolPort;
		this.servicePort = servicePort;
	}
		
	public Node() {}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public int getProtocolPort() {
		return protocolPort;
	}

	public void setProtocolPort(int protocolPort) {
		this.protocolPort = protocolPort;
	}

	public int getServicePort() {
		return servicePort;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Node)) return false;
		return this.id.equals(((Node)obj).id);
	}

	@Override
	public int compareTo(Node o) {
		return this.id.compareTo(o.id);
	}
		
	@Override
	public String toString() {
		return id + " " + address + " " + protocolPort + " " + servicePort;
	}
	
	public static Node getLowerNode() {
		return new Node(" "," ",0,0);
	}
	
	public static Node getGreaterNode() {
		return new Node(String.valueOf('z' + 1),"",0,0);
	}
	
}
