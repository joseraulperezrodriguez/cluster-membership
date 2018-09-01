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
import org.cluster.membership.common.model.util.Tuple2;
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

	private void createAndLaunchNode(JsonNode data, JsonNode config) throws Exception {		
		String id = data.get("id").asText();
		String address = data.get("address").asText();
		
		Tuple2<Integer, Integer> ports = Config.getPorts();
		assert(ports != null);
		
		int protocolPort = ports.getB();
		int servicePort = ports.getA();
		
		//int nodePort = data.get("protocol.port").asInt();
		//int servicePort = data.get("server.port").asInt();
		String timeZone = data.get("time.zone").asText();

		Node node = new Node(id, address, protocolPort, servicePort, timeZone);
		
		Config.newInstance(id);

		Config.updateConfig(id, "id", id);
		Config.updateConfig(id, "address", address);
		Config.updateConfig(id, "protocol.port", String.valueOf(protocolPort));
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
		
		Process p = processBuilder.start();
		Runner.runningProcess.add(p);
		
		do {
			Thread.sleep(1000);			
		} while(!Config.isListening(address, servicePort) || 
				!Config.isListening(address, protocolPort));
		
	}

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

	private boolean check(JsonNode data) throws Exception {

		List<String> nodes = iteratorToList(data.get("nodes").iterator());
		List<String> suspecting = iteratorToList(data.get("suspecting").iterator());
		List<String> failing = iteratorToList(data.get("failing").iterator());
		
		int tryInterval = data.get("try.interval").asInt();
		int tryTimes = data.get("try.times").asInt();
		
		for(int i = 1; i <= tryTimes; i++) {
			Thread.sleep(tryInterval * 1000);
			boolean success = true;
			for(Node node : createdNodes.values()) {
				StateInfo deb = RestClient.getStateInfo(node);
				
				if(differentLists(nodes, deb.getNodes())) {
					if(i == tryTimes) {
						logger.log(Level.SEVERE, "error comparing \"cluster nodes\" " + node.getId() + " against current state");
						logger.info("expected: " + listToString(nodes));
						logger.info("result: " + listToString(deb.getNodes()));
					}
					success = false;
				}
				if(differentLists(suspecting, deb.getDead())) {
					if(i == tryTimes) {
						logger.log(Level.SEVERE, "error comparing \"suspecting nodes\" " + node.getId() + " against current state");
						logger.info("expected: " + listToString(suspecting));
						logger.info("result: " + listToString(deb.getDead()));
					}
					success = false;
				}
				if(differentLists(failing, deb.getFailing())) {
					if(i == tryTimes) {
						logger.log(Level.SEVERE, "error comparing \"failing nodes\" " + node.getId() + " against current state");
						logger.info("expected: " + listToString(failing));
						logger.info("result: " + listToString(deb.getFailing()));
					}
					success = false;
				}
			}
			if(success) return true;
		}

		
		return false;

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
