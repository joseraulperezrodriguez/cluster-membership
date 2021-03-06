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
import org.cluster.membership.tester.core.RestClient;

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
		
		Node node = new Node(id, address, protocolPort, servicePort);
		
		getAppConfig().newInstance(id);

		getAppConfig().updateConfigInstance(id, Literals.NODE_ID, id);
		getAppConfig().updateConfigInstance(id, Literals.NODE_ADDRESS, address);
		getAppConfig().updateConfigInstance(id, Literals.NODE_PROTOCOL_PORT, String.valueOf(protocolPort));
		getAppConfig().updateConfigInstance(id, Literals.NODE_SERVER_PORT, String.valueOf(servicePort));

		String instanceHome = getAppConfig().getInstancesContainer() + File.separator + node.getId();
		
		Node commandLineParam = getCreatedNodes().size() > 0 ? getRandomNode() : null;		
		String args = (commandLineParam != null ? EnvUtils.generateNodeCommandLineArguments(commandLineParam,1) : "") +
			 " --" + Literals.APP_MODE + "=" + Literals.APP_TEST_MODE + 
			 " --" + Literals.APP_HOME + "=" + instanceHome;	
		String[] argsA = args.split("\\s+");
		
		ClusterNodeEntry.main(argsA);
		
		EnvUtils.waitUntilStarted(address, servicePort, protocolPort);		
		getCreatedNodes().put(id, node);
		
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
		for(Node nd : getCreatedNodes().values()) 
			RestClient.shutdown(nd, 2);			
	}


}
