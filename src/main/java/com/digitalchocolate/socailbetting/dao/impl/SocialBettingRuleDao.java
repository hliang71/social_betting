package com.digitalchocolate.socailbetting.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.digitalchocolate.socailbetting.dao.AbstractBaseDao;
import com.digitalchocolate.socailbetting.dao.BaseRuleDao;
import com.digitalchocolate.socailbetting.exception.SocialBettingInternalException;
import com.digitalchocolate.socailbetting.exception.SocialBettingValidationException;
import com.digitalchocolate.socailbetting.executor.DefaultCallbackCommand;
import com.digitalchocolate.socailbetting.model.Bet;
import com.digitalchocolate.socailbetting.model.BetNotification;
import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.model.BettingLevel;
import com.digitalchocolate.socailbetting.model.DBLock;
import com.digitalchocolate.socailbetting.model.ProjectKeys;
import com.digitalchocolate.socailbetting.model.ProjectPlatformLookUp;
import com.digitalchocolate.socailbetting.model.SocialBettingTransaction;
import com.digitalchocolate.socailbetting.model.rule.BaseRule;
import com.digitalchocolate.socailbetting.model.rule.CallbackRule;
import com.digitalchocolate.socailbetting.model.rule.OddsRule;
import com.digitalchocolate.socailbetting.model.rule.OpponentRule;
import com.digitalchocolate.socailbetting.model.rule.SettlementRule;
import com.digitalchocolate.socailbetting.utils.BetHistoryStatusEnum;
import com.digitalchocolate.socailbetting.utils.BetTypeEnum;
import com.digitalchocolate.socailbetting.utils.ErrCode;
import com.digitalchocolate.socailbetting.utils.NotificationStatusEnum;
import com.digitalchocolate.socailbetting.utils.RuleNamesEnum;
import com.digitalchocolate.socailbetting.utils.StatusEnum;

@Repository("ruleDao")
public class SocialBettingRuleDao extends AbstractBaseDao implements BaseRuleDao{
	private static final Logger log = Logger.getLogger(SocialBettingRuleDao.class);
	private static final int LOCK_FACTOR = 100; 
	private static final Integer LIMIT = 10;
	@Override
	public BaseRule getRule(RuleNamesEnum name, Long projectId,  BetTypeEnum betType)
	{
		if(log.isDebugEnabled()) log.debug("get rule for "+ name.value());
		if(betType == null)
		{
			throw new SocialBettingValidationException(ErrCode.INVALID_BET_TYPE, "betType is invalide.");
		}
		
		BaseRule rule = null;
		try
		{			
			if(name == RuleNamesEnum.ODDS)
			{
				rule = mongoTemplate.findOne(new Query(Criteria.where("projectId").is(projectId).and("name").is(name.value()).and("betType").is(betType.value())), OddsRule.class);
			}else if(name == RuleNamesEnum.OPPONENT)
			{
				rule = mongoTemplate.findOne(new Query(Criteria.where("projectId").is(projectId).and("name").is(name.value()).and("betType").is(betType.value())), OpponentRule.class);

			}else if(name == RuleNamesEnum.SETTLEMENT)
			{
				rule = mongoTemplate.findOne(new Query(Criteria.where("projectId").is(projectId).and("name").is(name.value()).and("betType").is(betType.value())), SettlementRule.class);
			}else if(name == RuleNamesEnum.CALLBACK)
			{
				rule = mongoTemplate.findOne(new Query(Criteria.where("projectId").is(projectId).and("name").is(name.value()).and("betType").is(betType.value())), CallbackRule.class);
			}
		}catch(Exception e)
		{
			throw new SocialBettingInternalException("get rule failed", e);
		}
		if(rule == null)
		{
			throw new SocialBettingValidationException(ErrCode.RULE_NOT_DEFINED, "get rule failed, rule is not defined in the db.");
		}
		return rule;
	}
	
	
	
	@Override
	public String saveBet(Bet bet) {
		String betId = bet.getId();
		if(StringUtils.isBlank(betId))
		{
			betId = UUID.randomUUID().toString();
			bet.setId(betId);
		}
		try
		{
			mongoTemplate.insert(bet);
		}catch(Exception e)
		{
			throw new SocialBettingInternalException("failed to place a new bet.", e);
		}
		return bet.getId();
	}

