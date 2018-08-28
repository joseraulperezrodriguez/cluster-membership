package org.cluster.membership.tester.core;

import org.springframework.web.client.RestTemplate;

public class HttpClient {
	
	
	private static RestTemplate restTemplate = new RestTemplate();
	
	private static String getAddress(Node node, String path) {
		String address = node.getAddress();
		int port = node.getServicePort();		
		return "http://" + address + ":" + port + "/" + path;		
	}
	
	public static int size(Node node) {
		return restTemplate.getForObject(getAddress(node, "/membership/size"), 
				Integer.class);
	}
	
	public static boolean unsubscribe(Node node) {
		return restTemplate.postForObject(getAddress(node, "/membership/unsubscribe"), null, 
				Boolean.class);
	}
	
	public static boolean pause(Node node, long time) {
		return restTemplate.postForObject(getAddress(node, "/membership/pause"), time, 
				Boolean.class);
	}
	
	public static NodesDebug nodes(Node node) {
		return restTemplate.getForObject(getAddress(node, "/membership/nodes-debug"), 
				NodesDebug.class);
	}

}
