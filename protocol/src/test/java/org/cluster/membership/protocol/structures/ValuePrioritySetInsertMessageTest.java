package org.cluster.membership.protocol.structures;

import java.util.Iterator;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.ClusterNodeEntryTest;
import org.cluster.membership.protocol.core.MessageType;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.util.MessageComparators;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ValuePrioritySetInsertMessageTest extends ClusterNodeEntryTest {
	
	
	private final Node a = new Node("A", "A 1", 7001, 6001);
	private final Node b = new Node("B", "B 1", 7001, 6001);
	private final Node c = new Node("C", "C 1", 7001, 6001);
	private final Node d = new Node("D", "D 1", 7001, 6001);
	private final Node e = new Node("E", "E 1", 7001, 6001);	
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ValuePrioritySetInsertMessageTest( String testName ) throws Exception {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( ValuePrioritySetInsertMessageTest.class );
    }
    
    public boolean equals(Message[] array, Iterator<Message> iterator) {
    	int i = 0;
    	while(iterator.hasNext()) {
    		Message val = iterator.next();
    		if(!array[i].equals(val) || array[i].remainingIterations() != val.remainingIterations()) return false;
    		i++;
    	}
    			
    	return true;
    }
    
	public Message[] buildArray(Message... array) {
    	return array;
    }
    
    public void testInsertion1() {
    	ValuePrioritySet<Message> set =
    			new ValuePrioritySet<>(MessageComparators.getIterationsDescComparator(),
    					MessageComparators.getIteratorPriorityAscComparator());
    	
    	Message o1 = new Message(MessageType.ADD_TO_CLUSTER, d, 2);
    	Message o2 = new Message(MessageType.KEEP_ALIVE, b, 3);
    	Message o3 = new Message(MessageType.REMOVE_FROM_CLUSTER, e, 1);
    	Message o4 = new Message(MessageType.PROBE, d, 4);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	
    	
    	assert(set.size() == 4);
    	
    	Iterator<Message> iterator = set.iterator(); 
    	
    	
		Message[] array = buildArray(o4, o2, o1, o3);    	
    	
    	assert(equals(array, iterator));
    }
    
    public void testInsertion2() {
    	ValuePrioritySet<Message> set =
    			new ValuePrioritySet<>(MessageComparators.getIterationsDescComparator(),
    					MessageComparators.getIteratorPriorityAscComparator());
    	
    	Message o1 = new Message(MessageType.ADD_TO_CLUSTER, d, 2);
    	Message o2 = new Message(MessageType.KEEP_ALIVE, b, 3);
    	Message o3 = new Message(MessageType.REMOVE_FROM_CLUSTER, e, 1);
    	Message o4 = new Message(MessageType.ADD_TO_CLUSTER, d, 4);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	
    	
    	assert(set.size() == 3);
    	
    	Iterator<Message> iterator = set.iterator(); 
    	
    	
		Message[] array = buildArray(o2, o1, o3);    	
    	
    	assert(equals(array, iterator));
    }
    
    public void testInsertion3() {
    	ValuePrioritySet<Message> set =
    			new ValuePrioritySet<>(MessageComparators.getIterationsDescComparator(),
    					MessageComparators.getIteratorPriorityAscComparator());
    	
    	Message o1 = new Message(MessageType.ADD_TO_CLUSTER, d, 4);
    	Message o2 = new Message(MessageType.KEEP_ALIVE, b, 3);
    	Message o3 = new Message(MessageType.REMOVE_FROM_CLUSTER, e, 1);
    	Message o4 = new Message(MessageType.ADD_TO_CLUSTER, d, 2);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	
    	
    	assert(set.size() == 3);
    	
    	Iterator<Message> iterator = set.iterator(); 
    	
    	
		Message[] array = buildArray(o2, o4, o3);    	
    	
    	assert(equals(array, iterator));
    }
    
    public void testInsertion4() {
    	ValuePrioritySet<Message> set =
    			new ValuePrioritySet<>(MessageComparators.getIterationsDescComparator(),
    					MessageComparators.getIteratorPriorityAscComparator());
    	
    	Message o1 = new Message(MessageType.ADD_TO_CLUSTER, d, 1);
    	Message o2 = new Message(MessageType.ADD_TO_CLUSTER, d, 2);
    	Message o3 = new Message(MessageType.ADD_TO_CLUSTER, d, 3);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);    	
    	
    	assert(set.size() == 1);

    	assert(set.last().remainingIterations() == 1);
    }
    
    public void testInsertion5() {
    	ValuePrioritySet<Message> set =
    			new ValuePrioritySet<>(MessageComparators.getIterationsDescComparator(),
    					MessageComparators.getIteratorPriorityAscComparator());
    	
    	Message o1 = new Message(MessageType.ADD_TO_CLUSTER, d, 4);
    	Message o2 = new Message(MessageType.KEEP_ALIVE, b, 3);
    	Message o3 = new Message(MessageType.REMOVE_FROM_CLUSTER, e, 1);
    	Message o4 = new Message(MessageType.ADD_TO_CLUSTER, d, 2);
    	Message o6 = new Message(MessageType.ADD_TO_CLUSTER, a, 2);
    	Message o5 = new Message(MessageType.ADD_TO_CLUSTER, c, 2);
    	
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	
    	
    	assert(set.size() == 5);
    	
    	Iterator<Message> iterator = set.iterator(); 
    	
    	
		Message[] array = buildArray(o2, o6, o5, o4, o3);    	
    	
    	assert(equals(array, iterator));
    }
    
    public void testInsertion6() {
    	ValuePrioritySet<Message> set =
    			new ValuePrioritySet<>(MessageComparators.getIterationsDescComparator(),
    					MessageComparators.getIteratorPriorityAscComparator());
    	
    	Message o4 = new Message(MessageType.ADD_TO_CLUSTER, d, 2);
    	Message o6 = new Message(MessageType.ADD_TO_CLUSTER, a, 2);
    	Message o5 = new Message(MessageType.ADD_TO_CLUSTER, c, 2);
    	
    	
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	
    	
    	assert(set.size() == 3);
    	
    	Iterator<Message> iterator = set.iterator(); 
    	    	
		Message[] array = buildArray(o6, o5, o4);    	
    	
    	assert(equals(array, iterator));
    }
    
    public void testInsertion7() {
    	ValuePrioritySet<Message> set =
    			new ValuePrioritySet<>(MessageComparators.getIterationsDescComparator(),
    					MessageComparators.getIteratorPriorityAscComparator());
    	
    	Message o4 = new Message(MessageType.ADD_TO_CLUSTER, a, 2);
    	Message o6 = new Message(MessageType.PROBE, a, 2);
    	Message o5 = new Message(MessageType.SUSPECT_DEAD, a, 2);
    	
    	
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	
    	
    	assert(set.size() == 3);
    	
    	Iterator<Message> iterator = set.iterator(); 
    	    	
		Message[] array = buildArray(o5, o4, o6);    	
    	
    	assert(equals(array, iterator));
    }
    
    public void testInsertion8() {
    	ValuePrioritySet<Message> set =
    			new ValuePrioritySet<>(MessageComparators.getIterationsDescComparator(),
    					MessageComparators.getIteratorPriorityAscComparator());
    	
    	Message o4 = new Message(MessageType.ADD_TO_CLUSTER, a, 2);
    	Message o6 = new Message(MessageType.PROBE, a, 2);
    	Message o5 = new Message(MessageType.SUSPECT_DEAD, a, 2);
    	Message o3 = new Message(MessageType.SUSPECT_DEAD, b, 5);
    	Message o2 = new Message(MessageType.SUSPECT_DEAD, b, 2);
    	
    	
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	set.add(o3, true);
    	set.add(o2, false);
    	
    	
    	assert(set.size() == 4);
    	
    	Iterator<Message> iterator = set.iterator(); 
    	    	
		Message[] array = buildArray(o3 ,o5, o4, o6);    	
    	
    	assert(equals(array, iterator));
    }
    
    public void testInsertion9() {
    	ValuePrioritySet<Message> set =
    			new ValuePrioritySet<>(MessageComparators.getIterationsDescComparator(),
    					MessageComparators.getIteratorPriorityAscComparator());
    	
    	Message o4 = new Message(MessageType.ADD_TO_CLUSTER, a, 2);
    	Message o6 = new Message(MessageType.PROBE, a, 2);
    	Message o5 = new Message(MessageType.SUSPECT_DEAD, a, 2);
    	Message o3 = new Message(MessageType.SUSPECT_DEAD, b, 5);
    	Message o2 = new Message(MessageType.SUSPECT_DEAD, b, 2);
    	
    	
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	set.add(o3, true);
    	set.add(o2, true);
    	
    	
    	assert(set.size() == 4);
    	
    	Iterator<Message> iterator = set.iterator(); 
    	    	
    	Message[] array = buildArray(o5, o2, o4, o6);    	
    	
    	assert(equals(array, iterator));
    }
    
    
}
