package org.cluster.membership.net.test;

public class ServerRunner extends Thread {

	public MembershipServerTest testServer;
	
	public ServerRunner(MembershipServerTest testServer) {
		
		this.testServer = testServer;
	}
	
	@Override
	public void run() {
		this.testServer.listen();
	}
	
}
