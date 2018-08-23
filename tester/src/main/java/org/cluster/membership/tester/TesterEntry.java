package org.cluster.membership.tester;

import org.cluster.membership.tester.core.Runner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
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
    			.web(WebApplicationType.NONE)
                .build()
                .run(args);
    	
    	
    }
	
	@Override
    public void run(ApplicationArguments args) throws Exception {
	
    	String pathToProgram = args.getOptionValues("program-path").get(0);    			
		//String pathToProgram = "";
    	Config.prepareEnvironment(pathToProgram);
    	
		Runner.runTemplates();
    }
	
}
