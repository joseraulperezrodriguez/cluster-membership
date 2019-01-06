package org.cluster.membership.tester.deploy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.EnvUtils;
import org.cluster.membership.common.model.util.Tuple2;
import org.cluster.membership.tester.config.AbstractEnvConfig;

import com.fasterxml.jackson.databind.JsonNode;


public class MultipleVMDeploymentAndExecutionSimulator extends AbstractDeploymentAndExecutionSimulator {


	//private Logger logger = Logger.getLogger(MultipleVMDeploymentSimulator.class.getName());
	
	private List<Process> runningProcess;
	
	private String killToken;
	
	public MultipleVMDeploymentAndExecutionSimulator(AbstractEnvConfig appConfig) {
		super(appConfig);
		this.runningProcess = new ArrayList<Process>();
		this.killToken = UUID.randomUUID().toString();
	}
	
	public List<Process> getRunningProcesses() {
		return runningProcess;
	}

	protected Node createAndLaunchNode(JsonNode data, JsonNode config) throws Exception {		
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
		String command = "java -jar -Xmx1G " + getAppConfig().programPath(id) + " " + args.trim() + " --mode=DEBUG --kill=" + killToken;
		
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
		
		return node;
		
	}

	/*protected boolean snapshot(JsonNode data) throws Exception {

		Set<String> nodes = iteratorToList(data.get("nodes").iterator());
		Set<String> suspecting = iteratorToList(data.get("suspecting").iterator());
		Set<String> failing = iteratorToList(data.get("failing").iterator());
		
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
				if(differentLists(suspecting, deb.getFailing())) {
					if(i == tryTimes) {
						logger.log(Level.SEVERE, "error comparing \"suspecting nodes\" " + node.getId() + " against current state");
						logger.info("expected: " + listToString(suspecting));
						logger.info("result: " + listToString(deb.getFailing()));
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

	}*/
	
	/*private String listToString(Set<String> list) {
		StringBuilder strList = new StringBuilder();		
		for(String s: list) strList.append(s + " ");		
		return strList.toString();
	}
	
	private boolean differentLists(Set<String> a, Set<String> b) {
		if(a.size() != b.size()) return true;		
		for(String s : a) {
			if(!b.contains(s)) {
				return true;
			}
		}
		return false;	
	}

	private Set<String> iteratorToList(Iterator<JsonNode> iterator) {
		Set<String> list = new HashSet<String>();		
		while(iterator.hasNext()) {
			JsonNode current = iterator.next();
			list.add(current.asText());			
		}		
		return list;
	}*/
		
	@Override
	public void undeploy() {
		String command = "pkill -f " + killToken;
		ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
		try {
			processBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
