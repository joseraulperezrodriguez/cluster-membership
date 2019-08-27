package org.cluster.membership.protocol.core;

import java.util.TimeZone;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.ClusterNodeEntryTest;
import org.cluster.membership.protocol.model.Message;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MessageTest extends ClusterNodeEntryTest {
	
	private Node a = new Node("A", "A 1", 7001, 6001);
	private Node b = new Node("B", "B 1", 7001, 6001);
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MessageTest( String testName ) throws Exception {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( MessageTest.class );
    }

    
    public void testEquals() {
    	Message m1 = new Message(MessageType.KEEP_ALIVE, a, 3, TimeZone.getDefault());
    	Message m2 = new Message(MessageType.KEEP_ALIVE, a, 3, TimeZone.getDefault());    	
    	Message m3 = new Message(MessageType.PROBE, a, 3, TimeZone.getDefault());
    	Message m4 = new Message(MessageType.PROBE, b, 3, TimeZone.getDefault());
    	    	
    	assert(m1.equals(m2));    	
    	assert(!m1.equals(m3));
    	assert(!m4.equals(m3));    	
    }
    
    public void testCompareTo1() {
    	Message m1 = new Message(MessageType.KEEP_ALIVE, a, 3, TimeZone.getDefault());
    	Message m2 = new Message(MessageType.KEEP_ALIVE, a, 3, TimeZone.getDefault());    	
    	Message m3 = new Message(MessageType.PROBE, a, 3, TimeZone.getDefault());
    	Message m4 = new Message(MessageType.PROBE, b, 3, TimeZone.getDefault());
    	    	
    	assert(m1.compareTo(m2) == 0);    	
    	assert(m1.compareTo(m3) == -1);
    	assert(m4.compareTo(m3) == 1);    	
    }
    
    public void comparator() {
    	Message m1 = new Message(MessageType.KEEP_ALIVE, a, 3, TimeZone.getDefault());
    	Message m2 = new Message(MessageType.KEEP_ALIVE, a, 1, TimeZone.getDefault());
    	
    	assert(Message.getIterationsAscComparator().compare(m2, m1) < 0);
    	
    }
    
           
}
