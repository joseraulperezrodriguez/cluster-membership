package org.cluster.membership;

import java.util.ArrayList;
import java.util.List;

import org.cluster.membership.core.ClusterView;
import org.cluster.membership.core.MessageType;
import org.cluster.membership.model.Message;
import org.cluster.membership.net.ResponseHandler;
import org.cluster.membership.net.core.MembershipClient;
import org.cluster.membership.net.core.MembershipClientHandler;
import org.cluster.membership.net.core.MembershipDirectClientHandler;
import org.cluster.membership.net.core.MembershipServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry Point!
 *
 */
@SpringBootApplication
@EnableScheduling
public class ClusterNodeEntry implements ApplicationRunner {
	
	
    public static void main( String[] args ) {
    	//SpringApplication.run(ClusterNodeEntry.class, args).getEnvironment().getP;
    	new SpringApplicationBuilder(ClusterNodeEntry.class)
                .properties("spring.config.name:app")
                .build()
                .run(args);
    	
    }
    
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ClusterView clusterView() {
    	return new ClusterView();
    }
    
    @Autowired
    private MembershipServer membershipServer;
    
    @Autowired
    private ResponseHandler responseHandler;
        
    @Override
    public void run(ApplicationArguments args) throws Exception {
	
    	Config.read(args);    	
    	assert(Config.isValid());
    	
    	membershipServer.listen();
    	
    	if(Config.SEEDS.size() > 0) {
    		Message subscriptionMessage = new Message(MessageType.SUBSCRIPTION, Config.SEEDS.get(0), 1);
    		List<Message> one = new ArrayList<Message>();
    		one.add(subscriptionMessage);
    		MembershipClientHandler handler = new MembershipDirectClientHandler(responseHandler, subscriptionMessage.getNode(), one);
    		MembershipClient.connect(subscriptionMessage.getNode(), handler);
    	}
		
    }
    
}
