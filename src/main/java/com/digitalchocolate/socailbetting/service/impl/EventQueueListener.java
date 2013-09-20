package com.digitalchocolate.socailbetting.service.impl;

import java.text.SimpleDateFormat;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Component;

import com.digitalchocolate.socailbetting.dao.EventDao;
import com.digitalchocolate.socailbetting.exception.SocialBettingMessageException;
import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.webapp.BettingEventMessage;

@Component
public class EventQueueListener extends AbstractQueueListener implements MessageListener
{
	private static final Logger log = Logger.getLogger(EventQueueListener.class);
    public void onMessage( final Message message )
    {
    	try
    	{
	        TextMessage textMsg = (TextMessage) message;
	        String eventMsg = textMsg.getText();
	        ObjectMapper mapper = new ObjectMapper();
	        SerializationConfig sConfig = mapper.getSerializationConfig();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sConfig.setDateFormat(format);
			DeserializationConfig dConfig = mapper.getDeserializationConfig();
			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dConfig.setDateFormat(dFormat);
			BettingEventMessage evMsg = mapper.readValue(eventMsg, BettingEventMessage.class);
			BettingEvent event = evMsg.getEvent();
			EventDao eventDao = this.getEventDao();
			if(evMsg.isInsert())
			{
				eventDao.saveEvent(event);
			}else
			{
				eventDao.updateEvent(event);
			}
				
    	}catch(Exception e)
    	{
    		log.error("save event failed. rolling back", e);
    		throw new SocialBettingMessageException("save event failed!", e);
    	}   
    }
}