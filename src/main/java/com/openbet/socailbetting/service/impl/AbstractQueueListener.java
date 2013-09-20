package com.openbet.socailbetting.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.openbet.socailbetting.dao.EventDao;
import com.openbet.socailbetting.dao.NotificationDao;
import com.openbet.socailbetting.dao.impl.SocialBettingRuleDao;

public class AbstractQueueListener {
	
	@Autowired
    @Qualifier("ruleDao")
    private SocialBettingRuleDao socialBettingDao;
		
	@Autowired
    @Qualifier("notificationDao")
    private NotificationDao notificationDao;
	
	@Autowired
    @Qualifier("eventDao")
    private EventDao eventDao;

	public SocialBettingRuleDao getSocialBettingDao() {
		return socialBettingDao;
	}

	public void setSocialBettingDao(SocialBettingRuleDao socialBettingDao) {
		this.socialBettingDao = socialBettingDao;
	}

	public NotificationDao getNotificationDao() {
		return notificationDao;
	}

	public void setNotificationDao(NotificationDao notificationDao) {
		this.notificationDao = notificationDao;
	}

	public EventDao getEventDao() {
		return eventDao;
	}

	public void setEventDao(EventDao eventDao) {
		this.eventDao = eventDao;
	}

}
