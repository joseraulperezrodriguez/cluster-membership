package org.cluster.membership.tester.core;

import java.net.URI;

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
	
	public static void main(String[] args) throws Exception {
		/*CloseableHttpClient client = HttpClients.createDefault();
	    HttpGet getSize = new HttpGet("http://localhost:6001/size");
	    
	    CloseableHttpResponse response = client.execute(getSize);
	    
	    HttpEntity entity = response.getEntity();*/
		
		
		Integer size = restTemplate.getForObject("http://localhost:6001/membership/size", Integer.class);
		
		Boolean uns = restTemplate.postForObject(new URI("http://localhost:6001/membership/unsubscribe"), null, Boolean.class);
		
		Boolean pause = restTemplate.postForObject(new URI("http://localhost:6001/membership/pause"), 5000, Boolean.class);
		
		NodesDebug debug = restTemplate.getForObject(new URI("http://localhost:6001/membership/nodes-debug"), NodesDebug.class);
		
		System.out.println(size);
		System.out.println(uns);
		System.out.println(pause);
	    
		for(String s : debug.getNodes()) System.out.println(s);
	    
	}

}
