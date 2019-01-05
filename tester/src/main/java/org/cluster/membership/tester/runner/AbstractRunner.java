package org.cluster.membership.tester.runner;

import org.cluster.membership.tester.config.AbstractEnvConfig;

public abstract class AbstractRunner {
	
	private AbstractEnvConfig appConfig;
	
	public AbstractRunner(AbstractEnvConfig appConfig) {
		this.appConfig = appConfig;
	}
	
	public AbstractEnvConfig getAppConfig() {
		return appConfig;
	}
	
	public abstract void runTemplates() throws Exception;
	
}
