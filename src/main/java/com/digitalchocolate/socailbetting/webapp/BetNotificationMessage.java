package com.digitalchocolate.socailbetting.webapp;

import com.digitalchocolate.socailbetting.model.BetNotification;

public class BetNotificationMessage {
	private BetNotification notif;
	private boolean isInsert;
	
	public BetNotification getNotif() {
		return notif;
	}
	public void setNotif(BetNotification notif) {
		this.notif = notif;
	}
	public boolean isInsert() {
		return isInsert;
	}
	public void setInsert(boolean isInsert) {
		this.isInsert = isInsert;
	}
	
}
