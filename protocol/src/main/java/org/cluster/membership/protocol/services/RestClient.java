package org.cluster.membership.protocol.services;

import java.net.URI;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.model.ClusterData;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClient {
	
	private RestTemplate template;
	
	public RestClient() {
		this.template = new RestTemplate();
	}

	private String getEndPoint(Node to, String path) {
		return "http://" + to.getAddress() + ":" + to.getServicePort() + "/" + path;
	}
	
	public ClusterData subscribe(Node to, Node subscriptor) {
		try {
			String endPoint = getEndPoint(to, "membership/subscribe");
			URI uri = new URI(endPoint);
			return template.postForObject(uri, subscriptor, ClusterData.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
