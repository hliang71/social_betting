package com.openbet.socailbetting.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

public abstract class AbstractNotificationDao {
		
	@Autowired
    @Qualifier("notificationTemplate")
	protected MongoTemplate notificationTemplate;

	public MongoTemplate getNotificationTemplate() {
		return notificationTemplate;
	}

	public void setNotificationTemplate(MongoTemplate notificationTemplate) {
		this.notificationTemplate = notificationTemplate;
	}
	
}
