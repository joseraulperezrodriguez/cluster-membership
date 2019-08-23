package org.cluster.membership.tester.deploy;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TreeMap;

import org.cluster.membership.common.debug.StateInfo;
import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.Literals;
import org.cluster.membership.common.model.util.MathOp;
import org.cluster.membership.tester.config.AbstractEnvConfig;
import org.cluster.membership.tester.core.RestClient;
import org.cluster.membership.tester.core.Snapshot;
import org.cluster.membership.tester.core.WakeUpPaused;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public abstract class AbstractDeploymentAndExecutionSimulator<T extends AbstractEnvConfig> {

	private ObjectMapper objectMapper;

	private Map<String, Node> createdNodes;

	private StateInfo lastView;

	private Snapshot snapshot;

	private Random random;

	private TreeMap<Long, Set<String>> timeToWake;

	private T appConfig;

	public AbstractDeploymentAndExecutionSimulator(T appConfig) {
		this.appConfig = appConfig;
		this.createdNodes = new HashMap<String, Node>();
		this.objectMapper = new ObjectMapper();
		this.random = new Random();
		this.lastView = new StateInfo();
		this.snapshot = new Snapshot();
		timeToWake = new TreeMap<Long, Set<String>>();
		Timer timer = new Timer(true);
		timer.schedule(new WakeUpPaused(lastView, timeToWake), 1000, 1600);
	}

	public T getAppConfig() {
		return appConfig;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public Map<String, Node> getCreatedNodes() {
		return createdNodes;
	}

	public Random getRandom() {
		return random;
	}

	protected Node getRandomNode() {
		if(createdNodes.size() == 0) return null;
		int index = random.nextInt(createdNodes.size());

		Iterator<Node> iterator = createdNodes.values().iterator();
		int i = 0;
		while(iterator.hasNext()) {
			if(i == index) return iterator.next();
			iterator.next();
			i++;
		}
		return null;
	}
		
	public Snapshot deploy(File file) throws Exception {

		JsonNode node = objectMapper.readTree(file);

		JsonNode config = node.get("config");
		readConfig(config);
		
		int interval = Integer.parseInt(appConfig.getProp(Literals.ITERATION_INTERVAL_MS));

		JsonNode procedure = node.get("procedure");

		Iterator<JsonNode> iterator = procedure.iterator();
		while(iterator.hasNext()) {
			JsonNode value = iterator.next();
			action(value, config);
			int waitTime = MathOp.waitTime(createdNodes.size(), interval);
			Thread.sleep(waitTime);
			snapshot();
		}

		return snapshot; 
	}
	
	private void action(JsonNode node, JsonNode config) throws Exception {

		JsonNode data = node.get("data");
		String type = node.get("type").asText(); 
		switch(type) {
		case "node": {
			Node createdNode = createAndLaunchNode(data, config); 
			lastView.addNode(createdNode.getId());
			break;			
		}
		case "pause": {
			pause(data); 
			break;
		}
		case "unsubscribe": {
			unsubscribe(data); 
			break;		
		}
		}

	}


	protected abstract void readConfig(JsonNode config) throws Exception;

	protected abstract Node createAndLaunchNode(JsonNode data, JsonNode config) throws Exception;

	public abstract void undeploy();

	protected void snapshot() {

		snapshot.addExpected(lastView.snapshot());
		for(Node node : getCreatedNodes().values()) {
			StateInfo stateInfo = RestClient.getStateInfo(node);
			snapshot.addResult(node, stateInfo);
		}

	}

	private void pause(JsonNode data)  {
		String nodeId = data.get("node.id").asText();

		long time = data.get("time").asLong();		
		Node node = createdNodes.get(nodeId);
		assert(RestClient.pause(node, time));

		Set<String> scheduled = timeToWake.get(time);
		if(scheduled == null) scheduled = new HashSet<String>();
		scheduled.add(nodeId);

		timeToWake.put(System.currentTimeMillis() + time, scheduled);
		lastView.sleep(nodeId);

	}

	private void unsubscribe(JsonNode data)  {
		String nodeId = data.asText();
		Node node = createdNodes.get(nodeId);
		assert(RestClient.unsubscribe(node));
	}

}
