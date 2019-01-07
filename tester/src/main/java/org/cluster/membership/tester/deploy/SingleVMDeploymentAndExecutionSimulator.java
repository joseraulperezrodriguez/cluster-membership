package org.cluster.membership.tester.deploy;

import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.EnvUtils;
import org.cluster.membership.common.model.util.Literals;
import org.cluster.membership.common.model.util.Tuple2;
import org.cluster.membership.protocol.ClusterNodeEntry;
import org.cluster.membership.tester.config.LocalEnvConfig;

import com.fasterxml.jackson.databind.JsonNode;


public class SingleVMDeploymentAndExecutionSimulator extends AbstractDeploymentAndExecutionSimulator<LocalEnvConfig> {


	//private Logger logger = Logger.getLogger(MultipleVMDeploymentSimulator.class.getName());
	
	public SingleVMDeploymentAndExecutionSimulator(LocalEnvConfig appConfig) {
		super(appConfig);
	}
		
	protected Node createAndLaunchNode(JsonNode data, JsonNode config) throws Exception {		
		String id = data.get(Literals.NODE_ID).asText();
		String address = data.get(Literals.NODE_ADDRESS).asText();
		
		Tuple2<Integer, Integer> ports = getAppConfig().getPorts();
		assert(ports != null);
		
		int protocolPort = ports.getB();
		int servicePort = ports.getA();
		
		String timeZone = data.get(Literals.NODE_TIME_ZONE).asText();

		Node node = new Node(id, address, protocolPort, servicePort, timeZone);
		
		getAppConfig().newInstance(id);

		getAppConfig().updateConfigInstance(id, Literals.NODE_ID, id);
		getAppConfig().updateConfigInstance(id, Literals.NODE_ADDRESS, address);
		getAppConfig().updateConfigInstance(id, Literals.NODE_PROTOCOL_PORT, String.valueOf(protocolPort));
		getAppConfig().updateConfigInstance(id, Literals.NODE_SERVER_PORT, String.valueOf(servicePort));
		getAppConfig().updateConfigInstance(id, Literals.NODE_TIME_ZONE, timeZone);

		String instanceHome = getAppConfig().getInstancesContainer() + File.separator + node.getId();
		
		Node commandLineParam = getCreatedNodes().size() > 0 ? getRandomNode() : null;		
		String args = commandLineParam != null ? EnvUtils.generateNodeCommandLineArguments(commandLineParam,1) : "" +
			 " --" + Literals.APP_MODE + "=" + Literals.APP_DEBUG_MODE + 
			 " --" + Literals.APP_HOME + "=" + instanceHome;	
		String[] argsA = args.split("\\s+");
		
		ClusterNodeEntry.main(argsA);
		
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

			getAppConfig().updateConfigInstance(getAppConfig().getTemplateFolder(), key, value);
		}

	}

	@Override
	public void undeploy() {
		/*String command = "pkill -f " + killToken;
		ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
		try {
			processBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}


}
