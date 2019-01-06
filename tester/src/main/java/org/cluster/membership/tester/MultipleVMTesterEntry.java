package org.cluster.membership.tester;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MultipleVMTesterEntry implements ApplicationRunner {
    
	public static void main( String[] args ) {
    	new SpringApplicationBuilder(MultipleVMTesterEntry.class)
                //.properties("spring.config.name:app")
    			.web(WebApplicationType.NONE)
                .build()
                .run(args);
    	
    }
	
	@Override
    public void run(ApplicationArguments args) throws Exception {	
    	/*String pathToProgram = args.getOptionValues("program-path").get(0);
    	String homePath = EnvUtils.getHomePath(MultipleVMTesterEntry.class, AbstractEnvConfig.configFolder);
    	AbstractEnvConfig config = new MultipleVMEnvConfig(homePath, pathToProgram);
    	IEvaluator evaluator = new BasicEvaluator();
    	AbstractRunner runner = new MultipleVMRunner(config, evaluator);
    	runner.runTemplates();*/
    }
	
}
