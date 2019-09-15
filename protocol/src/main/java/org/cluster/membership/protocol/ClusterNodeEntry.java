package org.cluster.membership.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.Config.Parsing;
import org.cluster.membership.protocol.core.ClusterView;
import org.cluster.membership.protocol.core.Global;
import org.cluster.membership.protocol.model.ClusterData;
import org.cluster.membership.protocol.model.SynchroObject;
import org.cluster.membership.protocol.net.MembershipServer;
import org.cluster.membership.protocol.services.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry Point!
 *
 */
@SpringBootApplication
@EnableScheduling
public class ClusterNodeEntry implements ApplicationRunner {
	
	private static Logger logger = Logger.getLogger(ClusterNodeEntry.class.getName());
	
	public static Map<Node, ConfigurableApplicationContext> applicationContexts = new HashMap<Node, ConfigurableApplicationContext>();
	
	public static Properties properties;
	public static ApplicationArguments appArguments;
	
	public static void main( String[] args ) throws Exception {
    	
    	String argsString = ""; for(String s : args) argsString += s + " ";
    	logger.info("started program with args: " + argsString);
    	
    	appArguments = new DefaultApplicationArguments(args);
        properties = Config.read(appArguments);    	
    	assert(Config.isValid(properties, appArguments));
    	    	
    	Node thisNode = Parsing.readThisPeer(properties, Parsing.getHome(appArguments));
    	applicationContexts.put(thisNode, new SpringApplicationBuilder(ClusterNodeEntry.class)
                //.properties("spring.config.name:app")
    			.properties(properties)
                .build()
                .run(args));
    }
        
    @Autowired
    private MembershipServer membershipServer;
    
    @Autowired
    private Config config;
        
    @Autowired
    private ClusterView clusterView;
    
    @Autowired
    private RestClient restClient;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
    	logger.info("node started in peer: " + config.getThisPeer());
    	
    	membershipServer.listen();
    	
    	clusterView.init();

    	if(config.getSeeds().size() > 0) {
    		for(Node nd: config.getSeeds().list()) {
    			logger.info("trying to subscribe against node: " + nd);
    			ClusterData view = restClient.subscribe(nd, config.getThisPeer());
    			if(view == null) continue;
    			logger.info("subscribed successfuly against node: " + nd + " with view " + view);
    			clusterView.updateMyView(new SynchroObject(view));
    			return;
    		}
    		logger.log(Level.SEVERE, "all seeds failed");
    		Global.shutdown(applicationContexts.get(config.getThisPeer()), 5);
        	throw new Exception("Not able to complete subscription in any seed node");
        	
    	}
		
    }
    
}
