package org.cluster.membership.tester.core;

import org.cluster.membership.common.debug.StateInfo;
import org.cluster.membership.common.model.Node;
import org.springframework.web.client.RestTemplate;

public class RestClient {
	
	
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
	
	public static boolean shutdown(Node node, int seconds) {
		return restTemplate.postForObject(getAddress(node, "/membership/debug/shutdown"), seconds, 
				Boolean.class);
	}
	
	public static boolean pause(Node node, long time) {
		return restTemplate.postForObject(getAddress(node, "/membership/debug/pause"), time, 
				Boolean.class);
	}
	
	public static StateInfo getStateInfo(Node node) {
		return restTemplate.getForObject(getAddress(node, "/membership/debug/state-info"), 
				StateInfo.class);
	}

}
