package org.cluster.membership.tester;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.common.model.util.Tuple2;
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
		
		List<Tuple2<String, Double>> ans = new ArrayList<Tuple2<String, Double>>();
		
		for(File f: sortedByName) {
			SingleVMDeploymentAndExecutionSimulator deployment = new SingleVMDeploymentAndExecutionSimulator(config);			
			try {
				Snapshot snapshot = deployment.deploy(f);
				Double rate = evaluator.evaluate(snapshot);
				ans.add(new Tuple2<String, Double>(f.getName(), rate));
				String message = "FAILED test for file " + f.getName();
				testArg(rate, message);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "FAILED test for file " + f.getName());
				logger.log(Level.SEVERE, "error trace below:");
				e.printStackTrace();
			}
			deployment.undeploy();
		}
		for(Tuple2<String, Double> rt : ans) logger.log(Level.INFO,"rate: " + rt.getB()	+ "/1 for file: " + rt.getA());
        assertTrue( true );
    }
    
    public void testArg(Double success, String message) {
    	assertEquals(message, 1, success, 0.5);
    	//assertTrue( true );
    }
}
