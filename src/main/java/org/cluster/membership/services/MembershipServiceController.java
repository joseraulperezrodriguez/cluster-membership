package org.cluster.membership.services;

import org.cluster.membership.core.ClusterView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/membership")
public class MembershipServiceController {
	
	@Autowired
	private ClusterView clusterView;
			
	@GetMapping("/size")	
	public int getSize() {
		return clusterView.getClusterSize();
	}

}
