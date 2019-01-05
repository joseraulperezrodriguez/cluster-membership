package org.cluster.membership.tester.runner;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.tester.config.AbstractEnvConfig;
import org.cluster.membership.tester.deploy.MultipleVMDeploymentSimulator;

public class MultipleVMRunner extends AbstractRunner {
	
	private Logger logger = Logger.getLogger(MultipleVMRunner.class.getName());	
	
	public MultipleVMRunner(AbstractEnvConfig appConfig) {
		super(appConfig);
	}
		
	public void runTemplates() throws Exception {
		File cases = new File(getAppConfig().casesPath);
				
		File[] sortedByName = cases.listFiles();
		Arrays.sort(sortedByName, (a, b) -> a.getName().compareTo(b.getName()));
		
		for(File f: sortedByName) {
			MultipleVMDeploymentSimulator deployment = new MultipleVMDeploymentSimulator(getAppConfig());			
			try {
				boolean success = new MultipleVMDeploymentSimulator(getAppConfig()).evaluate(f);
				if(success) logger.info("PASSED test for file " + f.getName());
				else logger.log(Level.SEVERE,"FAILED test for file " + f.getName());
			} catch (Exception e) {
				logger.log(Level.SEVERE, "FAILED test for file " + f.getName());
				logger.log(Level.SEVERE, "error trace below:");
				e.printStackTrace();
			}
			deployment.undeploy();
		}
		
	}
	
}
