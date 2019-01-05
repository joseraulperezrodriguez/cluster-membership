package org.cluster.membership.tester.deploy;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.tester.config.AbstractEnvConfig;
import org.cluster.membership.tester.core.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public abstract class AbstractDeploymentSimulator {

	private ObjectMapper objectMapper;

	private Map<String, Node> createdNodes;

	private Random random;

	private Logger logger = Logger.getLogger(AbstractDeploymentSimulator.class.getName());

	private AbstractEnvConfig appConfig;
	
	public AbstractDeploymentSimulator(AbstractEnvConfig appConfig) {
		this.appConfig = appConfig;
		createdNodes = new HashMap<String, Node>();
		objectMapper = new ObjectMapper();
		random = new Random();
	}
	
	public AbstractEnvConfig getAppConfig() {
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
	
	public boolean evaluate(File file) throws Exception {

		JsonNode node = objectMapper.readTree(file);

		JsonNode config = node.get("config");
		readConfig(config);		

		JsonNode procedure = node.get("procedure");

		Iterator<JsonNode> iterator = procedure.iterator();
		boolean testPassed = true;
		while(iterator.hasNext()) {
			JsonNode value = iterator.next();
			boolean success = action(value, config);
			if(!success) {
				testPassed = false;
				logger.log(Level.SEVERE, "the step: '" + value.toString() + "', has failed to exeute successfuly");
				break;
			}
		}

		return testPassed; 
	}
	
	private boolean action(JsonNode node, JsonNode config) throws Exception {

		JsonNode data = node.get("data");
		String type = node.get("type").asText(); 
		//System.out.println(type);
		switch(type) {
			case "node": createAndLaunchNode(data, config); break;			
			case "wait":  wait(data); break;
			case "pause":  pause(data); break;
			case "unsubscribe":  unsubscribe(data); break;
			case "check": { 
				boolean success = check(data);
				if(!success) return false;
				break;
			
			}
		}
		return true;

	}


	private void readConfig(JsonNode config) throws Exception {

		Iterator<Entry<String, JsonNode>> iterator = config.fields();
		while(iterator.hasNext()) {
			Entry<String, JsonNode> entry = iterator.next();
			String key = entry.getKey();
			String value = entry.getValue().toString();

			appConfig.updateConfig(AbstractEnvConfig.templateFolder, key, value);
		}

	}
	
	protected abstract void createAndLaunchNode(JsonNode data, JsonNode config) throws Exception;
	
	public abstract void undeploy();

	private void wait(JsonNode data) throws Exception {
		long time = data.asLong() * 1000;		
		Thread.sleep(time);
	}

	private void pause(JsonNode data)  {
		String nodeId = data.get("node.id").asText(); 
		long time = data.get("time").asLong();		
		Node node = createdNodes.get(nodeId);
		assert(RestClient.pause(node, time));
	}

	private void unsubscribe(JsonNode data)  {
		String nodeId = data.asText();
		Node node = createdNodes.get(nodeId);
		assert(RestClient.unsubscribe(node));
	}

	protected abstract boolean check(JsonNode data) throws Exception;
	

}
