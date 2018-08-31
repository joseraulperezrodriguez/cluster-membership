package org.cluster.membership.common.model.util;

public class Tuple2<A,B> {
	
	private A a;
	private B b;
	
	public Tuple2(A a, B b) {
		super();
		this.a = a;
		this.b = b;
	}
	
	public A getA() {
		return a;
	}
	public void setA(A a) {
		this.a = a;
	}
	public B getB() {
		return b;
	}
	public void setB(B b) {
		this.b = b;
	}
	
	
	

}
