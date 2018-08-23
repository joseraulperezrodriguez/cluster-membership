package org.cluster.membership.tester.core;

import java.io.File;

import org.cluster.membership.tester.Config;

public class Runner {
	
	public static void runTemplates() {
		File cases = new File(Config.casesPath);
		for(File f: cases.listFiles()) {
			try {
				Evaluator.evaluate(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void runGenerated() {
		
	}

}
