package com.openbet.socailbetting.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.client.RestTemplate;

import com.mongodb.WriteConcern;
import com.openbet.socailbetting.exception.SocialBettingInternalException;
import com.openbet.socailbetting.model.BetNotification;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.model.LogItem;
import com.openbet.socailbetting.model.SocialBettingTransaction;
import com.openbet.socailbetting.model.TransactionParticipantModel;
import com.openbet.socailbetting.model.TransactionalModel;
import com.openbet.socailbetting.utils.NotificationStatusEnum;
import com.openbet.socailbetting.utils.TransactionStatusEnum;

/**
 * 
 * @author hliang
 */

public abstract class AbstractBaseDao {
	private static final Logger LOG = Logger.getLogger(AbstractBaseDao.class);
	@Autowired
    @Qualifier("mongoTemplate")
	protected MongoTemplate mongoTemplate;

	public void setMongoTemplate(MongoTemplate template) {
	    this.mongoTemplate = template;
	    this.mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
	    this.mongoTemplate.setWriteConcern(WriteConcern.SAFE);
	}
	
	public MongoTemplate getMongoTemplate() {
	    return this.mongoTemplate;
	}
    public String startTransaction()
    {
    	SocialBettingTransaction tx = new SocialBettingTransaction();
    	String txId = UUID.randomUUID().toString();
    	tx.setId(txId);
    	Integer day = null;
    	try
    	{
    		this.mongoTemplate.insert(tx);
    		SocialBettingTransaction tx2 = this.mongoTemplate.findOne(new Query(Criteria.where("status").is(TransactionStatusEnum.NEW).and("id").is(txId)), SocialBettingTransaction.class);
    		if(tx2 == null)
    		{
    			throw new SocialBettingInternalException("did not insert tx correctly.");
    		}
    		day=tx2.getDay();
    	}catch(Exception e)
    	{
    		throw new SocialBettingInternalException("failed to start a new transaction.", e);
    	}
    	
    	return txId+"SOCIAL"+day;
    }
    public void commitTransaction(String txId)
    {
    	try
    	{
    		String[] tokens = txId.split("SOCIAL");
    		String tid = tokens[0];
    		String dayStr = tokens[1];
    		int day = Integer.valueOf(dayStr);
    		LOG.info("tid is "+tid+" day is"+day);
			mongoTemplate.updateFirst(new Query(Criteria.where("status").is(TransactionStatusEnum.NEW).and("id").is(tid).and("day").is(day)), Update.update("status", TransactionStatusEnum.COMMITED.value()), SocialBettingTransaction.class);						

    	}catch(Exception e)
    	{
    		LOG.error("failed commit.", e);
    		throw new SocialBettingInternalException("failed to commit the transaction", e);
    	}
    }
    
	public void atomicSave(List<TransactionParticipantModel> lightweightObject, TransactionalModel event, boolean isInsert)
	{
		try
		{
			String txId = this.startTransaction();
			String[] tokens = txId.split("SOCIAL");
    		String tid = tokens[0];
			for(TransactionParticipantModel ob : lightweightObject)
			{
				ob.setTransactionId(tid);
				ob.setTransactionStartTime(new Date());
			}
			this.mongoTemplate.insertAll(lightweightObject);
			List<String> transactionIds = event.getTransactionIds();
			transactionIds.add(txId);
			if(isInsert)
			{
				mongoTemplate.insert(event);
			}else
			{
				if(event instanceof BettingEvent)
				{
					BettingEvent ev = (BettingEvent) event;
					ev.setProjectId(null);
					ev.setBetType(null);
					ev.setDay(null);
					//ev.setId(null);
					mongoTemplate.save(ev);
				}else
				{
					mongoTemplate.save(event);
				}
				
			}
			try
			{
				this.commitTransaction(txId);
			}catch(Exception e)
			{
				LOG.info("transaction commit failed but event update correctly. "+e.toString());
			}	
		}catch(Exception e)
		{
			throw new SocialBettingInternalException("failed to update the event and notificaiton.", e);
		}
		
	}
	
	/**
	 * no exception should be thrown from this method.
	 * @param bn
	 * @param restTemplate
	 * @param callbackUrl
	 */
	public void atomicPost(BetNotification bn, RestTemplate restTemplate, String callbackUrl)
	{
		boolean doPost = false;
		Integer day = null;
		try
		{
			try
			{
				bn.setStatus(NotificationStatusEnum.NOTIFIED);
				day = bn.getDay();
				bn.setDay(null);
				mongoTemplate.save(bn);
				//String id = bn.getId();
				//Integer day = bn.getDay();
				//mongoTemplate.updateFirst(new Query(Criteria.where("status").is(NotificationStatusEnum.NEW.value()).and("id").is(id).and("day").is(day)), Update.update("status", NotificationStatusEnum.NOTIFIED.value()), BetNotification.class);						

				doPost = true;
			}catch(Exception e)
			{
				LOG.info("update notification status failed. no notification message going to send to GS!", e);
			}
			if(doPost)
			{
				Map parameters = null;
				try
				{
					parameters = PropertyUtils.describe(bn);
				}catch(Exception e)
				{
					if(LOG.isDebugEnabled()) LOG.debug("failed to get some of the fields.", e);
				}
				if(parameters == null)
				{
					throw new SocialBettingInternalException( "failed to post, no parameters.");

				}
				String response = restTemplate.getForObject(callbackUrl, String.class, parameters);
				if(LOG.isDebugEnabled()) LOG.debug("###########RESPONSE IS:"+response);
			}
		}catch(Exception e)
		{
			LOG.error("callback post failed, rolling back", e);
			Exception lastException = null;
			try
			{
				boolean rollbacked = false;
				int counter = 0;
				
				while(!rollbacked && counter < 20)
				{
					try
					{
						String id = bn.getId();	
						
						BetNotification bn1 = mongoTemplate.findOne(new Query(Criteria.where("day").is(day).and("id").is(id)), BetNotification.class);
						bn1.setStatus(NotificationStatusEnum.NEW);
						bn1.setDay(null);
						mongoTemplate.save(bn1);
						
						//mongoTemplate.updateFirst(new Query(Criteria.where("status").is(NotificationStatusEnum.NOTIFIED.value()).and("id").is(id).and("day").is(day)), Update.update("status", NotificationStatusEnum.NEW.value()), BetNotification.class);						
						rollbacked = true;
						
					}catch(Exception ef)
					{
						if(LOG.isTraceEnabled()) LOG.trace("rollback attempt failed: sleep one second....."+ef);
						LOG.info("rollback attempt failed: sleep one second.....",ef);
						lastException = ef;
					}
					try
					{
						if(!rollbacked)
						{
							Thread.sleep(1000);
						}
					}catch(InterruptedException eg)
					{
						if (LOG.isTraceEnabled()) LOG.trace("Interrupted!", eg);
					}
					counter++;
				}
				if(!rollbacked)
				{
					throw lastException;
				}
				
			}catch(Exception ex)
			{
				LOG.error("############################################ROLL BACK NOTIFICATION FAILED#################################################");
				LOG.error("eventId is: "+bn.getSbp_event_id()+" project id is: "+bn.getProjectId()+" betType is: "+bn.getBetType()+" notification id is: "+bn.getId()+" transaction id is: "+bn.getTransactionId()+" platform user id is: "+bn.getP_uid()+" sbp_call_type: "+bn.getSbp_call_type().value()+" sbp_call_detail: "+bn.getSbp_call_detail());
				LOG.error("############################################END OF THE FAIL MESSAGE#######################################################");
				LOG.error("Exception Details:", ex);
			}
		}
	}
	
}