	@Override
	public Double getOddsByProjectTier(Long projectId, Integer tier) {
		List<BettingLevel> levels = null;
		try
		{
			levels = mongoTemplate.find(new Query(Criteria.where("level").is(tier).and("projectId").is(projectId)), BettingLevel.class);
		}catch(Exception e)
		{
			throw new SocialBettingInternalException("failed to lookup odds by user tier:", e);
		}
		if(levels == null || levels.isEmpty())
		{
			throw new SocialBettingInternalException(new StringBuilder("No Odds is defined for tier:").append(tier).append(" project:").append(projectId).toString());
		}
		BettingLevel level = levels.get(0);
		Double odds = level.getOdds();
		if(odds == null)
		{
			throw new SocialBettingInternalException(new StringBuilder("the odds is blank for tier:").append(tier).append(" project:").append(projectId).toString());
		}
		// TODO Auto-generated method stub
		return odds;
	}


    
	@Override
	public String getKey(Long projectId) {
		 
		String key = null;
		try
		{
			ProjectKeys projKey = mongoTemplate.findOne(new Query(Criteria.where("projectId").is(projectId)), ProjectKeys.class);
			key = projKey.getKey();
		}catch(Exception e)
		{
			throw new SocialBettingInternalException("failed to get the key.", e);
		}
		return key;
	}

	
	
	@Override
	public List<BettingEvent> getExpiredBets() 
	{
		List<BettingEvent> result = new ArrayList<BettingEvent>();
		try
		{
			result = mongoTemplate.find(new Query(Criteria.where("status").is(StatusEnum.EXPIRED.value())).skip(LIMIT).limit(LIMIT), BettingEvent.class);
		}catch(Exception e)
		{
			log.error("failed to get the expired bets:", e);
		}
		return result;
	}

	

	@Override
	public boolean release(Long jobId, String jobName) {
		DBLock lock = null;
		boolean result = false;
		
		try
		{
			lock = this.getLock(jobId, jobName);
		}catch(Exception e)
		{
			log.info("failed to get the lock:"+ e);
			return false;
		}
		if(lock == null)
		{
			result = true;
		}else
		{
			try
			{
				if(lock.getLocked())
				{
					lock.setLocked(false);
					this.mongoTemplate.save(lock);
				}			
				result = true;
			}catch(Exception e)
			{
				log.error("failed to release the lock."+ e);
			}
		}
		return result;
	}

	@Override
	public boolean release(String jobName) {
		//DBLock lock = null;
		boolean result = true;
		List<DBLock> locks = new ArrayList<DBLock>();
		try
		{
			locks = this.getLock(jobName);
		}catch(Exception e)
		{
			log.info("failed to get the lock:"+ e);
			return false;
		}
		
		if(!locks.isEmpty())
		{
			for(DBLock lock : locks)
			{
				try
				{
					if(lock.getLocked())
					{
						lock.setLocked(false);
						this.mongoTemplate.save(lock);
					}			
					
				}catch(Exception e)
				{
					log.error("failed to release the lock."+e);
					result = false;
				}
			}
			
		}
		return result;
	}

	@Override
	public boolean lock(Long jobId, String jobName, int expiredSeconds) {
		DBLock lock = null;
		boolean result = false;
		try
		{
			lock = this.getLock(jobId, jobName);
		}catch(Exception e)
		{
			log.info("failed to get the lock:"+e);
			return false;
		}
		if(lock == null)
		{
			try
			{
				lock = new DBLock();
				lock.setLockDate(new Date());
				lock.setJobId(jobId);
				lock.setJobName(jobName);
				lock.setLocked(true);		
				this.mongoTemplate.save(lock);
				result = true;
			}catch(Exception e)
			{
				log.error("failed to insert a new lock for job:"+jobName+e);
			}
		}else
		{
			boolean wasLock = lock.getLocked();
			if(!wasLock)
			{
				try
				{
					lock.setLockDate(new Date());
					lock.setLocked(true);
					this.mongoTemplate.save(lock);
					result = true;
				}catch(Exception e)
				{
					if(log.isDebugEnabled()) log.debug("failed to hold the lock for job:"+jobName+e);
				}
			}else
			{
				boolean override = false;
				if(expiredSeconds > 0)//came from cronjob with expire seconds try handle lock table errors
				{
					Date lastLockDate = lock.getLockDate();
					Date expiredDate = DateUtils.addSeconds(lastLockDate, expiredSeconds*LOCK_FACTOR);
					override = new Date().after(expiredDate);
				}
				if(override)
				{
					lock.setLockDate(new Date());
					this.mongoTemplate.save(lock);
					result = true;
				}
			}
		}
		return result;
	}
	
