package org.cluster.membership.net.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.cluster.membership.Config;
import org.cluster.membership.core.ClusterView;
import org.cluster.membership.core.MessageType;
import org.cluster.membership.model.Message;
import org.cluster.membership.model.Node;
import org.cluster.membership.structures.DList;
import org.cluster.membership.structures.ValuePriorityEntry;
import org.cluster.membership.structures.ValuePrioritySet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class SocketCommunicationTest extends TestCase {
	
	private DList nodes = new DList();
	private ValuePrioritySet<ValuePriorityEntry<Node, Long>> suspectingNodesTimeout;
	private ValuePrioritySet<ValuePriorityEntry<Node, Long>> failed;
	private ValuePrioritySet<Message> rumorsToSend;	
	private ValuePrioritySet<Message> receivedRumors;

	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SocketCommunicationTest( String testName ) {
        super( testName );
        init();
    }
    
    public void init() {
    	
		this.suspectingNodesTimeout = new ValuePrioritySet<>(ValuePriorityEntry.<Node, Long>ascComparator(),
				ValuePriorityEntry.<Node, Long>ascPriorityComparator());
		this.rumorsToSend = new ValuePrioritySet<>(Message.getIterationsDescComparator(), 
				Message.getIteratorPriorityAscComparator());		
		this.nodes = new DList();
		this.failed = new ValuePrioritySet<>(ValuePriorityEntry.<Node, Long>ascComparator(),
				ValuePriorityEntry.<Node, Long>ascPriorityComparator());		
		this.receivedRumors = new ValuePrioritySet<Message>(Message.getGeneratedTimeAscComparator(),
				Message.getGeneratedTimePriorityAscComparator());

    	
		TimeZone tz = TimeZone.getDefault();
		
		List<String> ids = new ArrayList<String>();
		for(int i = 0; i < 1000000; i++) ids.add(UUID.randomUUID().toString());
		
		Collections.sort(ids);
		
    	for(int i = 0; i < 1000000; i++) {
    		Node n = new Node(ids.get(i), "localhost", 7001, tz);
    		nodes.add(n);
    	}
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( SocketCommunicationTest.class );
    }
    
    public void testConnectionServer1() throws Exception {    	
    	
    	Node to = new Node("A","localhost",7001, TimeZone.getDefault());
    	MembershipClientHandlerTest handler = new MembershipClientHandlerTest("Message", String.class);
    	
    	MembershipServerTest server = new MembershipServerTest(7001,
    			new MembershipServerHandlerTest());
    	ServerRunner serverRunner = new ServerRunner(server);
    	serverRunner.start();
    	Thread.sleep(Config.CONNECTION_TIME_OUT_MS);
    	MembershipClientTest.connect(to, handler);
    	
    	Thread.sleep(Config.CONNECTION_TIME_OUT_MS);
    	assert(handler.getAsserted());
    	server.shutdown();
    	    	
    }
    
    public void testConnectionServer2() throws Exception {    	
    	
    	Node to = new Node("A","localhost",7001, TimeZone.getDefault());
    	
    	ClusterView clusterView = new ClusterView(nodes, suspectingNodesTimeout, failed, rumorsToSend, receivedRumors);
    	
    	MembershipClientHandlerTest handler = new MembershipClientHandlerTest(clusterView, ClusterView.class);
    	
    	MembershipServerTest server = new MembershipServerTest(7001,
    			new MembershipServerHandlerTest());
    	ServerRunner serverRunner = new ServerRunner(server);
    	serverRunner.start();
    	Thread.sleep(Config.CONNECTION_TIME_OUT_MS);
    	MembershipClientTest.connect(to, handler);
    	
    	Thread.sleep(Config.CONNECTION_TIME_OUT_MS);
    	assert(handler.getAsserted());
    	server.shutdown();    	
    }
    
    public void testConnectionServer3() throws Exception {    	
    	
    	Node to = new Node("A","localhost",7001, TimeZone.getDefault());
    	Message message = new Message(MessageType.ADD_TO_CLUSTER, to, 5);
    	
    	MembershipClientHandlerTest handler = new MembershipClientHandlerTest(message, Message.class);
    	
    	MembershipServerTest server = new MembershipServerTest(7001,
    			new MembershipServerHandlerTest());
    	ServerRunner serverRunner = new ServerRunner(server);
    	serverRunner.start();
    	Thread.sleep(Config.CONNECTION_TIME_OUT_MS);
    	MembershipClientTest.connect(to, handler);
    	
    	Thread.sleep(Config.CONNECTION_TIME_OUT_MS);
    	assert(handler.getAsserted());
    	server.shutdown();    	
    }
    
    public void testForwardConnectionServer4() throws Exception {    	
    	
    	
    	
    	Node to = new Node("A","localhost",7001, TimeZone.getDefault());
    	Node forward = new Node("B","localhost",7002, TimeZone.getDefault());    	
    	
    	MembershipServerTest serverFwd = new MembershipServerTest(7002,
    			new MembershipServerForwardHandlerTest(to));
    	ServerRunner serverFwdRunner = new ServerRunner(serverFwd);
    	serverFwdRunner.start();
    	
    	MembershipServerTest server = new MembershipServerTest(7001,
    			new MembershipServerHandlerTest());
    	ServerRunner serverRunner = new ServerRunner(server);
    	serverRunner.start();

    	
    	Thread.sleep(Config.CONNECTION_TIME_OUT_MS);
    	Message message = new Message(MessageType.ADD_TO_CLUSTER, to, 5);
    	MembershipClientHandlerTest handler = new MembershipClientHandlerTest(message, Message.class);
    	MembershipClientTest.connect(forward, handler);
    	
    	Thread.sleep(Config.CONNECTION_TIME_OUT_MS);
    	assert(handler.getAsserted());
    	serverFwd.shutdown();
    	server.shutdown();
    }
    
     
}
