package org.cluster.membership.tester;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.tester.config.MultipleVMEnvConfig;
import org.cluster.membership.tester.core.IEvaluator;
import org.cluster.membership.tester.core.Snapshot;
import org.cluster.membership.tester.deploy.MultipleVMDeploymentAndExecutionSimulator;

public class MultipleVMRunner extends AbstractRunner<MultipleVMEnvConfig> {
	
	private Logger logger = Logger.getLogger(MultipleVMRunner.class.getName());	
	
	public MultipleVMRunner(MultipleVMEnvConfig appConfig, IEvaluator evaluator) {
		super(appConfig, evaluator);
	}
		
	public void runTemplates() throws Exception {
		File cases = new File(getAppConfig().getCasesPath());
				
		File[] sortedByName = cases.listFiles();
		Arrays.sort(sortedByName, (a, b) -> a.getName().compareTo(b.getName()));
		for(File f: sortedByName) {
			MultipleVMDeploymentAndExecutionSimulator deployment = new MultipleVMDeploymentAndExecutionSimulator(getAppConfig());			
			try {
				//boolean success = new MultipleVMDeploymentSimulator(getAppConfig()).deploy(f);
				Snapshot snapshot = deployment.deploy(f);
				Double success = getEvaluator().evaluate(snapshot);
				
				String message = "FAILED test for file " + f.getName();
				assertEquals(message, 1, success, 0.5);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "FAILED test for file " + f.getName());
				logger.log(Level.SEVERE, "error trace below:");
				e.printStackTrace();
			}
			deployment.undeploy();
		}
		
	}
	
}
