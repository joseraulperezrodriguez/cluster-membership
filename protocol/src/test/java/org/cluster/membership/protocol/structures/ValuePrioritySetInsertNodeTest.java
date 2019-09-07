package org.cluster.membership.protocol.structures;

import java.util.Iterator;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.ClusterNodeEntryTest;
import org.cluster.membership.protocol.util.ValuePriorityEntryComparators;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ValuePrioritySetInsertNodeTest extends ClusterNodeEntryTest {
	
	
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
    public ValuePrioritySetInsertNodeTest( String testName ) throws Exception {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( ValuePrioritySetInsertNodeTest.class );
    }
    
    public boolean equals(ValuePriorityEntry<Node, Long>[] array, Iterator<ValuePriorityEntry<Node, Long>> iterator) {
    	int i = 0;
    	while(iterator.hasNext()) {
    		ValuePriorityEntry<Node, Long> val = iterator.next();
    		if(!array[i].equals(val) || array[i].getValue() != val.getValue()) return false;
    		i++;
    	}
    			
    	return true;
    }
    
    @SuppressWarnings("unchecked")
	public ValuePriorityEntry<Node, Long>[] buildArray(ValuePriorityEntry<Node, Long>... array) {
    	return array;
    }
    
    public void testInsertion1() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(d, 2l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(b, 3l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(b, 4l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(c, 3l);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	
    	assert(set.size() == 3);
    	
    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator(); 
    	
    	@SuppressWarnings("unchecked")
		ValuePriorityEntry<Node, Long>[] array = buildArray(o1, o2, o4);    	
    	
    	assert(equals(array, iterator));
    }
    
    public void testInsertion2() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(b, 4l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(b, 3l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(b, 2l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(b, 1l);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	
    	assert(set.size() == 1);
    	assert(set.last().getValue() == 1l);        	    	
    }
    
    public void testInsertion3() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(b, 4l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(a, 3l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(b, 2l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(e, 1l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(c, 5l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(d, 5l);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	
    	assert(set.size() == 5);

    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();
    	
    	@SuppressWarnings("unchecked")
		ValuePriorityEntry<Node, Long>[] array = buildArray(o4, o3, o2, o5, o6);    	
    	
    	assert(equals(array, iterator));
    	
    }
    
    public void testInsertion4() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(b, 4l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(a, 3l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(b, 2l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(e, 1l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(c, 5l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(d, 5l);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, false);
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	
    	assert(set.size() == 5);

    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();
    	
    	@SuppressWarnings("unchecked")
		ValuePriorityEntry<Node, Long>[] array = buildArray(o4, o2, o1, o5, o6);    	
    	
    	assert(equals(array, iterator));
    	
    }
    
    public void testInsertion5() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(b, 4l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(a, 4l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(b, 4l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(e, 4l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(c, 4l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(d, 4l);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, false);
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	
    	assert(set.size() == 5);

    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();
    	
    	@SuppressWarnings("unchecked")
		ValuePriorityEntry<Node, Long>[] array = buildArray(o2, o3, o5, o6, o4);    	
    	
    	assert(equals(array, iterator));
    	
    }
    
    public void testInsertion6() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(b, 4l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(a, 1l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(b, 1l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(e, 2l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(c, 5l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(d, 3l);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, false);
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	
    	assert(set.size() == 5);

    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();
    	
    	@SuppressWarnings("unchecked")
		ValuePriorityEntry<Node, Long>[] array = buildArray(o2, o4, o6, o1, o5);    	
    	
    	assert(equals(array, iterator));
    	
    }

    
    public void testInsertion7() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(b, 4l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(a, 1l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(e, 2l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(c, 5l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(d, 3l);
    	
    	set.add(o1, false);
    	set.add(o2, false);
    	set.add(o4, false);
    	set.add(o5, false);
    	set.add(o6, false);
    	
    	assert(set.size() == 5);

    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();
    	
    	@SuppressWarnings("unchecked")
		ValuePriorityEntry<Node, Long>[] array = buildArray(o2, o4, o6, o1, o5);    	
    	
    	assert(equals(array, iterator));
    	
    }
    
    public void testInsertion8() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(b, 11l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(a, 8l);    	
    	ValuePriorityEntry<Node, Long> o8 = new ValuePriorityEntry<Node, Long>(e, 15l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(d, 13l);
    	ValuePriorityEntry<Node, Long> o10 = new ValuePriorityEntry<Node, Long>(d, 20l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(b, 41l);
    	ValuePriorityEntry<Node, Long> o7 = new ValuePriorityEntry<Node, Long>(a, 35l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(e, 22l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(c, 5l);    
    	ValuePriorityEntry<Node, Long> o9 = new ValuePriorityEntry<Node, Long>(c, 12l);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	set.add(o7, true);
    	set.add(o8, true);
    	set.add(o9, true);
    	set.add(o10, true);
    	
    	assert(set.size() == 5);

    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();
    	@SuppressWarnings("unchecked")
		ValuePriorityEntry<Node, Long>[] array = buildArray(o4, o2, o1, o5, o8);    	

    	
    	assert(equals(array, iterator));
    	
    }
    
    public void testInsertion9() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(b, 11l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(a, 8l);    	
    	ValuePriorityEntry<Node, Long> o8 = new ValuePriorityEntry<Node, Long>(e, 15l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(d, 13l);
    	ValuePriorityEntry<Node, Long> o10 = new ValuePriorityEntry<Node, Long>(d, 20l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(b, 41l);
    	ValuePriorityEntry<Node, Long> o7 = new ValuePriorityEntry<Node, Long>(a, 35l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(e, 22l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(c, 5l);    
    	ValuePriorityEntry<Node, Long> o9 = new ValuePriorityEntry<Node, Long>(c, 12l);
    	
    	set.add(o1, false);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, false);
    	set.add(o5, true);
    	set.add(o6, true);
    	set.add(o7, false);
    	set.add(o8, true);
    	set.add(o9, false);
    	set.add(o10, false);
    	
    	assert(set.size() == 5);

    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();
    	@SuppressWarnings("unchecked")
		ValuePriorityEntry<Node, Long>[] array = buildArray(o4, o2, o1, o5, o8);    	

    	
    	assert(equals(array, iterator));
    	
    }
    
    public void testInsertion10() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>descPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(b, 11l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(a, 8l);    	
    	ValuePriorityEntry<Node, Long> o8 = new ValuePriorityEntry<Node, Long>(e, 15l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(d, 13l);
    	ValuePriorityEntry<Node, Long> o10 = new ValuePriorityEntry<Node, Long>(d, 20l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(b, 41l);
    	ValuePriorityEntry<Node, Long> o7 = new ValuePriorityEntry<Node, Long>(a, 35l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(e, 22l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(c, 5l);    
    	ValuePriorityEntry<Node, Long> o9 = new ValuePriorityEntry<Node, Long>(c, 12l);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	set.add(o7, true);
    	set.add(o8, true);
    	set.add(o9, true);
    	set.add(o10, true);
    	
    	assert(set.size() == 5);

    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();
    	@SuppressWarnings("unchecked")
		ValuePriorityEntry<Node, Long>[] array = buildArray(o9, o10, o3, o7,  o6);    	

    	
    	assert(equals(array, iterator));
    	
    }
    
    public void testInsertion11() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>descPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(b, 11l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(a, 8l);    	
    	ValuePriorityEntry<Node, Long> o8 = new ValuePriorityEntry<Node, Long>(e, 15l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(d, 13l);
    	ValuePriorityEntry<Node, Long> o10 = new ValuePriorityEntry<Node, Long>(d, 20l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(b, 41l);
    	ValuePriorityEntry<Node, Long> o7 = new ValuePriorityEntry<Node, Long>(a, 35l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(e, 22l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(c, 5l);    
    	ValuePriorityEntry<Node, Long> o9 = new ValuePriorityEntry<Node, Long>(c, 12l);
    	
    	set.add(o1, false);
    	set.add(o2, true);
    	set.add(o3, false);
    	set.add(o4, false);
    	set.add(o5, true);
    	set.add(o6, false);
    	set.add(o7, false);
    	set.add(o8, true);
    	set.add(o9, false);
    	set.add(o10, false);
    	
    	assert(set.size() == 5);

    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();
    	@SuppressWarnings("unchecked")
    	ValuePriorityEntry<Node, Long>[] array = buildArray(o4, o2, o1, o5, o3);    	

    	assert(equals(array, iterator));
    	
    }
    
    
    
    
}
