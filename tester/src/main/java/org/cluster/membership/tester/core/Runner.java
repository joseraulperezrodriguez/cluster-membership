package org.cluster.membership.tester.core;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.tester.Config;

public class Runner {
	
	private static Logger logger = Logger.getLogger(Runner.class.getName());
	
	public static void runTemplates() {
		File cases = new File(Config.casesPath);
		for(File f: cases.listFiles()) {
			try {
				boolean success = new Evaluator().evaluate(f);
				if(success) logger.info("ACCEPTED test for file " + f.getName());
				else logger.log(Level.SEVERE,"failed test for file " + f.getName());
			} catch (Exception e) {
				logger.log(Level.SEVERE, "failed test for file " + f.getName());
				logger.log(Level.SEVERE, "error trace below:");
				e.printStackTrace();
			}
		}
		
	}
	
	public static void runGenerated() {
		
	}

}
