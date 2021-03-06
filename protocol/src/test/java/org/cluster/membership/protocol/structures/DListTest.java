package org.cluster.membership.protocol.structures;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.ClusterNodeEntryTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class DListTest extends ClusterNodeEntryTest {
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DListTest( String testName ) throws Exception {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( DListTest.class );
    }
     
    public void testRemove() {
    	DList nodes = new DList();    	
    	nodes.add(new Node("A", "A 1", 7001, 6001));
    	nodes.add(new Node("B", "B 1", 7002, 6002));
    	nodes.add(new Node("C", "C 1", 7003, 6003));
    	nodes.add(new Node("D", "D 1", 7004, 6004));
    	nodes.add(new Node("E", "E 1", 7005, 6005));
    	nodes.add(new Node("F", "F 1", 7006, 6006));
    	nodes.add(new Node("G", "G 1", 7007, 6007));
    	nodes.add(new Node("H", "H 1", 7008, 6008));
    	nodes.add(new Node("I", "I 1", 7009, 6009));
    	
    	nodes.remove(new Node("A",null,0,0));
    	assert(!nodes.contains(new Node("A",null,0,0)) && nodes.size() == 8);
    	nodes.remove(new Node("A",null,0,0));
    	assert(!nodes.contains(new Node("A",null,0,0)) && nodes.size() == 8);
    	
    	nodes.remove(new Node("B",null,0,0));
    	assert(!nodes.contains(new Node("B","",0,0)) && nodes.size() == 7);
    	nodes.remove(new Node("B",null,0,0));
    	assert(!nodes.contains(new Node("B",null,0,0)) && nodes.size() == 7);
    	
    	nodes.remove(new Node("C",null,0,0));
    	assert(!nodes.contains(new Node("C",null,0,0)) && nodes.size() == 6);
    	nodes.remove(new Node("C",null,0,0));
    	assert(!nodes.contains(new Node("C",null,0,0)) && nodes.size() == 6);
    	
    	nodes.remove(new Node("D",null,0,0));
    	assert(!nodes.contains(new Node("D",null,0,0)) && nodes.size() == 5);
    	nodes.remove(new Node("D",null,0,0));
    	assert(!nodes.contains(new Node("D",null,0,0)) && nodes.size() == 5);
    	
    	nodes.remove(new Node("E",null,0,0));
    	assert(!nodes.contains(new Node("E",null,0,0)) && nodes.size() == 4);
    	nodes.remove(new Node("E",null,0,0));
    	assert(!nodes.contains(new Node("E",null,0,0)) && nodes.size() == 4);
    	
    	nodes.remove(new Node("F",null,0,0));
    	assert(!nodes.contains(new Node("F",null,0,0)) && nodes.size() == 3);
    	nodes.remove(new Node("F",null,0,0));
    	assert(!nodes.contains(new Node("F",null,0,0)) && nodes.size() == 3);
    	
    	nodes.remove(new Node("G",null,0,0));
    	assert(!nodes.contains(new Node("G",null,0,0)) && nodes.size() == 2);
    	nodes.remove(new Node("G",null,0,0));
    	assert(!nodes.contains(new Node("G",null,0,0)) && nodes.size() == 2);
    	
    	nodes.remove(new Node("H",null,0,0));
    	assert(!nodes.contains(new Node("H",null,0,0)) && nodes.size() == 1);
    	nodes.remove(new Node("H",null,0,0));
    	assert(!nodes.contains(new Node("H",null,0,0)) && nodes.size() == 1);
    	
    	nodes.remove(new Node("I",null,0,0));
    	assert(!nodes.contains(new Node("I",null,0,0)) && nodes.size() == 0);
    	nodes.remove(new Node("I",null,0,0));
    	assert(!nodes.contains(new Node("I",null,0,0)) && nodes.size() == 0);
    	
    	nodes.remove(new Node("I",null,0,0));
    	assert(!nodes.contains(new Node("I",null,0,0)) && nodes.size() == 0);
    	nodes.remove(new Node("I",null,0,0));
    	assert(!nodes.contains(new Node("I",null,0,0)) && nodes.size() == 0);
    	
    }
    
    public void testContains1() {
    	DList nodes = new DList();    	
    	nodes.add(new Node("A", "A 1", 7001, 6001));
    	nodes.add(new Node("B", "B 1", 7002, 6002));
    	nodes.add(new Node("C", "C 1", 7003, 6003));
    	nodes.add(new Node("D", "D 1", 7004, 6004));
    	nodes.add(new Node("E", "E 1", 7005, 6005));
    	nodes.add(new Node("F", "F 1", 7006, 6006));
    	nodes.add(new Node("G", "G 1", 7007, 6007));
    	nodes.add(new Node("H", "H 1", 7008, 6008));
    	nodes.add(new Node("I", "I 1", 7009, 6009));
    	
    	assert(nodes.contains(new Node("A", null, 0, 0)));
    	assert(nodes.contains(new Node("B", null, 0, 0)));
    	assert(nodes.contains(new Node("C", null, 0, 0)));
    	assert(nodes.contains(new Node("D", null, 0, 0)));
    	assert(nodes.contains(new Node("E", null, 0, 0)));
    	assert(nodes.contains(new Node("F", null, 0, 0)));
    	assert(nodes.contains(new Node("G", null, 0, 0)));
    	assert(nodes.contains(new Node("H", null, 0, 0)));
    	assert(nodes.contains(new Node("I", null, 0, 0)));
    	
    	assert(!nodes.contains(new Node("AA", null, 0, 0)));
    	assert(!nodes.contains(new Node("II", null, 0, 0)));
    	
    }
    
    public void testContains2() {
    	DList nodes = new DList();    	
    	nodes.add(new Node("A", "A 1", 7001, 6001));    	
    	
    	assert(nodes.contains(new Node("A", null, 0, 0)));    	    	
    	assert(!nodes.contains(new Node("AA", null, 0, 0)));    	    	
    }
    
    public void testContains3() {
    	DList nodes = new DList();    	        
    	
    	assert(!nodes.contains(new Node("A", null, 0, 0)));    	    	    	    	    
    }
    
    public void testInsertion1() {
    	DList list = new DList();
    	list.add(new Node("A", "A 1", 7001, 6001));
    	assert(list.size() == 1);
    }
    
    public void testInsertionOrder1() {
    	DList nodes = new DList();    	
    	nodes.add(new Node("A", "A 1", 7001, 6001));
    	nodes.add(new Node("B", "B 1", 7002, 6002));
    	nodes.add(new Node("C", "C 1", 7003, 6003));
    	nodes.add(new Node("D", "D 1", 7004, 6004));
    	nodes.add(new Node("E", "E 1", 7005, 6005));
    	nodes.add(new Node("F", "F 1", 7006, 6006));
    	nodes.add(new Node("G", "G 1", 7007, 6007));
    	nodes.add(new Node("H", "H 1", 7008, 6008));
    	nodes.add(new Node("I", "I 1", 7009, 6009));
    	
    	Node prev = nodes.get(0);
    	for(int i = 1; i < 9; i++) {
    		Node current = nodes.get(i);
    		assert(prev.getId().compareTo(current.getId()) == -1);
    		prev = current;
    	}
    	
    	
    }
    
    public void testInsertionOrder2() {
    	DList nodes = new DList();
    	nodes.add(new Node("B", "B 1", 7002, 6002));
    	nodes.add(new Node("A", "A 1", 7001, 6001));
    	nodes.add(new Node("C", "C 1", 7003, 6003));
    	nodes.add(new Node("D", "D 1", 7004, 6004));
    	nodes.add(new Node("E", "E 1", 7005, 6005));
    	nodes.add(new Node("F", "F 1", 7006, 6006));
    	nodes.add(new Node("G", "G 1", 7007, 6007));
    	nodes.add(new Node("H", "H 1", 7008, 6008));
    	nodes.add(new Node("I", "I 1", 7009, 6009));
    	
    	Node prev = nodes.get(0);
    	for(int i = 1; i < 9; i++) {
    		Node current = nodes.get(i);
    		assert(prev.getId().compareTo(current.getId()) == -1);
    		prev = current;
    	}
    	
    	
    }
    
    public void testInsertionOrder3() {
    	DList nodes = new DList();
    	nodes.add(new Node("C", "C 1", 7003, 6003));
    	nodes.add(new Node("B", "B 1", 7002, 6002));
    	nodes.add(new Node("A", "A 1", 7001, 6001));    	
    	nodes.add(new Node("D", "D 1", 7004, 6004));
    	nodes.add(new Node("E", "E 1", 7005, 6005));
    	nodes.add(new Node("F", "F 1", 7006, 6006));
    	nodes.add(new Node("G", "G 1", 7007, 6007));
    	nodes.add(new Node("H", "H 1", 7008, 6008));
    	nodes.add(new Node("I", "I 1", 7009, 6009));
    	
    	Node prev = nodes.get(0);
    	for(int i = 1; i < 9; i++) {
    		Node current = nodes.get(i);
    		assert(prev.getId().compareTo(current.getId()) == -1);
    		prev = current;
    	}
    	
    	
    }
    
    public void testInsertionOrder4() {
    	DList nodes = new DList();
    	nodes.add(new Node("F", "F 1", 7006, 6006));
    	nodes.add(new Node("E", "E 1", 7005, 6005));
    	nodes.add(new Node("D", "D 1", 7004, 6004));
    	nodes.add(new Node("C", "C 1", 7003, 6003));
    	nodes.add(new Node("B", "B 1", 7002, 6002));
    	nodes.add(new Node("A", "A 1", 7001, 6001));    	    	    	    	
    	nodes.add(new Node("G", "G 1", 7007, 6007));
    	nodes.add(new Node("H", "H 1", 7008, 6008));
    	nodes.add(new Node("I", "I 1", 7009, 6009));
    	
    	Node prev = nodes.get(0);
    	for(int i = 1; i < 9; i++) {
    		Node current = nodes.get(i);
    		assert(prev.getId().compareTo(current.getId()) == -1);
    		prev = current;
    	}
    	
    	
    }
    
    public void testInsertionOrder5() {
    	DList nodes = new DList();
    	nodes.add(new Node("I", "I 1", 7009, 6009));
    	nodes.add(new Node("H", "H 1", 7008, 6008));
    	nodes.add(new Node("G", "G 1", 7007, 6007));
    	nodes.add(new Node("F", "F 1", 7006, 6006));
    	nodes.add(new Node("E", "E 1", 7005, 6005));
    	nodes.add(new Node("D", "D 1", 7004, 6004));
    	nodes.add(new Node("C", "C 1", 7003, 6003));
    	nodes.add(new Node("B", "B 1", 7002, 6002));
    	nodes.add(new Node("A", "A 1", 7001, 6001));    	    	    	    	

    	Node prev = nodes.get(0);
    	for(int i = 1; i < 9; i++) {
    		Node current = nodes.get(i);
    		assert(prev.getId().compareTo(current.getId()) == -1);
    		prev = current;
    	}
    	
    	
    }
    
    public void testInsertionOrder6() {
    	DList nodes = new DList();
    	
    	nodes.add(new Node("I", "I 1", 7009, 6009));    	
    	nodes.add(new Node("G", "G 1", 7007, 6007));    	
    	nodes.add(new Node("A", "A 1", 7001, 6001));    	
    	nodes.add(new Node("H", "H 1", 7008, 6008));
    	nodes.add(new Node("C", "C 1", 7003, 6003));
    	nodes.add(new Node("E", "E 1", 7005, 6005));
    	nodes.add(new Node("B", "B 1", 7002, 6002));
    	nodes.add(new Node("F", "F 1", 7006, 6006));
    	
    	nodes.add(new Node("D", "D 1", 7004, 6004));

    	Node prev = nodes.get(0);
    	for(int i = 1; i < 9; i++) {
    		Node current = nodes.get(i);
    		assert(prev.getId().compareTo(current.getId()) == -1);
    		prev = current;
    	}
    	
    	
    }
    
}
