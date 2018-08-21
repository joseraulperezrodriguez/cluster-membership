package org.cluster.membership.tester;

import java.io.File;

public class Config {
	
	public static String programFolder;
	
	public static String programPath;
	
	public static String configFolder;
	
	public static String appConfigPath;
	
	public static String peerConfigPath;
	
	
	public static void read(String programPath) throws Exception {
		
		Config.programPath = programPath;
		
		File file = new File(programPath);
		
		programFolder = file.getParent();
		
		configFolder = programFolder + File.separator + "config";
		
		appConfigPath = configFolder + File.separator + "app.properties";
		
		peerConfigPath = configFolder + File.separator + "peer.properties";
		
		
		
	}

}
