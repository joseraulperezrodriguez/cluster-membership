package org.cluster.membership.common.model;

import java.io.Serializable;
import java.util.TimeZone;

public class Node implements Comparable<Node>, Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String address;
	private int protocolPort;
	private int servicePort;
	
	private TimeZone timeZone;
	
	public Node(String id, String address, int protocolPort, int servicePort, TimeZone timeZone) {
		super();
		this.id = id;
		this.address = address;
		this.protocolPort = protocolPort;
		this.servicePort = servicePort;
		this.timeZone = timeZone;				
	}
	
	public Node(String id, String address, int protocolPort, int servicePort, String timeZone) {
		super();
		this.id = id;
		this.address = address;
		this.protocolPort = protocolPort;
		this.servicePort = servicePort;
		this.timeZone = TimeZone.getTimeZone(timeZone);				
	}
	
	public Node() {}
	
	
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
	
	/*public String commandLineParamString(int idx) {
		return " --id." + idx + "=" + id +
				" --address." + idx + "=" + address +
				" --protocol.port." + idx + "=" + protocolPort +
				" --server.port." + idx + "=" + servicePort +
				" --time.zone." + idx + "=" + timeZone;
	}*/
	
	@Override
	public String toString() {
		return id + " " + address + " " + protocolPort + " " + servicePort + " " + timeZone.getID();
	}
	
	public static Node getLowerNode() {
		return new Node(" "," ",0,0,TimeZone.getDefault());
	}
	
	public static Node getGreaterNode() {
		return new Node("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz","z",0,0,TimeZone.getDefault());
	}

}
