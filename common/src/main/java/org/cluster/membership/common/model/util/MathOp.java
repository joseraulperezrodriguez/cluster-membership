package org.cluster.membership.common.model.util;

public class MathOp {

	public static int log2n(int n) {
		int base = 2;
		int iterations = 0;		
		while((base*=2) <= n*2) iterations++;		
		return iterations + 1;		
	}
	
	public static int waitTime(int size, int interval) {
		int iterations = MathOp.log2n(size) + 1;
		int time = iterations * interval;
		return (int)(time * 1.25);
	}
	
}
