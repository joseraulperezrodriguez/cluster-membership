package org.cluster.membership.tester;

import org.cluster.membership.tester.config.AbstractEnvConfig;
import org.cluster.membership.tester.core.IEvaluator;

import junit.framework.TestCase;

public abstract class AbstractRunner extends TestCase{
	
	private AbstractEnvConfig appConfig;
	
	private IEvaluator evaluator;
	
	public AbstractRunner(AbstractEnvConfig appConfig, IEvaluator evaluator) {
		this.appConfig = appConfig;
		this.evaluator = evaluator;
	}
	
	public IEvaluator getEvaluator() {
		return evaluator;
	}
	
	public AbstractEnvConfig getAppConfig() {
		return appConfig;
	}
	
	public abstract void runTemplates() throws Exception;
	
	
	
}
