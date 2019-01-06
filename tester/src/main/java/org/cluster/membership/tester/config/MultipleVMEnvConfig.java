package org.cluster.membership.tester.config;

import java.io.File;

import org.springframework.util.FileCopyUtils;

public class MultipleVMEnvConfig extends LocalEnvConfig {

	private String programName;	
	private String programPath;
	
	public MultipleVMEnvConfig(String homePath, String programPath) throws Exception {
		super(homePath, false);
		super.prepareEnvironment();
		this.programPath = programPath;
		this.prepareEnvironment();
	}
	
	public void prepareEnvironment() throws Exception {
		File sourceProgram = new File(programPath);
		File templateProgram = new File(templateContainer + File.separator + sourceProgram.getName());
		templateProgram.setExecutable(true);
		if(!templateProgram.exists())templateProgram.createNewFile();

		FileCopyUtils.copy(sourceProgram, templateProgram);
		this.programName = templateProgram.getName();

	}
	
	public String getProgramName() {
		return programName;
	}
	
	public String programPath(String id) {
		return instancesContainer + File.separator + id + File.separator + programName;
	}



}
