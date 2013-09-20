package com.openbet.socailbetting.dao;

import java.util.Date;
import java.util.List;

import com.openbet.socailbetting.model.Bet;
import com.openbet.socailbetting.model.BetNotification;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.model.SocialBettingTransaction;
import com.openbet.socailbetting.model.rule.BaseRule;
import com.openbet.socailbetting.model.rule.CallbackRule;
import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.RuleNamesEnum;
import com.openbet.socailbetting.utils.StatusEnum;


public interface BaseRuleDao {
	public BaseRule getRule(RuleNamesEnum name, Long projectId,
			BetTypeEnum betType);

	

	public String saveBet(Bet bet);

	public Double getOddsByProjectTier(Long projectId, Integer tier);

	

	
	public String getKey(Long projectId);
	
	
	public List<BettingEvent> getExpiredBets();// deprecated
	
    public boolean release(Long jobId, String jobName);
    
    public boolean release(String jobName);
	
	public boolean lock(Long jobId, String jobName, int expiredInSeconds);
	
	public List<BetNotification> getNewNotifications(Long projectId, String betType, Integer batchSize, Integer begin);
	
	public List<CallbackRule> getAllCallbackRules(Integer begin, Integer limit);
	
	public SocialBettingTransaction getTransacitonById(String txId);// deprecated
	
	public BettingEvent  findEventByTransactionId(Long projectId, String betType, String txId);//// deprecated
	
	public void delete(Object notif);
	
	public Long lookupPlatformIdByProjectId(Long projectId);
	
	public void updateBucket(CallbackRule rule, Integer totalBucks);// deprecated
	
	public List<CallbackRule> getCallbackRulesByShardNum(Integer shardNum);// deprecated
}
