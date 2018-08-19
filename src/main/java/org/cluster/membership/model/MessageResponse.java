package org.cluster.membership.model;

import java.io.Serializable;

public class MessageResponse<T extends Serializable> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private T response;	

	private Message message;
	
	public MessageResponse(T response, Message message) {
		super();
		this.response = response;
		this.message = message;
	}
	
	public T getResponse() {
		return response;
	}

	public void setResponse(T response) {
		this.response = response;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
	

}
