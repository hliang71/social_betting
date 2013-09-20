package com.openbet.socailbetting.service.impl;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openbet.socailbetting.dao.EventDao;
import com.openbet.socailbetting.dao.impl.SocialBettingRuleDao;
import com.openbet.socailbetting.exception.SocialBettingMessageException;
import com.openbet.socailbetting.model.BetNotification;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.model.TransactionParticipantModel;
import com.openbet.socailbetting.model.TransactionalModel;
import com.openbet.socailbetting.service.BettingMessageProducer;
import com.openbet.socailbetting.utils.NotificationStatusEnum;
import com.openbet.socailbetting.webapp.BetNotificationMessage;

@Service("bettingMessageProducer")
public class BettingMessageProducerImpl implements BettingMessageProducer{
	
	private static final Logger log = Logger.getLogger(BettingMessageProducerImpl.class);
	
	private static final ObjectMapper mapper;
	static
	{
		mapper = new ObjectMapper();
		SerializationConfig sConfig = mapper.getSerializationConfig();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sConfig.setDateFormat(format);
		DeserializationConfig dConfig = mapper.getDeserializationConfig();
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dConfig.setDateFormat(dFormat);
	}
	@Autowired
    @Qualifier("jmsTemplate")
    private JmsTemplate jmsTemplate;
	
	@Autowired
    @Qualifier("ruleDao")
    private SocialBettingRuleDao dao;
	
	@Autowired
    @Qualifier("eventDao")
    private EventDao eventDao;
	
	private String eventQueue;
	private String historyQueue;
	private String callbackQueue;
	
	
	@Override
	@Transactional(timeout=3, rollbackFor=SocialBettingMessageException.class)
	public void atomicSave(List<TransactionParticipantModel> lightweightObject, TransactionalModel event, boolean isInsert) 
	{
		for(TransactionParticipantModel model : lightweightObject)
		{
			BetNotification bn = (BetNotification) model;
			if(bn == null || bn.getBetType() == null || bn.getProjectId() == null)
			{
				throw new SocialBettingMessageException("failed to save the bet notification, project id and bet type are required.");
			}
			bn.setStatus(NotificationStatusEnum.NOTIFIED);
			BetNotificationMessage bnm = new BetNotificationMessage();
			bnm.setInsert(true);
			bnm.setNotif(bn);
			String notfiStr = null;
			try
			{	        
				notfiStr = mapper.writeValueAsString(bnm);
			}catch(Exception e)
			{
				throw new SocialBettingMessageException("Notification is not valid Json.", e);
			}
			this.sendTextMessage(notfiStr, this.jmsTemplate, historyQueue);
			this.sendTextMessage(notfiStr, this.jmsTemplate, callbackQueue);
		}
	
		BettingEvent bEvent = (BettingEvent) event;
		try
		{
			if(isInsert)
			{
				this.eventDao.saveEvent(bEvent);
			}else
			{
				this.eventDao.updateEvent(bEvent);
			}
		}catch(Exception e)
		{
			throw new SocialBettingMessageException("save bet event failed.", e);
		}
	}
	
    private void sendTextMessage(final String messageToSend, JmsTemplate jmsTemplate, String destination)
    {
    	try
    	{
	    	jmsTemplate.send(destination, new MessageCreator() {
	            public Message createMessage(Session session) throws JMSException {
	                return session.createTextMessage( messageToSend );
	            }
	        } );
    	}catch(Exception e)
    	{
    		log.error("send message failed.", e);
    		throw new SocialBettingMessageException("save event and notification failed.", e);
    	}
    }
	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public String getEventQueue() {
		return eventQueue;
	}

	public void setEventQueue(String eventQueue) {
		this.eventQueue = eventQueue;
	}

	public String getHistoryQueue() {
		return historyQueue;
	}

	public void setHistoryQueue(String historyQueue) {
		this.historyQueue = historyQueue;
	}

	public String getCallbackQueue() {
		return callbackQueue;
	}

	public void setCallbackQueue(String callbackQueue) {
		this.callbackQueue = callbackQueue;
	}
	public SocialBettingRuleDao getDao() {
		return dao;
	}
	public void setDao(SocialBettingRuleDao dao) {
		this.dao = dao;
	}

	public EventDao getEventDao() {
		return eventDao;
	}

	public void setEventDao(EventDao eventDao) {
		this.eventDao = eventDao;
	}

}
