package org.cluster.membership.protocol.core;

import java.util.TimeZone;

import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.model.Node;
import org.cluster.membership.protocol.structures.DList;
import org.cluster.membership.protocol.structures.ValuePriorityEntry;
import org.cluster.membership.protocol.structures.ValuePrioritySet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class RandomServiceTest 
    extends TestCase
{
	
	private DList nodes = new DList();
	private ValuePrioritySet<ValuePriorityEntry<Node, Long>> suspectingNodesTimeout;
	private ValuePrioritySet<ValuePriorityEntry<Node, Long>> failed;
	private ValuePrioritySet<Message> rumorsToSend;	
	private ValuePrioritySet<Message> receivedRumors;
	
	private Node a = new Node("A", "A 1", 7001, 6001,TimeZone.getDefault());
	private Node b = new Node("B", "B 1", 7002, 6002,TimeZone.getDefault());
	private Node c = new Node("C", "C 1", 7003, 6003,TimeZone.getDefault());
	private Node d = new Node("D", "D 1", 7004, 6004,TimeZone.getDefault());
	private Node e = new Node("E", "E 1", 7005, 6005,TimeZone.getDefault());
	private Node f = new Node("F", "F 1", 7006, 6006,TimeZone.getDefault());
	private Node g = new Node("G", "G 1", 7007, 6007,TimeZone.getDefault());
	private Node h = new Node("H", "H 1", 7008, 6008,TimeZone.getDefault());
	private Node i = new Node("I", "I 1", 7009, 6009,TimeZone.getDefault());
	
	private ClusterView clusterView;
	
	private RandomService randomService;
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RandomServiceTest( String testName )
    {
        super( testName );
        
        init();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( RandomServiceTest.class );
    }

    
    private void init() {
    	
    	this.suspectingNodesTimeout = new ValuePrioritySet<>(ValuePriorityEntry.<Node, Long>ascComparator(),
				ValuePriorityEntry.<Node, Long>ascPriorityComparator());
		this.rumorsToSend = new ValuePrioritySet<>(Message.getIterationsDescComparator(), 
				Message.getIteratorPriorityAscComparator());		
		this.nodes = new DList();
		this.failed = new ValuePrioritySet<>(ValuePriorityEntry.<Node, Long>ascComparator(),
				ValuePriorityEntry.<Node, Long>ascPriorityComparator());		
		this.receivedRumors = new ValuePrioritySet<Message>(Message.getGeneratedTimeAscComparator(),
				Message.getGeneratedTimePriorityAscComparator());
		
		this.clusterView = new ClusterView(nodes, suspectingNodesTimeout, failed, rumorsToSend, receivedRumors);
		
		this.randomService = new RandomService();
    	
    	nodes.add(a);
    	nodes.add(b);
    	nodes.add(c);
    	nodes.add(d);
    	nodes.add(e);
    	nodes.add(f);
    	nodes.add(g);
    	nodes.add(h);
    	nodes.add(i);
    	
    }
    
    public void testRandomGeneration1() {
    	DList list = new DList();
    	ClusterView localClusterView = new ClusterView(list, suspectingNodesTimeout, failed, rumorsToSend, receivedRumors);
    	
    	Node node1 = this.randomService.getRandom(localClusterView);
    	
    	assert(node1 == null);
    }
    
    public void testRandomGeneration2() {    	
    	
    	Node node1 = this.randomService.getRandom(clusterView);
    	Node node2 = this.randomService.getRandom(clusterView);
    	Node node3 = this.randomService.getRandom(clusterView);
    	Node node4 = this.randomService.getRandom(clusterView);
    	
    	assert(node1 != null);
    	assert(node2 != null);
    	assert(node3 != null);
    	assert(node4 != null);
    	    	
    }
    
    public void testAvoidFailing1() {
    	failed.add(new ValuePriorityEntry<Node, Long>(c,10l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(e,11l), true);
    	
    	Node node = this.randomService.getRandom(clusterView);
    	
    	if(node == null) return;
    	
    	assert(!node.getId().equals("C"));
    	assert(!node.getId().equals("E"));
    	    	
    	
    }
    
    public void testAvoidFailing2() {
    	failed.add(new ValuePriorityEntry<Node, Long>(c,10l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(e,11l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(a,99l), true);
    	
    	Node node = this.randomService.getRandom(clusterView);
    	
    	if(node == null) return;
    	
    	assert(!node.getId().equals("C"));
    	assert(!node.getId().equals("E"));
    	assert(!node.getId().equals("A"));
    	
    }
    
    public void testAvoidFailing3() {
    	failed.add(new ValuePriorityEntry<Node, Long>(c,10l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(e,11l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(a,99l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(h,18l), true);
    	
    	Node node = this.randomService.getRandom(clusterView);
    	
    	if(node == null) return;
    	
    	assert(!node.getId().equals("C"));
    	assert(!node.getId().equals("E"));
    	assert(!node.getId().equals("A"));
    	assert(!node.getId().equals("H"));
    	
    }
    
    public void testAvoidFailing4() {
    	failed.add(new ValuePriorityEntry<Node, Long>(c,10l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(e,11l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(a,99l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(h,18l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(d,82l), true);
    	
    	Node node = this.randomService.getRandom(clusterView);
    	
    	if(node == null) return;
    	
    	assert(!node.getId().equals("C"));
    	assert(!node.getId().equals("E"));
    	assert(!node.getId().equals("A"));
    	assert(!node.getId().equals("H"));
    	assert(!node.getId().equals("D"));
    	
    }
    
    public void testAvoidFailing5() {
    	failed.add(new ValuePriorityEntry<Node, Long>(c,10l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(e,11l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(a,99l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(h,18l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(d,82l), true);
    	
    	suspectingNodesTimeout.add(new ValuePriorityEntry<Node, Long>(b,10l), true);
    	suspectingNodesTimeout.add(new ValuePriorityEntry<Node, Long>(f,17l), true);
    	suspectingNodesTimeout.add(new ValuePriorityEntry<Node, Long>(i,17l), true);
    	
    	
    	Node node = this.randomService.getRandom(clusterView);
    	
    	if(node == null) return;
    	
    	assert(!node.getId().equals("C"));
    	assert(!node.getId().equals("E"));
    	assert(!node.getId().equals("A"));
    	assert(!node.getId().equals("H"));
    	assert(!node.getId().equals("D"));
    	assert(!node.getId().equals("B"));
    	assert(!node.getId().equals("F"));
    	assert(!node.getId().equals("I"));
    	
    }
    
    public void testAvoidFailing6() {
    	failed.add(new ValuePriorityEntry<Node, Long>(c,10l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(e,11l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(a,99l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(h,18l), true);
    	failed.add(new ValuePriorityEntry<Node, Long>(d,82l), true);
    	
    	suspectingNodesTimeout.add(new ValuePriorityEntry<Node, Long>(b,10l), true);
    	suspectingNodesTimeout.add(new ValuePriorityEntry<Node, Long>(f,17l), true);
    	suspectingNodesTimeout.add(new ValuePriorityEntry<Node, Long>(i,17l), true);
    	
    	Node node = this.randomService.getRandom(clusterView);
    	
    	if(node == null) return;
    	
    	assert(!node.getId().equals("C"));
    	assert(!node.getId().equals("E"));
    	assert(!node.getId().equals("A"));
    	assert(!node.getId().equals("H"));
    	assert(!node.getId().equals("D"));
    	assert(!node.getId().equals("B"));
    	assert(!node.getId().equals("F"));
    	assert(!node.getId().equals("I"));
    	
    }
    
}
