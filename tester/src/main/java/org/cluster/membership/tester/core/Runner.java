package org.cluster.membership.tester.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cluster.membership.tester.Config;

public class Runner {
	
	private static Logger logger = Logger.getLogger(Runner.class.getName());
	
	public static List<Process> runningProcess = new ArrayList<Process>();
	
	private static void kill(Process p) { p.destroyForcibly(); }
	
	private static void killProcesses() {
		for(Process p : runningProcess)  kill(p);				
		runningProcess.clear();
	}
			
	public static void runTemplates() throws Exception {
		File cases = new File(Config.casesPath);
				
		File[] sortedByName = cases.listFiles();
		Arrays.sort(sortedByName, (a, b) -> a.getName().compareTo(b.getName()));
		
		for(File f: sortedByName) {
			try {
				boolean success = new Evaluator().evaluate(f);
				if(success) logger.info("PASSED test for file " + f.getName());
				else logger.log(Level.SEVERE,"FAILED test for file " + f.getName());
			} catch (Exception e) {
				logger.log(Level.SEVERE, "FAILED test for file " + f.getName());
				logger.log(Level.SEVERE, "error trace below:");
				e.printStackTrace();
			}
			killProcesses();
		}
		
	}
	
	public static void runGenerated() {
		
	}

}