	private DBLock getLock(Long jobId, String jobName)
	{
		List<DBLock> result = new ArrayList<DBLock>();
		DBLock returnValue = null;
		result = mongoTemplate.find(new Query(Criteria.where("jobId").is(jobId).and("jobName").is(jobName)).limit(LIMIT), DBLock.class);
		if(result.size() > 1)
		{
			throw new SocialBettingInternalException("inconsistent db status!!!!!!!!!!!, only one lock for each jobid and jobname is allowed.");
		}else if(result.size() == 1)
		{
			returnValue = result.get(0);
		}
		return returnValue;
	}
	private List<DBLock> getLock(String jobName)
	{
		List<DBLock> result = new ArrayList<DBLock>();
		
		result = mongoTemplate.find(new Query(Criteria.where("jobName").is(jobName)), DBLock.class);
		
		return result;
	}
	@Override
	public List<BetNotification> getNewNotifications(Long projectId, String betType, Integer batchSize, Integer begin) 
	{
		if(batchSize == null || batchSize == 0)
		{
			batchSize = DefaultCallbackCommand.DEFAULT_BATCH_SIZE;
		}
		if(begin == null)
		{
			begin = 0;
		}
		List<BetNotification> result = new ArrayList<BetNotification>();
		try
		{
			result = mongoTemplate.find(new Query(Criteria.where("status").is(NotificationStatusEnum.NEW).and("projectId").is(projectId).and("betType").is(betType)).skip(begin).limit(batchSize), BetNotification.class);
		}catch(Exception e)
		{
			log.error("failed to get the new notifications:", e);
		}
		return result;
	}

	@Override
	public List<CallbackRule> getAllCallbackRules(Integer begin, Integer limit) {
		List<CallbackRule> result = new ArrayList<CallbackRule>();
		int trueLimit = (limit == null)? -1 : limit;
		int trueBegin = (begin == null)? 0 : begin;
		try
		{   
			if(trueLimit > 0)
			{
				result = mongoTemplate.find(new Query(Criteria.where("name").is(RuleNamesEnum.CALLBACK.value())).skip(trueBegin).limit(trueLimit), CallbackRule.class);
			}else
			{
				result = mongoTemplate.find(new Query(Criteria.where("name").is(RuleNamesEnum.CALLBACK.value())), CallbackRule.class);
			}
		}catch(Exception e)
		{
			log.error("failed to get the callback rules:", e);
		}
		return result;
	}

	@Override
	public SocialBettingTransaction getTransacitonById(String txId) {
		SocialBettingTransaction tx = null;
		try
		{
			tx = mongoTemplate.findById(txId, SocialBettingTransaction.class);
		}catch(Exception e)
		{
			log.error("failed to get the transaction by id:", e);
		}
		return tx;
	}

	@Override
	public BettingEvent findEventByTransactionId(Long projectId, String betType, String txId) {
		BettingEvent event = null;
		try
		{
			event = mongoTemplate.findOne(new Query(Criteria.where("projectId").is(projectId).and("betType").is(betType).and("transactionIds").is(txId)), BettingEvent.class);
		}catch(Exception e)
		{
			log.error("failed to get event by transactionId.", e);
		}
		return event;
	}

	@Override
	public void delete(Object notif) {
		try
		{
			mongoTemplate.remove(notif);
		}catch(Exception e)
		{
			log.error("failed to delete the .", e);
		}
	}

	@Override
	public Long lookupPlatformIdByProjectId(Long projectId) {
		ProjectPlatformLookUp lookup = null;
		try
		{
			lookup = mongoTemplate.findOne(new Query(Criteria.where("projectId").is(projectId)), ProjectPlatformLookUp.class);
		}catch(Exception e)
		{
			log.error("failed to get the platform id for project:"+projectId, e);
		}
		Long platformId = (lookup == null)? null : lookup.getPlatformId();
		return platformId;
	}
    
	@Override
	public void updateBucket(CallbackRule rule, Integer totalBucks) 
	{
		try
		{
			Long projectId = rule.getProjectId();
			if(projectId != null && totalBucks != null && totalBucks != 0)
			{
				String pId = projectId.toString();
				int bucketNumber =  (Integer.valueOf(pId).intValue() % totalBucks);
				rule.setBuckNumber(bucketNumber);
				mongoTemplate.save(rule);
			}
		}catch(Exception e)
		{
			log.error("update callback rule failed:", e);
		}
		
	}

	@Override
	public List<CallbackRule> getCallbackRulesByShardNum(Integer shardNum) {
		List<CallbackRule> result = new ArrayList<CallbackRule>();
		
		try
		{
			result = mongoTemplate.find(new Query(Criteria.where("buckNumber").is(shardNum)), CallbackRule.class);
			if(log.isTraceEnabled()) log.trace("find rule for buck "+shardNum+" result is:"+result.size());
		}catch(Exception e)
		{
			log.error("failed to get the callback rules:", e);
		}
		return result;
	}
}
