package org.cluster.membership.tester;

import java.io.File;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

public class Config {
		
	public static final String homePath = getHomePath();
	public static final String casesPath = homePath + File.separator + "cases";
	
	private static final String protocolFolder = "protocol";	
	private static final String configFolder = "config";
	
	public static String programPath;
	public static String appPropertiesPath;
	public static String peerPropertiesPath;
	
	private static String getHomePath() {
		ApplicationHome home = new ApplicationHome(TesterEntry.class); 
		String path = home.getDir().getAbsolutePath();
		String configFolderPath = path + File.separator + configFolder;
		File configFolder = new File(configFolderPath);
		if(!configFolder.exists()) return path + File.separator + "target";
		return path;
	}
	
	
	public static void prepareEnvironment(String programPath) throws Exception {
		String protocolContainer = homePath + File.separator + protocolFolder;
		
		File template = new File(protocolContainer);
		if(!template.exists())template.mkdir();
		
		File sourceProgram = new File(programPath);
		File sourceConfigFolder = new File(sourceProgram.getParent() + File.separator + configFolder);
						
		File templateProgram = new File(protocolContainer + File.separator + sourceProgram.getName());
		File templateConfig = new File(protocolContainer + File.separator + configFolder);
		
		if(!templateProgram.exists())templateProgram.createNewFile();
		
		FileCopyUtils.copy(sourceProgram, templateProgram);
		FileSystemUtils.copyRecursively(sourceConfigFolder, templateConfig);
		
		Config.programPath = templateProgram.getAbsolutePath();
		Config.appPropertiesPath = protocolContainer + File.separator + configFolder + File.separator + "app.properties";
		Config.peerPropertiesPath = protocolContainer + File.separator + configFolder + File.separator + "peer.properties";
		
	}

}
