package org.cluster.membership.protocol.net.test;

import java.util.TimeZone;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.core.MessageType;
import org.cluster.membership.protocol.model.Message;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class SocketCommunicationTest extends TestCase {
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SocketCommunicationTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( SocketCommunicationTest.class );
    }
    
    public void testConnectionServer1() throws Exception {    	
    	
    	Node to = new Node("A","localhost",7001, 6001,TimeZone.getDefault());
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
    	
    	Node to = new Node("A","localhost",7001, 6001, TimeZone.getDefault());
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
    
    public void testForwardConnectionServer3() throws Exception {    	
    	Node to = new Node("A","localhost",7001, 6001,TimeZone.getDefault());
    	Node forward = new Node("B","localhost",7002, 6001, TimeZone.getDefault());    	
    	
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
