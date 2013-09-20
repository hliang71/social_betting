package com.openbet.socailbetting.executor.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.openbet.socailbetting.dao.EventDao;
import com.openbet.socailbetting.dao.impl.SocialBettingRuleDao;
import com.openbet.socailbetting.engine.RuleEngine;
import com.openbet.socailbetting.engine.SocialBettingDataConverter;
import com.openbet.socailbetting.model.BetNotification;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.model.BettingOpponent;
import com.openbet.socailbetting.model.BettingTarget;
import com.openbet.socailbetting.model.TransactionParticipantModel;
import com.openbet.socailbetting.service.BettingMessageProducer;
import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.EngineSupportedMethodEnum;
import com.openbet.socailbetting.utils.ExpirationListener;
import com.openbet.socailbetting.utils.SocialBettingCallTypeEnum;
import com.openbet.socailbetting.utils.StatusEnum;
import com.openbet.socailbetting.utils.TimerStatusEnum;
import com.openbet.socailbetting.webapp.EventTargetVO;

public class CompletionExpireLisener  implements ExpirationListener<EventTargetVO>{
	private static final Logger log = Logger.getLogger(CompletionExpireLisener.class);
	@Autowired
    @Qualifier("ruleDao")
	private SocialBettingRuleDao ruleDao;
	
	@Autowired
    @Qualifier("eventDao")
    private EventDao eventDao;
	
	@Autowired
    @Qualifier("ruleEngine")
	private RuleEngine ruleEngine;

	@Autowired
    @Qualifier("bettingMessageProducer")
	private BettingMessageProducer bettingMessageProducer;


    public SocialBettingRuleDao getRuleDao() {
		return ruleDao;
	}



	public void setRuleDao(SocialBettingRuleDao ruleDao) {
		this.ruleDao = ruleDao;
	}



	public RuleEngine getRuleEngine() {
		return ruleEngine;
	}



	public void setRuleEngine(RuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}



	public BettingMessageProducer getBettingMessageProducer() {
		return bettingMessageProducer;
	}



	public void setBettingMessageProducer(
			BettingMessageProducer bettingMessageProducer) {
		this.bettingMessageProducer = bettingMessageProducer;
	}

	public EventDao getEventDao() {
		return eventDao;
	}

	public void setEventDao(EventDao eventDao) {
		this.eventDao = eventDao;
	}



	/**
     * update game result with expired value and determine the winner.
     */
	@Override
	public void expired(EventTargetVO expiredEventTargetId) {
		log.info("completion expired.");
		String expiredEventId = expiredEventTargetId.getEventId();
		
			
		String targetId = expiredEventTargetId.getTargetId();
		Long projectId = expiredEventTargetId.getProjectId();
		BetTypeEnum betType = expiredEventTargetId.getBetType();
		BettingEvent event = eventDao.getEventByIds(projectId, betType.value(), expiredEventId);
		boolean settled = false;
		
		if(event != null )
		{
			if(event.getStatus() == StatusEnum.SETTLED)
			{
				log.info("the event is already settled.");
				return;
			}
			List<BettingTarget> targets = event.getTargets();
			BettingTarget expiredTarget = null;
			BettingTarget otherTarget = null;
			
			for(BettingTarget target : targets)
			{
				String tId = target.getTargetId();
				if(targetId.trim().equals(tId))
				{
					String gResult = target.getGameResult();
					if(StringUtils.isBlank(gResult))
					{
						target.setGameResult(TimerStatusEnum.COMPLETION_TIMER_EXPIRED.value()); // set to -3.1415926
					}
					expiredTarget = target;
				}else
				{
					otherTarget = target;
				}
			}
			if(expiredTarget == null)
			{
				log.error("failed to expire the event, the expired target id is not found in the event. event id is "+expiredEventId+" target id is "+targetId);
				return;
			}
			
			event = this.ruleEngine.exeRule(event, EngineSupportedMethodEnum.COMPLETION_EXPIRED, event.getProjectId(), event.getBetType(), null);
			List<TransactionParticipantModel> notifs = new ArrayList<TransactionParticipantModel>();
			BettingOpponent expiredOpponent = null;
			BettingOpponent otherOpponent = null;
			List<BettingOpponent> opponents = event.getOpponents();
			for(BettingOpponent opp : opponents)
			{
				if(otherTarget != null && opp.getTargetId().equals(otherTarget.getTargetId()))
				{
					otherOpponent = opp;
				}else
				{
					expiredOpponent = opp;
				}
			}
			if(event.getStatus() == StatusEnum.SETTLED)
            {			
				BetNotification expiredBN= SocialBettingDataConverter.convertToNotification(expiredOpponent.getOpponentId(), event, expiredOpponent.getPlatformUserId(), event.getCurrency(), SocialBettingCallTypeEnum.SETTLED);
				BetNotification otherBN= SocialBettingDataConverter.convertToNotification(otherOpponent.getOpponentId(), event, otherOpponent.getPlatformUserId(), event.getCurrency(), SocialBettingCallTypeEnum.SETTLED);
				notifs.add(expiredBN);
				notifs.add(otherBN);
				
            }else
            {
            	if(expiredTarget.getGameResult()!= null && expiredTarget.getGameResult().equals(TimerStatusEnum.COMPLETION_TIMER_EXPIRED.value()))
            	{
    				BetNotification expiredBN= SocialBettingDataConverter.convertToNotification(expiredOpponent.getOpponentId(), event, expiredOpponent.getPlatformUserId(), event.getCurrency(), SocialBettingCallTypeEnum.TIMEOUT);
    				notifs.add(expiredBN);
            	}
            }
			log.info("before update completion event.........");
			//ruleDao.atomicSave(notifs, event, false);
			this.bettingMessageProducer.atomicSave(notifs, event, false);
		}
		
	}
}
