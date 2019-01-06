package org.cluster.membership.tester.deploy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.EnvUtils;
import org.cluster.membership.common.model.util.Tuple2;
import org.cluster.membership.tester.config.LocalEnvConfig;
import org.cluster.membership.tester.config.MultipleVMEnvConfig;

import com.fasterxml.jackson.databind.JsonNode;


public class MultipleVMDeploymentAndExecutionSimulator extends AbstractDeploymentAndExecutionSimulator<MultipleVMEnvConfig> {


	//private Logger logger = Logger.getLogger(MultipleVMDeploymentSimulator.class.getName());
	
	private List<Process> runningProcess;
	
	private String killToken;
	
	public MultipleVMDeploymentAndExecutionSimulator(MultipleVMEnvConfig appConfig) {
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
	
	protected void readConfig(JsonNode config) throws Exception {

		Iterator<Entry<String, JsonNode>> iterator = config.fields();
		while(iterator.hasNext()) {
			Entry<String, JsonNode> entry = iterator.next();
			String key = entry.getKey();
			String value = entry.getValue().toString();

			getAppConfig().updateConfig(LocalEnvConfig.templateFolder, key, value);
		}

	}

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
