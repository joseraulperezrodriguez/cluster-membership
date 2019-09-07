package org.cluster.membership.protocol.structures;

import java.util.Iterator;
import java.util.TreeSet;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.util.ValuePriorityEntryComparators;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ValuePrioritySetTest extends TestCase {
	
	
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
    public ValuePrioritySetTest( String testName ) throws Exception {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( ValuePrioritySetTest.class );
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
    
    public void testContains1() {
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
    	
    	assert(set.contains(new ValuePriorityEntry<Node, Long>(c, null), false));
    	assert(!set.contains(new ValuePriorityEntry<Node, Long>(a, null), false));
    	assert(set.contains(new ValuePriorityEntry<Node, Long>(c, null), true));
    	assert(!set.contains(new ValuePriorityEntry<Node, Long>(a, null), true));
    }
    
    public void testContains2() {
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
    	
    	assert(set.contains(new ValuePriorityEntry<Node, Long>(c, 1l), true));
    	
    	assert(set.size() == 3);
    	
    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();
    	
    	@SuppressWarnings("unchecked")
    	ValuePriorityEntry<Node, Long>[] array = buildArray(new ValuePriorityEntry<Node, Long>(c, 1l), o1, o2);    	

    	assert(equals(array, iterator));
    	
    }
    
    @SuppressWarnings("unchecked")
	public void testRemove() {
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
    	
    	set.remove(new ValuePriorityEntry<Node, Long>(c, null));
    	
    	assert(set.size() == 2);
    	    	
    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();    	
    	
    	ValuePriorityEntry<Node, Long>[] array = buildArray(o1, o2);    	
    	assert(equals(array, iterator));
    	
    	set.remove(new ValuePriorityEntry<Node, Long>(d, 2l));
    	
    	assert(set.size() == 1);
    	iterator = set.iterator();    	
    	
    	array = buildArray(o2);    	
    	assert(equals(array, iterator));
    	
    	set.remove(new ValuePriorityEntry<Node, Long>(b, 2l));

    	assert(set.size() == 0);

    	iterator = set.iterator();    	
    	
    	array = buildArray();    	
    	assert(equals(array, iterator));

    	
    }
    
    public void testTailSet() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(d, 2l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(b, 3l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(e, 1l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(b, 4l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(c, 3l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(a, 5l);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	
    	TreeSet<ValuePriorityEntry<Node, Long>> tail = set.tailSet(new ValuePriorityEntry<Node, Long>(Node.getLowerNode(), 2l));
    	
    	Iterator<ValuePriorityEntry<Node, Long>> iterator = tail.iterator();
    	    	
    	@SuppressWarnings("unchecked")
		ValuePriorityEntry<Node, Long>[] array = buildArray(o1, o2, o5, o6);
    	
    	assert(tail.size() == 4);
    	
    	
    	assert(equals(array, iterator));
    	
    }
    
    public void testPollFirst() {
       	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(d, 2l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(b, 3l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(e, 1l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(b, 4l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(c, 3l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(a, 5l);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	
    	ValuePriorityEntry<Node, Long> first = set.pollFirst();
    	
    	assert(first.equals(o4) && first.getValue() == 1L);
    	
    	Iterator<ValuePriorityEntry<Node, Long>> iterator = set.iterator();
    	
    	@SuppressWarnings("unchecked")
		ValuePriorityEntry<Node, Long>[] array = buildArray(o1, o2, o5, o6);
    	
    	assert(set.size() == 4);
    	
    	assert(equals(array, iterator));
    
    }
    
    public void testLast() {
    	ValuePrioritySet<ValuePriorityEntry<Node, Long>> set =
    			new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
    					ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
    	
    	ValuePriorityEntry<Node, Long> o1 = new ValuePriorityEntry<Node, Long>(d, 2l);
    	ValuePriorityEntry<Node, Long> o2 = new ValuePriorityEntry<Node, Long>(b, 3l);
    	ValuePriorityEntry<Node, Long> o4 = new ValuePriorityEntry<Node, Long>(e, 1l);
    	ValuePriorityEntry<Node, Long> o3 = new ValuePriorityEntry<Node, Long>(b, 4l);
    	ValuePriorityEntry<Node, Long> o5 = new ValuePriorityEntry<Node, Long>(c, 3l);
    	ValuePriorityEntry<Node, Long> o6 = new ValuePriorityEntry<Node, Long>(a, 5l);
    	
    	set.add(o1, true);
    	set.add(o2, true);
    	set.add(o3, true);
    	set.add(o4, true);
    	set.add(o5, true);
    	set.add(o6, true);
    	
    	ValuePriorityEntry<Node, Long> last = set.last();
    	
    	assert(last.equals(o6) && last.getValue() == 5L);
    	
    }
    
}
