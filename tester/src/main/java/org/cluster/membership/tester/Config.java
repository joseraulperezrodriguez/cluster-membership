package org.cluster.membership.tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

import org.cluster.membership.common.model.util.Tuple2;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

public class Config {

	public static final String homePath = getHomePath();
	public static final String casesPath = homePath + File.separator + "cases";	

	public static final String instancesFolder = "instances";
	public static final String templateFolder = "template";
	public static final String configFolder = "config";
	public static final String appProperties = "app.properties";

	private static String instancesContainer;
	private static String templateContainer;
	private static String programName;

	private static int servicePort = 1;
	private static int protocolPort = 1;

	private static void initPorts(String configPath) throws Exception {
		Properties p = new Properties();
		p.load(new FileInputStream(configPath));		
		servicePort = Integer.parseInt(p.getProperty("server.port"));
		protocolPort = Integer.parseInt(p.getProperty("protocol.port"));
	}

	private static boolean isAvailable(String host, int port) {
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

	private static int getAvailablePort(int port) {
		for(int i = port; i < Short.MAX_VALUE - 1; i++) {
			if(isAvailable("localhost", i)) return i;
		}
		return -1;
	}
	
	public static Tuple2<Integer, Integer> getPorts() {
		int availableForService = getAvailablePort(servicePort);
		if(availableForService == -1) return null;
		
		int availableForProtocol = getAvailablePort(protocolPort);
		if(availableForProtocol == -1) return null;
		
		servicePort = availableForService + 1;
		protocolPort = availableForProtocol + 1;
		
		return new Tuple2<>(availableForService, availableForProtocol);
	}

	private static String getHomePath() {
		ApplicationHome home = new ApplicationHome(TesterEntry.class); 
		String path = home.getDir().getAbsolutePath();
		String configFolderPath = path + File.separator + configFolder;
		File configFolder = new File(configFolderPath);
		if(!configFolder.exists()) return path + File.separator + "target";
		return path;
	}

	public static File createFolder(String path) throws Exception {
		File folder = new File(path);
		if(!folder.exists())folder.mkdir();
		return folder;
	}

	public static void prepareEnvironment(String programPath) throws Exception {
		Config.instancesContainer = homePath + File.separator + instancesFolder;
		Config.templateContainer = instancesContainer + File.separator + templateFolder;

		createFolder(instancesContainer);
		createFolder(templateContainer);

		File sourceProgram = new File(programPath);
		File sourceConfigFolder = new File(homePath + File.separator + configFolder);

		File templateProgram = new File(templateContainer + File.separator + sourceProgram.getName());
		templateProgram.setExecutable(true);
		File templateConfig = new File(templateContainer + File.separator + configFolder);

		if(!templateProgram.exists())templateProgram.createNewFile();

		FileCopyUtils.copy(sourceProgram, templateProgram);
		FileSystemUtils.copyRecursively(sourceConfigFolder, templateConfig);

		initPorts(templateContainer + File.separator + configFolder +
				File.separator + appProperties);

		Config.programName = templateProgram.getName();

	}

	public static void newInstance(String id) throws Exception {
		File folder = new File(Config.instancesContainer + File.separator + id);		
		if(folder.exists()) folder.delete();
		
		File source = new File(Config.templateContainer);

		FileSystemUtils.copyRecursively(source, folder);
	}

	public static void updateConfig(String id, String key, String value) throws Exception {
		String path = instancesContainer + File.separator + id + File.separator + configFolder +
				File.separator + appProperties;

		Properties p = new Properties();
		p.load(new FileInputStream(path));

		p.setProperty(key, value);

		p.store(new FileOutputStream(path), "");		
	}

	public static String programPath(String id) {
		return instancesContainer + File.separator + id + File.separator + programName;
	}

	public static File logPath(String id) {
		return new File(instancesContainer + File.separator + id + File.separator + id + ".log");
	}

	public static File cd(String id) {
		return new File(instancesContainer + File.separator + id);
	}



}
