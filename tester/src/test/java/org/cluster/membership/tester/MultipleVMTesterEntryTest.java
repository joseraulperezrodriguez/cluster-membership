package org.cluster.membership.tester;

import java.io.File;

import org.cluster.membership.tester.config.MultipleVMEnvConfig;
import org.cluster.membership.tester.core.BasicEvaluator;
import org.cluster.membership.tester.core.IEvaluator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MultipleVMTesterEntryTest extends TestCase {
    
	private MultipleVMRunner runner;
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MultipleVMTesterEntryTest( String testName ) throws Exception {
        super( testName );
        
        String homePath = System.getProperty("user.dir") + File.separator + "target";
        String programPath = System.getProperty("multiple.vm.program.path");
        if(programPath == null) return;
        System.out.println("DEBUG P " + programPath);
        MultipleVMEnvConfig config = new MultipleVMEnvConfig(homePath, programPath);
    	IEvaluator evaluator = new BasicEvaluator();    	
    	runner = new MultipleVMRunner(config, evaluator);    	
        
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
    public void testApp() throws Exception {
    	if(runner != null) runner.runTemplates();
        assertTrue( true );
    }
}
