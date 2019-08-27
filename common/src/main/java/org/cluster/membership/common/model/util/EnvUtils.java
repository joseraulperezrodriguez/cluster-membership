package org.cluster.membership.common.model.util;

import java.io.File;
import java.net.Socket;
import java.net.SocketException;

import org.cluster.membership.common.model.Node;
import org.springframework.boot.system.ApplicationHome;

public class EnvUtils {

	public static boolean isAvailable(String host, int port) {
		try {
			(new Socket(host, port)).close();
			return false;
		}
		catch(SocketException e) { return true; } 
		catch(Exception e) { return false; }		  
	}
	
	public static boolean isListening(String host, int port) {
		try {
			(new Socket(host, port)).close();
			return true;
		}
		catch(SocketException e) { return false; } 
		catch(Exception e) { return false; }		  
	}

	public static int getLocalAvailablePort(int port) {
		for(int i = port; i < Short.MAX_VALUE - 1; i++) {
			if(isAvailable("localhost", i)) return i;
		}
		return -1;
	}
	

	public static String getHomePath(Class<?> clasz, String configFolderName) {
		ApplicationHome home = new ApplicationHome(clasz); 
		String path = home.getDir().getAbsolutePath();
		String configFolderPath = path + File.separator + configFolderName;
		File configFolder = new File(configFolderPath);
		if(!configFolder.exists()) return path + File.separator + "target";
		return path;
	}
	
	public static void waitUntilStarted(String address, int servicePort, int protocolPort) throws Exception {
		do {
			Thread.sleep(1000);			
		} while(!EnvUtils.isListening(address, servicePort) || 
				!EnvUtils.isListening(address, protocolPort));
	}

	public static String generateNodeCommandLineArguments(Node node, int idx) {
		return " --" + Literals.NODE_ID + "." + idx + "=" + node.getId() +
				" --" + Literals.NODE_ADDRESS + "." + idx + "=" + node.getAddress() +
				" --" + Literals.NODE_PROTOCOL_PORT + "." + idx + "=" + node.getProtocolPort() +
				" --" + Literals.NODE_SERVER_PORT + "." + idx + "=" + node.getServicePort();
	}

	
}
