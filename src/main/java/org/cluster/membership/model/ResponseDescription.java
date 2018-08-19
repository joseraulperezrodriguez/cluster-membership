package org.cluster.membership.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ResponseDescription implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long responseTime;
	
	private List<MessageResponse<?>> reponses;

	public ResponseDescription(List<MessageResponse<?>> reponses) {
		super();
		this.reponses = reponses;
	}
	
	public ResponseDescription(MessageResponse<?>... responses) {
		super();
		this.reponses = Arrays.asList(responses);
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long time) {
		this.responseTime = time;
	}

	public List<MessageResponse<?>> getReponses() {
		return reponses;
	}

	public void setReponses(List<MessageResponse<?>> reponses) {
		this.reponses = reponses;
	}
	
	
	

}
