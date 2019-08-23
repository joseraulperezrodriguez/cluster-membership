package org.cluster.membership.tester.util;

import java.io.File;
import java.util.Set;

import org.cluster.membership.protocol.ClusterNodeEntry;

public class Utils {
	
	public static boolean equals(Set<String> s1, Set<String> s2) {
		if(s1.size() != s2.size()) return false;
		
		for(String s : s1) {
			if(!s2.contains(s)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static File createFolder(String path) throws Exception {
		File folder = new File(path);
		if(!folder.exists())folder.mkdir();
		return folder;
	}
	
	public static void main(String[] args) {
		System.out.println(ClusterNodeEntry.class.getCanonicalName());
	}

}
