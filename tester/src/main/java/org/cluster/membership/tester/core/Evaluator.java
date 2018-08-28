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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.common.debug.StateInfo;
import org.cluster.membership.common.model.Node;
import org.cluster.membership.tester.Config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Evaluator {

	private ObjectMapper objectMapper;

	private Map<String, Node> createdNodes;

	private Random random;

	private Logger logger = Logger.getLogger(Evaluator.class.getName());

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
		List<Process> processes  = new ArrayList<Process>();
		boolean testPassed = true;
		while(iterator.hasNext()) {
			JsonNode value = iterator.next();
			boolean success = action(value, config, processes);
			if(!success) {
				testPassed = false;
				logger.log(Level.SEVERE, "the step: '" + value.toString() + "', has failed to exeute successfuly");
				break;
			}
		}
		for(Process p : processes) p.destroy();		

		return testPassed; 
	}
	
	private boolean action(JsonNode node, JsonNode config, List<Process> processes) throws Exception {

		JsonNode data = node.get("data");
		String type = node.get("type").asText(); 
		//System.out.println(type);
		switch(type) {
			case "node":  processes.add(createAndLaunchNode(data, config)); break;			
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

			Config.updateConfig(Config.templateFolder, key, value);
		}

	}
	
	private String generateNodeCommandLineArguments(Node node, int idx) {
		return " --id." + idx + "=" + node.getId() +
				" --address." + idx + "=" + node.getAddress() +
				" --protocol.port." + idx + "=" + node.getProtocolPort() +
				" --server.port." + idx + "=" + node.getServicePort() +
				" --time.zone." + idx + "=" + node.getTimeZone().getID();
	}

	private Process createAndLaunchNode(JsonNode data, JsonNode config) throws Exception {		
		String id = data.get("id").asText();
		String address = data.get("address").asText();
		int nodePort = data.get("node-port").asInt();
		int servicePort = data.get("service-port").asInt();
		String timeZone = data.get("time-zone").asText();

		Node node = new Node(id, address, nodePort, servicePort, timeZone);
		
		Config.newInstance(id);

		Config.updateConfig(id, "id", id);
		Config.updateConfig(id, "address", address);
		Config.updateConfig(id, "protocol.port", String.valueOf(nodePort));
		Config.updateConfig(id, "server.port", String.valueOf(servicePort));
		Config.updateConfig(id, "time.zone", timeZone);

		Node commandLineParam = createdNodes.size() > 0 ? getRandomNode() : null;		
		String args = commandLineParam != null ? generateNodeCommandLineArguments(commandLineParam,1) : "";		
		String command = "java -jar " + Config.programPath(id) + " " + args.trim() + " --mode=DEBUG";
		
		ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
		processBuilder.directory(Config.cd(id));
		processBuilder.redirectErrorStream(true);
		processBuilder.redirectOutput(Config.logPath(id));		
		createdNodes.put(id, node);
		
		return processBuilder.start();

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

	private boolean check(JsonNode data) {

		List<String> nodes = iteratorToList(data.get("nodes").iterator());
		List<String> deads = iteratorToList(data.get("dead-nodes").iterator());
		List<String> failing = iteratorToList(data.get("failing-nodes").iterator());

		boolean success = true;
		for(Node node : createdNodes.values()) {
			StateInfo deb = HttpClient.nodes(node);
			
			if(differentLists(nodes, deb.getNodes())) {
				logger.log(Level.SEVERE, "error comparing \"cluster nodes\" " + node.getId() + " against current state");
				logger.info("expected: " + listToString(nodes));
				logger.info("result: " + listToString(deb.getNodes()));
				success = false;
			}
			if(differentLists(deads, deb.getDead())) {
				logger.log(Level.SEVERE, "error comparing \"dead nodes\" " + node.getId() + " against current state");
				logger.info("expected: " + listToString(deads));
				logger.info("result: " + listToString(deb.getDead()));
				success = false;
			}
			if(differentLists(failing, deb.getFailing())) {
				logger.log(Level.SEVERE, "error comparing \"failing nodes\" " + node.getId() + " against current state");
				logger.info("expected: " + listToString(failing));
				logger.info("result: " + listToString(deb.getFailing()));
				success = false;
			}
		}
		return success;

	}
	
	private String listToString(List<String> list) {
		StringBuilder strList = new StringBuilder();
		Collections.sort(list);		
		for(String s: list) strList.append(s + " ");		
		return strList.toString();
	}
	
	private boolean differentLists(List<String> a, List<String> b) {
		if(a.size() != b.size()) return true;
		
		Collections.sort(a);
		Collections.sort(b);
		
		for(int i = 0; i < a.size(); i++)
			if(!a.get(i).equals(b.get(i))) 
				return true;
			
		return false;	
	}

	private List<String> iteratorToList(Iterator<JsonNode> iterator) {
		List<String> list = new ArrayList<String>();		
		while(iterator.hasNext()) {
			JsonNode current = iterator.next();
			list.add(current.asText());			
		}		
		return list;
	}


}
