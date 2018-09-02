package org.cluster.membership.protocol.model;

import java.io.Serializable;

public class FrameMessageCount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long startTime;
	
	private Long endTime;
	
	private Integer count;

	public FrameMessageCount(Long startTime, Long endTime, Integer count) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.count = count;
	}
	
	public FrameMessageCount() {}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	
	
	

}
