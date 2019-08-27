package org.cluster.membership.protocol.net.test;

import java.util.TimeZone;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.ClusterNodeEntryTest;
import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.core.MessageType;
import org.cluster.membership.protocol.model.Message;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class SocketCommunicationTest extends ClusterNodeEntryTest {
	
	private TimeZone timeZone = TimeZone.getDefault();
	
	private Config config = new Config(Node.getGreaterNode());
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SocketCommunicationTest( String testName ) throws Exception {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( SocketCommunicationTest.class );
    }
    
    public void testConnectionServer1() throws Exception {    	
    	
    	Node to = new Node("A","localhost",7001, 6001);
    	MembershipClientHandlerTest handler = new MembershipClientHandlerTest("Message", String.class);
    	
    	MembershipServerTest server = new MembershipServerTest(7001,
    			new MembershipServerHandlerTest(), config);
    	ServerRunner serverRunner = new ServerRunner(server);
    	serverRunner.start();
    	Thread.sleep(config.getConnectionTimeOutMs());
    	MembershipClientTest.connect(to, handler, config);
    	
    	Thread.sleep(config.getConnectionTimeOutMs());
    	assert(handler.getAsserted());
    	server.shutdownSync();
    	    	
    }
        
    public void testConnectionServer2() throws Exception {    	
    	
    	Node to = new Node("A","localhost",7001, 6001);
    	Message message = new Message(MessageType.ADD_TO_CLUSTER, to, 5, timeZone);
    	
    	MembershipClientHandlerTest handler = new MembershipClientHandlerTest(message, Message.class);
    	
    	MembershipServerTest server = new MembershipServerTest(7001,
    			new MembershipServerHandlerTest(), config);
    	ServerRunner serverRunner = new ServerRunner(server);
    	serverRunner.start();
    	Thread.sleep(config.getConnectionTimeOutMs());
    	MembershipClientTest.connect(to, handler, config);
    	
    	Thread.sleep(config.getConnectionTimeOutMs());
    	assert(handler.getAsserted());
    	server.shutdownSync();    	
    }
    
    public void testForwardConnectionServer3() throws Exception {    	
    	Node to = new Node("A","localhost",7001, 6001);
    	Node forward = new Node("B","localhost",7002, 6001);    	
    	
    	MembershipServerTest serverFwd = new MembershipServerTest(7002,
    			new MembershipServerForwardHandlerTest(to, config), config);
    	ServerRunner serverFwdRunner = new ServerRunner(serverFwd);
    	serverFwdRunner.start();
    	
    	MembershipServerTest server = new MembershipServerTest(7001,
    			new MembershipServerHandlerTest(), config);
    	ServerRunner serverRunner = new ServerRunner(server);
    	serverRunner.start();

    	
    	Thread.sleep(config.getConnectionTimeOutMs());
    	Message message = new Message(MessageType.ADD_TO_CLUSTER, to, 5, timeZone);
    	MembershipClientHandlerTest handler = new MembershipClientHandlerTest(message, Message.class);
    	MembershipClientTest.connect(forward, handler, config);
    	
    	Thread.sleep(config.getConnectionTimeOutMs());
    	assert(handler.getAsserted());
    	serverFwd.shutdownSync();
    	server.shutdownSync();
    }
    
}
