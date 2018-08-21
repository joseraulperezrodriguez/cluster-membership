package org.cluster.membership.tester;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Hello world!
 *
 */

@SpringBootApplication
public class TesterEntry implements ApplicationRunner {
    
	public static void main( String[] args ) {
    	new SpringApplicationBuilder(TesterEntry.class)
                //.properties("spring.config.name:app")
                .build()
                .run(args);
    	
    	
    }
	
	@Override
    public void run(ApplicationArguments args) throws Exception {
	
    	String pathToProgram = args.getOptionValues("program-path").get(0);
    	boolean runsGenerated = Boolean.parseBoolean(args.getOptionValues("runs-generated").get(0));
    	if(runsGenerated) {
    		
    	}
    	Config.read(pathToProgram);
    	
		
    }
	
}
