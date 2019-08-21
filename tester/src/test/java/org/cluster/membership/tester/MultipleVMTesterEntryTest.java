package org.cluster.membership.tester;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.tester.config.MultipleVMEnvConfig;
import org.cluster.membership.tester.core.BasicEvaluator;
import org.cluster.membership.tester.core.IEvaluator;
import org.cluster.membership.tester.core.Snapshot;
import org.cluster.membership.tester.deploy.MultipleVMDeploymentAndExecutionSimulator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MultipleVMTesterEntryTest extends TestCase {
    

	private Logger logger = Logger.getLogger(MultipleVMTesterEntryTest.class.getName());	
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MultipleVMTesterEntryTest( String testName ) throws Exception {
        super( testName );        
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( MultipleVMTesterEntryTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    /*public void testApp() throws Exception {
        String homePath = System.getProperty("user.dir") + File.separator + "target";
        String programPath = System.getProperty("multiple.vm.program.path");
        if(programPath == null) return;
        MultipleVMEnvConfig config = new MultipleVMEnvConfig(homePath, programPath);
    	IEvaluator evaluator = new BasicEvaluator();    	
    	//runner = new MultipleVMRunner(config, evaluator);    	

    	
    	File cases = new File(config.getCasesPath());
		
		File[] sortedByName = cases.listFiles();
		Arrays.sort(sortedByName, (a, b) -> a.getName().compareTo(b.getName()));
		List<Double> ans = new ArrayList<Double>();
		for(File f: sortedByName) {
			MultipleVMDeploymentAndExecutionSimulator deployment = new MultipleVMDeploymentAndExecutionSimulator(config);			
			try {
				//boolean success = new MultipleVMDeploymentSimulator(getAppConfig()).deploy(f);
				Snapshot snapshot = deployment.deploy(f);
				Double success = evaluator.evaluate(snapshot);
				
				String message = "FAILED test for file " + f.getName();
				testArg(success, message);
				ans.add(success);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "FAILED test for file " + f.getName());
				logger.log(Level.SEVERE, "error trace below:");
				e.printStackTrace();
			}
			deployment.undeploy();
		}
		for(Double dbl:ans)System.out.println("success: " + dbl);
        assertTrue( true );
    }
    
    public void testArg(Double success, String message) {
    	assertEquals(message, 1, success, 0.5);
    }*/
    
}
