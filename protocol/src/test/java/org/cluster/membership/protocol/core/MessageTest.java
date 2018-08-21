package org.cluster.membership.protocol.core;

import java.util.TimeZone;

import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.model.Node;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MessageTest 
    extends TestCase
{
	
	private Node a = new Node("A", "A 1", 7001, TimeZone.getDefault());
	private Node b = new Node("B", "B 1", 7001, TimeZone.getDefault());
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MessageTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MessageTest.class );
    }

    
    public void testEquals() {
    	Message m1 = new Message(MessageType.KEEP_ALIVE, a, 3);
    	Message m2 = new Message(MessageType.KEEP_ALIVE, a, 3);    	
    	Message m3 = new Message(MessageType.PROBE, a, 3);
    	Message m4 = new Message(MessageType.PROBE, b, 3);
    	    	
    	assert(m1.equals(m2));    	
    	assert(!m1.equals(m3));
    	assert(!m4.equals(m3));    	
    }
    
    public void testCompareTo1() {
    	Message m1 = new Message(MessageType.KEEP_ALIVE, a, 3);
    	Message m2 = new Message(MessageType.KEEP_ALIVE, a, 3);    	
    	Message m3 = new Message(MessageType.PROBE, a, 3);
    	Message m4 = new Message(MessageType.PROBE, b, 3);
    	    	
    	assert(m1.compareTo(m2) == 0);    	
    	assert(m1.compareTo(m3) == -1);
    	assert(m4.compareTo(m3) == 1);    	
    }
    
    public void comparator() {
    	Message m1 = new Message(MessageType.KEEP_ALIVE, a, 3);
    	Message m2 = new Message(MessageType.KEEP_ALIVE, a, 1);
    	
    	assert(Message.getIterationsAscComparator().compare(m2, m1) < 0);
    	
    }
    
           
}
