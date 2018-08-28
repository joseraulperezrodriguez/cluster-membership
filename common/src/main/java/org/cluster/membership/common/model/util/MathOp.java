package org.cluster.membership.common.model.util;

public class MathOp {

	public static int log2n(int n) {
		int base = 2;
		int iterations = 0;
		
		while((base*=2) <= n*2) iterations++;
		
		return iterations + 1;
		
	}
	
}
