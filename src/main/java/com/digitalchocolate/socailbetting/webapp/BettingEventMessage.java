package com.digitalchocolate.socailbetting.webapp;

import com.digitalchocolate.socailbetting.model.BettingEvent;

public class BettingEventMessage {
	private BettingEvent event;
	private boolean isInsert;
	public BettingEvent getEvent() {
		return event;
	}
	public void setEvent(BettingEvent event) {
		this.event = event;
	}
	public boolean isInsert() {
		return isInsert;
	}
	public void setInsert(boolean isInsert) {
		this.isInsert = isInsert;
	}
	
	
}
