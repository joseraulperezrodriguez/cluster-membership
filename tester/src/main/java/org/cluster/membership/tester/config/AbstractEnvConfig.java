package org.cluster.membership.tester.config;

import java.util.Properties;

public abstract class AbstractEnvConfig {

	protected Properties properties;
	
	public abstract void prepareEnvironment() throws Exception; 
	
	public String getProp(String key) {
		return properties.getProperty(key);		
	}

}
