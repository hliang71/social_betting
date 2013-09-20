package com.digitalchocolate.socailbetting.dao.impl;

import org.springframework.stereotype.Repository;

import com.digitalchocolate.socailbetting.dao.AbstractNotificationDao;
import com.digitalchocolate.socailbetting.dao.NotificationDao;


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
