package org.cluster.membership.tester.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.common.debug.StateInfo;
import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.EnvUtils;
import org.cluster.membership.common.model.util.Tuple2;
import org.cluster.membership.tester.config.AbstractEnvConfig;
import org.cluster.membership.tester.core.RestClient;

import com.fasterxml.jackson.databind.JsonNode;


public class MultipleVMDeploymentSimulator extends AbstractDeploymentSimulator {


	private Logger logger = Logger.getLogger(MultipleVMDeploymentSimulator.class.getName());
	
	private List<Process> runningProcess;
	
	public MultipleVMDeploymentSimulator(AbstractEnvConfig appConfig) {
		super(appConfig);
		this.runningProcess = new ArrayList<Process>();
	}
	
	public List<Process> getRunningProcesses() {
		return runningProcess;
	}

	protected void createAndLaunchNode(JsonNode data, JsonNode config) throws Exception {		
		String id = data.get("id").asText();
		String address = data.get("address").asText();
		
		Tuple2<Integer, Integer> ports = getAppConfig().getPorts();
		assert(ports != null);
		
		int protocolPort = ports.getB();
		int servicePort = ports.getA();
		
		//int nodePort = data.get("protocol.port").asInt();
		//int servicePort = data.get("server.port").asInt();
		String timeZone = data.get("time.zone").asText();

		Node node = new Node(id, address, protocolPort, servicePort, timeZone);
		
		getAppConfig().newInstance(id);

		getAppConfig().updateConfig(id, "id", id);
		getAppConfig().updateConfig(id, "address", address);
		getAppConfig().updateConfig(id, "protocol.port", String.valueOf(protocolPort));
		getAppConfig().updateConfig(id, "server.port", String.valueOf(servicePort));
		getAppConfig().updateConfig(id, "time.zone", timeZone);

		Node commandLineParam = getCreatedNodes().size() > 0 ? getRandomNode() : null;		
		String args = commandLineParam != null ? EnvUtils.generateNodeCommandLineArguments(commandLineParam,1) : "";		
		String command = "java -jar " + getAppConfig().programPath(id) + " " + args.trim() + " --mode=DEBUG";
		
		ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
		processBuilder.directory(getAppConfig().cd(id));
		processBuilder.redirectErrorStream(true);
		processBuilder.redirectOutput(getAppConfig().logPath(id));		
		getCreatedNodes().put(id, node);
		
		Process p = processBuilder.start();
		runningProcess.add(p);
		
		do {
			Thread.sleep(1000);			
		} while(!EnvUtils.isListening(address, servicePort) || 
				!EnvUtils.isListening(address, protocolPort));
		
	}

	protected boolean check(JsonNode data) throws Exception {

		List<String> nodes = iteratorToList(data.get("nodes").iterator());
		List<String> suspecting = iteratorToList(data.get("suspecting").iterator());
		List<String> failing = iteratorToList(data.get("failing").iterator());
		
		int tryDelay = data.get("try.delay").asInt();
		int tryInterval = data.get("try.interval").asInt();
		int tryTimes = data.get("try.times").asInt();
		
		Thread.sleep(tryDelay * 1000);
		for(int i = 1; i <= tryTimes; i++) {
			Thread.sleep(tryInterval * 1000);
			boolean success = true;
			for(Node node : getCreatedNodes().values()) {
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

	private void killProcesses() {
		for(Process p : runningProcess) p.destroyForcibly();				
		runningProcess.clear();
	}
		
	@Override
	public void undeploy() {
		killProcesses();		
	}


}
