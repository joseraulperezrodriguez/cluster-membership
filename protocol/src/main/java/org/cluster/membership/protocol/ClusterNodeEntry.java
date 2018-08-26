package org.cluster.membership.protocol;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.protocol.core.ClusterView;
import org.cluster.membership.protocol.model.ClusterData;
import org.cluster.membership.protocol.model.Node;
import org.cluster.membership.protocol.net.RestClient;
import org.cluster.membership.protocol.net.core.MembershipServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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
	
	public static ConfigurableApplicationContext applicationContext;
	
    public static void main( String[] args ) {
    	String argsString = ""; for(String s : args) argsString += s + " ";
    	logger.info("started program with args: " + argsString);
    	
    	applicationContext = new SpringApplicationBuilder(ClusterNodeEntry.class)
                .properties("spring.config.name:app")
                .build()
                .run(args);
    }
        
    @Autowired
    private MembershipServer membershipServer;
        
    @Autowired
    private ClusterView clusterView;
    
    @Autowired
    private RestClient restClient;
        
    @Override
    public void run(ApplicationArguments args) throws Exception {
    	logger.info("node started in peer: " + Config.THIS_PEER);
    	
    	Config.read(args);    	
    	assert(Config.isValid());
    	
    	membershipServer.listen();
    	
    	clusterView.init();
    	
    	if(Config.SEEDS.size() > 0) {
    		for(Node nd: Config.SEEDS.list()) {
    			logger.info("trying to subscribe against node: " + nd);
    			ClusterData view = restClient.subscribe(nd, Config.THIS_PEER);
    			if(view == null) continue;     			
    			logger.info("subscribed successfuly against node: " + nd + " with view " + view);    			
    			clusterView.updateMyView(view);
    			return;
    		}
    		logger.log(Level.SEVERE, "all seeds failed");
        	throw new Exception("Not able to complete subscription in any seed node");
    	}
		
    }
    
}
