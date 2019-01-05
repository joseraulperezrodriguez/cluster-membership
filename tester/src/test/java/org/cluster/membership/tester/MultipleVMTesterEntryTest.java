package org.cluster.membership.tester;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MultipleVMTesterEntryTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MultipleVMTesterEntryTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MultipleVMTesterEntryTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	String home = System.getProperty("user.dir");
    	System.out.println(home);
        assertTrue( true );
    }
}
