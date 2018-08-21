package org.cluster.membership.protocol.model;

import java.io.Serializable;
import java.util.TimeZone;

public class Node implements Comparable<Node>, Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String address;
	private int port;
	
	private TimeZone timeZone;
	
	public Node(String id, String address, int port, TimeZone timeZone) {
		super();
		this.id = id;
		this.address = address;
		this.port = port;
		this.timeZone = timeZone;		
		
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
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
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
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
		return id + " " + address + " " + port;
	}
	
	public static Node getLowerNode() {
		return new Node(" "," ",0,TimeZone.getDefault());
	}
	
	public static Node getGreaterNode() {
		return new Node("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz","z",0,TimeZone.getDefault());
	}

}
