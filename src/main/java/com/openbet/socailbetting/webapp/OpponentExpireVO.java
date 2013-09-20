package com.openbet.socailbetting.webapp;

import java.io.Serializable;

import com.openbet.socailbetting.utils.BetTypeEnum;

public class OpponentExpireVO implements Serializable{

	private static final long serialVersionUID = -410967985423613341L;
	private String eventId;
	private Long projectId;
	private BetTypeEnum betType;
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public BetTypeEnum getBetType() {
		return betType;
	}
	public void setBetType(BetTypeEnum betType) {
		this.betType = betType;
	}
	
	

}
