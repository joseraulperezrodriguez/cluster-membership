package org.cluster.membership.protocol;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.EnvUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ClusterNodeEntryTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ClusterNodeEntryTest( String testName ) throws Exception {
        super( testName );
        
        //String home = System.getProperty("user.dir");        
        Node node = new Node("A", "localhost", 6001, 7001);
        
        String argsS = EnvUtils.generateNodeCommandLineArguments(node, 0) + " --mode=TEST"; 
        			
        ApplicationArguments defaultAA = new DefaultApplicationArguments(argsS.split("\\s+")); 
        Config.read(defaultAA);
        
    }
    
    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( ClusterNodeEntryTest.class );
    }
    
    public void testWork() {
    	assert(true);
    }
    
}
