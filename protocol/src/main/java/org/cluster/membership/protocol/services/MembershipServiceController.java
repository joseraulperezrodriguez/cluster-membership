package org.cluster.membership.protocol.services;

import java.util.List;
import java.util.logging.Logger;

import org.cluster.membership.protocol.core.ClusterView;
import org.cluster.membership.protocol.debug.NodesDebug;
import org.cluster.membership.protocol.model.ClusterData;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.model.Node;
import org.cluster.membership.protocol.model.SynchronTypeWrapper;
import org.cluster.membership.protocol.net.core.MembershipServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/membership")
public class MembershipServiceController {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private ClusterView clusterView;
	
	@Autowired
	private MembershipServer membershipServer;
			
	@GetMapping("/size")	
	public int getSize() {
		return clusterView.getClusterSize();
	}
	
	@PostMapping("/unsubscribe")	
	public boolean unSubscribe() {
		logger.info("unsubscribe message received");
		clusterView.unsubscribe();
		return true;
	}
	
	@PostMapping("/pause")	
	public boolean pause(@RequestBody(required = true) long millis) {
		logger.info("pause message received " + millis);
		membershipServer.pause(millis);
		return true;
	}
	
	@GetMapping("/nodes")
	public List<Node> nodes() {		
		return clusterView.nodes();
	}
	
	@GetMapping("/nodes-debug")
	public NodesDebug nodesDebug() {
		List<String> nodes = clusterView.nodesDebug();
		List<String> dead = clusterView.deadNodes();
		List<String> failed = clusterView.failingNodes();
				
		return new NodesDebug(nodes, dead, failed);
	}
		
	@PostMapping("/update/full-view")
	public boolean updateView(@RequestBody(required = true)ClusterData clusterData) {
		clusterView.updateMyView(clusterData);
		return true;
	}
	
	@PostMapping("/update/commit-log")
	public boolean updateView(@RequestBody(required = true)List<Message> clusterData) {
		clusterView.updateMyView(clusterData);
		return true;
	}
	
	@PostMapping("/subscribe")
	public ClusterData subscribe(@RequestBody(required = true)Node node) {
		logger.info("enter subscribe endpoint: " + node);
		clusterView.subscribe(node);
		return clusterView.myView(node);
	}
		
	@PostMapping("/synchronize/{first-time}")
	public SynchronTypeWrapper synchronize(@PathVariable(name="first-time")Long firstTime,@RequestBody(required = true) Node node) {		
		return clusterView.handlerUpdateNodeRequest(node, firstTime);
	}
	
	
	
}
