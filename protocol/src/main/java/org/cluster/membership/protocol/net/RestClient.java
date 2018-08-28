package org.cluster.membership.protocol.net;

import java.net.URI;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.model.ClusterData;
import org.cluster.membership.protocol.model.SynchronTypeWrapper;
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
		
	public SynchronTypeWrapper synchronize(Node to, Node updated,long firstTime) {
		try {
			URI uri = new URI(getEndPoint(to, "membership/synchronize/"+firstTime));			
			return template.postForObject(uri, updated, SynchronTypeWrapper.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
