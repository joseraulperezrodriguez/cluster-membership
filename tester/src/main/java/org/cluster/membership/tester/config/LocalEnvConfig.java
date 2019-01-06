package org.cluster.membership.tester.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.cluster.membership.common.model.util.EnvUtils;
import org.cluster.membership.common.model.util.Tuple2;
import org.cluster.membership.tester.util.Utils;
import org.springframework.util.FileSystemUtils;

public class LocalEnvConfig extends AbstractEnvConfig {
	
	public static final String instancesFolder = "instances";
	public static final String templateFolder = "template";
	public static final String configFolder = "config";
	public static final String appProperties = "app.properties";
	
	private String homePath;// = getHomePath();
	private String casesPath;	

	protected String instancesContainer;
	protected String templateContainer;

	private int servicePort = 1;
	private int protocolPort = 1;

	public LocalEnvConfig(String homePath, boolean prepare) throws Exception {
		super();
		this.homePath = homePath;
		this.casesPath = homePath + File.separator + "cases";
		if(prepare) this.prepareEnvironment();

	}
	
	public void newInstance(String id) throws Exception {
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
	}

	public void prepareEnvironment() throws Exception {
		this.instancesContainer = homePath + File.separator + instancesFolder;
		this.templateContainer = instancesContainer + File.separator + templateFolder;

		Utils.createFolder(instancesContainer);
		Utils.createFolder(templateContainer);

		File sourceConfigFolder = new File(homePath + File.separator + configFolder);
		File templateConfig = new File(templateContainer + File.separator + configFolder);

		FileSystemUtils.copyRecursively(sourceConfigFolder, templateConfig);

		initPorts(templateContainer + File.separator + configFolder +
				File.separator + appProperties);

	}
	
	public Tuple2<Integer, Integer> getPorts() {
		int availableForService = EnvUtils.getLocalAvailablePort(servicePort);
		if(availableForService == -1) return null;
		
		int availableForProtocol = EnvUtils.getLocalAvailablePort(protocolPort);
		if(availableForProtocol == -1) return null;
		
		servicePort = availableForService + 1;
		protocolPort = availableForProtocol + 1;
		
		return new Tuple2<>(availableForService, availableForProtocol);
	}
	
	public void initPorts(String configPath) throws Exception {
		Properties p = new Properties();
		p.load(new FileInputStream(configPath));
		
		servicePort = Integer.parseInt(p.getProperty("server.port"));
		protocolPort = Integer.parseInt(p.getProperty("protocol.port"));
	}

	public File logPath(String id) {
		return new File(instancesContainer + File.separator + id + File.separator + id + ".log");
	}

	public File cd(String id) {
		return new File(instancesContainer + File.separator + id);
	}
	
	public String getCasesPath() {
		return casesPath;
	}
}
