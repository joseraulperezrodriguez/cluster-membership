package org.cluster.membership.services;

import java.util.List;

import org.cluster.membership.core.ClusterView;
import org.cluster.membership.model.Node;
import org.cluster.membership.net.core.MembershipServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/membership")
public class MembershipServiceController {
	
	@Autowired
	private ClusterView clusterView;
	
	@Autowired
	private MembershipServer membershipServer;
			
	@GetMapping("/size")	
	public int getSize() {
		return clusterView.getClusterSize();
	}
	
	@PostMapping("/unsubscribe")	
	public void unSubscribe() {
		clusterView.unsubscribe();
	}
	
	@PostMapping("/pause")	
	public void pause(@PathVariable("millis")long millis) {
		membershipServer.pause(millis);
	}
	
	@GetMapping("/nodes")
	public List<Node> nodes() {
		return clusterView.nodes();
	}

}
