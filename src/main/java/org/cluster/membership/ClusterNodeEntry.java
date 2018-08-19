package org.cluster.membership;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry Point!
 *
 */
@SpringBootApplication
public class ClusterNodeEntry implements ApplicationRunner {
	
	
    public static void main( String[] args ) {
    	SpringApplication.run(ClusterNodeEntry.class, args);
    }
        
    @Override
    public void run(ApplicationArguments args) throws Exception {
	
    	Config.read(args);    	
    	
    	
		
    }
    
}
