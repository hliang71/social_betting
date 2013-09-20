package com.openbet.socailbetting.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.openbet.socailbetting.exception.SocialBettingInternalException;
import com.openbet.socailbetting.utils.ErrCode;

public abstract class AbstractEventDao {
	
	@Autowired
    @Qualifier("eventTemplate")
	protected MongoTemplate eventTemplate;

	public MongoTemplate getEventTemplate() {
		return eventTemplate;
	}

	public void setEventTemplate(MongoTemplate eventTemplate) {
		this.eventTemplate = eventTemplate;
	}
	
	public String getProjectIdFromEventId(String eventId)
	{
		String[] tokens = eventId.split(ErrCode.SB_SEPARATER.value());
		if(tokens.length != 3)
		{
			throw new SocialBettingInternalException("getProjectId: invalid eventid format.");
		}
		return tokens[0];
	}
	
	public String getBetTypeFromEventId(String eventId)
	{
		String[] tokens = eventId.split(ErrCode.SB_SEPARATER.value());
		if(tokens.length != 3)
		{
			throw new SocialBettingInternalException("getBetType: invalid eventid format.");
		}
		return tokens[1];
	}
}
