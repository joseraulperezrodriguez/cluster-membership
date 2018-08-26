package org.cluster.membership.tester.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.cluster.membership.tester.Config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Evaluator {

	private ObjectMapper objectMapper;

	private Map<String, Node> createdNodes;

	private Random random;


	public Evaluator() {
		createdNodes = new HashMap<String, Node>();
		objectMapper = new ObjectMapper();
		random = new Random();

	}

	private Node getRandomNode() {
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

		while(iterator.hasNext()) {
			JsonNode value = iterator.next();
			action(value, config);			
		}

		return false; 
	}
	
	private void action(JsonNode node, JsonNode config) throws Exception {

		JsonNode data = node.get("data");
		String type = node.get("type").asText(); 
		//System.out.println(type);
		switch(type) {
		case "node":  createAndLaunchNode(data, config); break;			
		case "wait":  wait(data); break;
		/*case "pause":  pause(data); break;
		case "unsubscribe":  unsubscribe(data); break;
		case "check":  check(data); break;*/
		}
		System.out.println(type + " executed");

	}


	private void readConfig(JsonNode config) throws Exception {

		Iterator<Entry<String, JsonNode>> iterator = config.fields();
		while(iterator.hasNext()) {
			Entry<String, JsonNode> entry = iterator.next();
			String key = entry.getKey();
			String value = entry.getValue().toString();

			Config.updateConfig(Config.templateFolder,Config.appProperties, key, value);
		}

	}
	

	private void createAndLaunchNode(JsonNode data, JsonNode config) throws Exception {		
		String id = data.get("id").asText();
		String address = data.get("address").asText();
		int nodePort = data.get("node-port").asInt();
		int servicePort = data.get("service-port").asInt();
		String timeZone = data.get("time-zone").asText();

		Node node = new Node(id, address, nodePort, servicePort, timeZone);
		
		Config.newInstance(id);

		Config.updateConfig(id, Config.appProperties, "server.port", String.valueOf(servicePort));

		Config.updateConfig(id, Config.peerProperties, "id", id);
		Config.updateConfig(id, Config.peerProperties, "address", address);
		Config.updateConfig(id, Config.peerProperties, "port", String.valueOf(nodePort));
		Config.updateConfig(id, Config.peerProperties, "time-zone", timeZone);

		Node commandLineParam = createdNodes.size() > 0 ? getRandomNode() : null;		
		String args = commandLineParam != null ? commandLineParam.commandLineParamString(1) : "";		
		String command = "java -jar " + Config.programPath(id) + " " + args.trim() + " --mode=DEBUG";	
		System.out.println(command);
		
		ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
		processBuilder.directory(Config.cd(id));
		processBuilder.redirectErrorStream(true);
		processBuilder.redirectOutput(Config.logPath(id));
		processBuilder.start();
		createdNodes.put(id, node);

	}

	private void wait(JsonNode data) throws Exception {
		long time = data.asLong();		
		Thread.sleep(time);
	}

	private void pause(JsonNode data)  {
		String nodeId = data.get("node-id").asText(); 
		long time = data.get("time").asLong();		
		Node node = createdNodes.get(nodeId);
		assert(HttpClient.pause(node, time));
	}

	private void unsubscribe(JsonNode data)  {
		String nodeId = data.asText();
		Node node = createdNodes.get(nodeId);
		assert(HttpClient.unsubscribe(node));
	}

	private void check(JsonNode data) {

		List<String> nodes = iteratorToList(data.get("nodes").iterator());
		List<String> deads = iteratorToList(data.get("dead-nodes").iterator());
		List<String> failing = iteratorToList(data.get("failing-nodes").iterator());

		for(Node node : createdNodes.values()) {
			NodesDebug deb = HttpClient.nodes(node);
			assert(equalLists(nodes, deb.getNodes()));
			assert(equalLists(deads, deb.getDead()));
			assert(equalLists(failing, deb.getFailing()));						
		}

	}
	
	private boolean equalLists(List<String> a, List<String> b) {
		if(a.size() != b.size()) return false;
		
		Collections.sort(a);
		Collections.sort(b);
		
		for(int i = 0; i < a.size(); i++)
			if(a.get(i) != b.get(i)) 
				return false;
			
		return true;	
	}

	private List<String> iteratorToList(Iterator<JsonNode> iterator) {
		List<String> list = new ArrayList<String>();		
		while(iterator.hasNext()) {
			JsonNode current = iterator.next();
			list.add(current.toString());			
		}		
		return list;
	}


}
