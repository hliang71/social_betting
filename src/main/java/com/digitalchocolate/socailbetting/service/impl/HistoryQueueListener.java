package com.digitalchocolate.socailbetting.service.impl;

import java.text.SimpleDateFormat;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.digitalchocolate.socailbetting.dao.NotificationDao;
import com.digitalchocolate.socailbetting.exception.SocialBettingMessageException;
import com.digitalchocolate.socailbetting.model.BetNotification;
import com.digitalchocolate.socailbetting.utils.NotificationStatusEnum;
import com.digitalchocolate.socailbetting.webapp.BetNotificationMessage;

@Component
public class HistoryQueueListener extends AbstractQueueListener implements MessageListener{
    private static final Logger log = Logger.getLogger(HistoryQueueListener.class);
	@Override
	public void onMessage(Message message) {
		try
    	{
	        TextMessage textMsg = (TextMessage) message;
	        String notificationMsg = textMsg.getText();
	        ObjectMapper mapper = new ObjectMapper();
	        SerializationConfig sConfig = mapper.getSerializationConfig();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sConfig.setDateFormat(format);
			DeserializationConfig dConfig = mapper.getDeserializationConfig();
			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dConfig.setDateFormat(dFormat);
			BetNotificationMessage notiMsg = mapper.readValue(notificationMsg, BetNotificationMessage.class);
			BetNotification notif = notiMsg.getNotif();
			notif.setStatus(NotificationStatusEnum.NOTIFIED);
			NotificationDao notificationDao = this.getNotificationDao();
			if(notiMsg.isInsert())
			{
				notificationDao.insert(notif);
			}else
			{
				notif.setDay(null);
				notificationDao.save(notif);
			}
				
    	}catch(Exception e)
    	{
    		log.info("jms listener exception:"+e.toString());
    		if(!(e instanceof DuplicateKeyException))
    		{
    			log.error("save notificaion failed. rolling back", e);
    			throw new SocialBettingMessageException("save notification failed!", e);
    		}
    		//log.error("save notificaion failed. rolling back", e);
			//throw new SocialBettingMessageException("save notification failed!", e);
    	}  
	}

}
