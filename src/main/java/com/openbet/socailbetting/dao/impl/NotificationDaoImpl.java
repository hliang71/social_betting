package com.openbet.socailbetting.dao.impl;

import org.springframework.stereotype.Repository;

import com.openbet.socailbetting.dao.AbstractNotificationDao;
import com.openbet.socailbetting.dao.NotificationDao;


@Repository("notificationDao")
public class NotificationDaoImpl extends AbstractNotificationDao implements NotificationDao{

	@Override
	public void insert(Object entity) {
		this.notificationTemplate.insert(entity);
		
	}

	@Override
	public void save(Object entity) {
		this.notificationTemplate.save(entity);
	}


}
