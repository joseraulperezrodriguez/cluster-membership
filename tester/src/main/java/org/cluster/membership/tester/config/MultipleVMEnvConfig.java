package org.cluster.membership.tester.config;

import java.io.File;

import org.springframework.util.FileCopyUtils;

public class MultipleVMEnvConfig extends LocalEnvConfig {

	private String programName;	
	private String programPath;
	private int memoryMb;
	
	public MultipleVMEnvConfig(String homePath, String programPath, int memoryMb) throws Exception {
		super(homePath, false);
		this.programPath = programPath;
		this.memoryMb = memoryMb;
		this.prepareEnvironment();
	}
	
	public void prepareEnvironment() throws Exception {
		super.prepareEnvironment();
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

    public int getMemoryMb() {
    	return this.memoryMb;
    }

}
