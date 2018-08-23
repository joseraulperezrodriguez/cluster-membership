package org.cluster.membership.tester.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.cluster.membership.tester.Config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Evaluator {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static boolean evaluate(File file) throws Exception {
		
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
	
	private static void updateConfigFile(String configFile, String property, String value) throws Exception {
		Properties p = new Properties();
		p.load(new FileInputStream(configFile));		
		p.setProperty(property, value);		
		p.store(new FileOutputStream(configFile), "");		
	}
	
	private static void readConfig(JsonNode config) throws Exception {
		
		Iterator<Entry<String, JsonNode>> iterator = config.fields();
		while(iterator.hasNext()) {
			Entry<String, JsonNode> entry = iterator.next();
			String key = entry.getKey();
			String value = entry.getValue().toString();
			
			updateConfigFile(Config.appPropertiesPath, key, value);
		}
		
	}
	
	
	private static void action(JsonNode node, JsonNode config) throws Exception {
		
		JsonNode data = node.get("data");
		String type = node.get("type").asText(); 
		//System.out.println(type);
		switch(type) {
			case "node":  createAndLaunchNode(data, config); break;			
			case "wait":  wait(data); break;
			case "pause":  pause(data); break;
			case "check":  check(data); break;
		}
		
	}
	
	private static void createAndLaunchNode(JsonNode data, JsonNode config) throws Exception {
		
		String id = data.get("id").asText();
		String address = data.get("address").asText();
		int nodePort = data.get("node-port").asInt();
		int servicePort = data.get("service-port").asInt();
		String timeZone = data.get("time-zone").asText();
		
		updateConfigFile(Config.appPropertiesPath, "server.port", String.valueOf(servicePort));
		
		updateConfigFile(Config.peerPropertiesPath, "id", id);
		updateConfigFile(Config.peerPropertiesPath, "address", address);
		updateConfigFile(Config.peerPropertiesPath, "port", String.valueOf(nodePort));
		updateConfigFile(Config.peerPropertiesPath, "time-zone", timeZone);
		
		//TODO launch the command line 
		
	}
	
	private static void wait(JsonNode data) throws Exception {
		long time = data.asLong();		
		Thread.sleep(time);
	}
	
	private static void pause(JsonNode data)  {
		String node = data.get("node").asText(); 
		long time = data.get("time").asLong();
		
		//TODO set sleep message
	}
	
	private static void check(JsonNode data) {
		
		List<String> nodes = iteratorToList(data.get("nodes").iterator());
		List<String> deads = iteratorToList(data.get("dead-nodes").iterator());
		List<String> failing = iteratorToList(data.get("failing-nodes").iterator());
		
		//TODO
		
	}
	
	private static List<String> iteratorToList(Iterator<JsonNode> iterator) {
		List<String> list = new ArrayList<String>();		
		while(iterator.hasNext()) {
			JsonNode current = iterator.next();
			list.add(current.toString());			
		}		
		return list;
	}
	

}
