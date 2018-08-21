package org.cluster.membership.protocol.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ResponseDescription implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long responseTime;
	
	private List<MessageResponse<? extends Serializable>> reponses;

	public ResponseDescription(List<MessageResponse<? extends Serializable>> reponses) {
		super();
		this.reponses = reponses;
	}
	
	@SafeVarargs
	public ResponseDescription(MessageResponse<? extends Serializable>... responses) {
		super();
		this.reponses = Arrays.asList(responses);
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long time) {
		this.responseTime = time;
	}

	public List<MessageResponse<? extends Serializable>> getReponses() {
		return reponses;
	}

	public void setReponses(List<MessageResponse<? extends Serializable>> reponses) {
		this.reponses = reponses;
	}
	
	
	

}
