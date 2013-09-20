package com.openbet.socailbetting.webapp;

import java.util.ArrayList;
import java.util.List;

public class UpdateNotifyStatusRequestBean extends BaseRequestDTO{
	private List<String> eventIds = new ArrayList<String>();

	public List<String> getEventIds() {
		return eventIds;
	}

	public void setEventIds(List<String> eventIds) {
		this.eventIds = eventIds;
	}

	@Override
	public String getRequestContent() {
		return null;
	}
}
