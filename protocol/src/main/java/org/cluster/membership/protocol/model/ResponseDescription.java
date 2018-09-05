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
	
	private SynchroObject syncObject;
	
	private List<MessageResponse<? extends Serializable>> reponses;

	public ResponseDescription(SynchroObject syncMessages,
			List<MessageResponse<? extends Serializable>> reponses) {
		super();
		this.reponses = reponses;
		this.syncObject = syncMessages;
	}
	
	@SafeVarargs
	public ResponseDescription(SynchroObject syncMessages, 
			MessageResponse<? extends Serializable>... responses) {
		super();
		this.reponses = Arrays.asList(responses);
		this.syncObject = syncMessages;
	}

	public SynchroObject getSyncObject() {
		return syncObject;
	}

	public void setSyncObject(SynchroObject syncMessages) {
		this.syncObject = syncMessages;
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
