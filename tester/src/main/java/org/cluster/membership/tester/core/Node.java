package org.cluster.membership.tester.core;

public class Node implements Comparable<Node> {
	
	private String id;
	
	private String address;
	
	private int protocolPort;
	
	private int servicePort;
	
	private String timeZone;
	
	public Node(String id, String address, int protocolPort, int servicePort, String timeZone) {
		super();
		this.id = id;
		this.address = address;
		this.protocolPort = protocolPort;
		this.servicePort = servicePort;
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

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Node)) return false;
		
		Node node = (Node)obj;
		return this.id.equals(node.id);
	}

	@Override
	public int compareTo(Node o) {
		return id.compareTo(o.id);
	}
	
	public String commandLineParamString(int idx) {
		return " --id." + idx + "=" + id +
				" --address." + idx + "=" + address +
				" --port." + idx + "=" + protocolPort +
				" --time-zone." + idx + "=" + timeZone;
	}

}
