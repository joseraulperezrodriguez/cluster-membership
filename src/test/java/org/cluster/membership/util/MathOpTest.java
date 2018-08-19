package org.cluster.membership.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MathOpTest 
    extends TestCase
{
		
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MathOpTest( String testName )
    {
        super( testName );
        
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MathOpTest.class );
    }

    public void testLog2N() {
    	int log2_0 = MathOp.log2n(0);
    	int log2_1 = MathOp.log2n(1);
    	int log2_2 = MathOp.log2n(2);
    	int log2_3 = MathOp.log2n(3);
    	int log2_4 = MathOp.log2n(4);
    	int log2_5 = MathOp.log2n(5);
    	int log2_6 = MathOp.log2n(6);
    	int log2_7 = MathOp.log2n(7);
		int log2_8 = MathOp.log2n(8);
		
		
		assert(log2_0 == 0);
		assert(log2_1 == 0);
		assert(log2_2 == 1);
		assert(log2_3 == 1);
		assert(log2_4 == 2);
		assert(log2_5 == 2);
		assert(log2_6 == 2);
		assert(log2_7 == 2);
		assert(log2_8 == 3);		
		
    }
    
     
}
