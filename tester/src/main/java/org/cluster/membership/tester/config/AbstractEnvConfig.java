package org.cluster.membership.tester.config;

import java.util.Properties;

public abstract class AbstractEnvConfig {

	/*public static final String instancesFolder = "instances";
	public static final String templateFolder = "template";
	public static final String configFolder = "config";
	public static final String appProperties = "app.properties";
	
	public String homePath;// = getHomePath();
	public String casesPath;	

	protected String instancesContainer;
	protected String templateContainer;
	//private String programName;

	private int servicePort = 1;
	private int protocolPort = 1;*/
	
	protected Properties properties;
	
	public AbstractEnvConfig(/*String homePath*//*, String programPath*/) /*throws Exception*/ {
	}

	/*public String getProgramName() {
		return programName;
	}*/
	
	/*public void initPorts(String configPath) throws Exception {
		Properties p = new Properties();
		p.load(new FileInputStream(configPath));
		
		servicePort = Integer.parseInt(p.getProperty("server.port"));
		protocolPort = Integer.parseInt(p.getProperty("protocol.port"));
	}*/

	
	/*public Tuple2<Integer, Integer> getPorts() {
		int availableForService = EnvUtils.getLocalAvailablePort(servicePort);
		if(availableForService == -1) return null;
		
		int availableForProtocol = EnvUtils.getLocalAvailablePort(protocolPort);
		if(availableForProtocol == -1) return null;
		
		servicePort = availableForService + 1;
		protocolPort = availableForProtocol + 1;
		
		return new Tuple2<>(availableForService, availableForProtocol);
	}*/

	public abstract void prepareEnvironment() throws Exception; 
	/*public void newInstance(String id) throws Exception {
		File folder = new File(this.instancesContainer + File.separator + id);		
		if(folder.exists()) folder.delete();
		
		File source = new File(this.templateContainer);

		FileSystemUtils.copyRecursively(source, folder);
	}

	public void updateConfig(String id, String key, String value) throws Exception {
		String path = instancesContainer + File.separator + id + File.separator + configFolder +
				File.separator + appProperties;

		Properties p = new Properties();
		p.load(new FileInputStream(path));
		p.setProperty(key, value);
		p.store(new FileOutputStream(path), "date: " + System.currentTimeMillis());	
		
		if(id.equals(templateFolder)) properties = p;
	}*/
	
	
	public String getProp(String key) {
		return properties.getProperty(key);		
	}
	
	/*public String programPath(String id) {
		return instancesContainer + File.separator + id + File.separator + programName;
	}*/

	/*public File logPath(String id) {
		return new File(instancesContainer + File.separator + id + File.separator + id + ".log");
	}

	public File cd(String id) {
		return new File(instancesContainer + File.separator + id);
	}*/



}
