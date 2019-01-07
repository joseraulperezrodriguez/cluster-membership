package org.cluster.membership.tester;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.tester.config.LocalEnvConfig;
import org.cluster.membership.tester.core.BasicEvaluator;
import org.cluster.membership.tester.core.IEvaluator;
import org.cluster.membership.tester.core.Snapshot;
import org.cluster.membership.tester.deploy.SingleVMDeploymentAndExecutionSimulator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class SingleVMTesterEntryTest extends TestCase {
    
	private Logger logger = Logger.getLogger(SingleVMTesterEntryTest.class.getName());	
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SingleVMTesterEntryTest( String testName ) throws Exception {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( SingleVMTesterEntryTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws Exception {
        String homePath = System.getProperty("user.dir") + File.separator + "target";
        LocalEnvConfig config = new LocalEnvConfig(homePath, true);
    	IEvaluator evaluator = new BasicEvaluator();    	
    	
    	File cases = new File(config.getCasesPath());
		
		File[] sortedByName = cases.listFiles();
		Arrays.sort(sortedByName, (a, b) -> a.getName().compareTo(b.getName()));
		for(File f: sortedByName) {
			SingleVMDeploymentAndExecutionSimulator deployment = new SingleVMDeploymentAndExecutionSimulator(config);			
			try {
				//boolean success = new MultipleVMDeploymentSimulator(getAppConfig()).deploy(f);
				Snapshot snapshot = deployment.deploy(f);
				Double success = evaluator.evaluate(snapshot);
				
				String message = "FAILED test for file " + f.getName();
				testArg(success, message);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "FAILED test for file " + f.getName());
				logger.log(Level.SEVERE, "error trace below:");
				e.printStackTrace();
			}
			deployment.undeploy();
			break;
		}
        assertTrue( true );
    }
    
    public void testArg(Double success, String message) {
    	assertEquals(message, 1, success, 0.5);
    	System.out.println("success: " + success);
    }
}
