package com.digitalchocolate.socailbetting.executor.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.digitalchocolate.socailbetting.dao.EventDao;
import com.digitalchocolate.socailbetting.dao.impl.SocialBettingRuleDao;
import com.digitalchocolate.socailbetting.engine.SocialBettingDataConverter;
import com.digitalchocolate.socailbetting.model.BetNotification;
import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.model.BettingOpponent;
import com.digitalchocolate.socailbetting.model.TransactionParticipantModel;
import com.digitalchocolate.socailbetting.service.BettingMessageProducer;
import com.digitalchocolate.socailbetting.utils.BetTypeEnum;
import com.digitalchocolate.socailbetting.utils.ExpirationListener;
import com.digitalchocolate.socailbetting.utils.SocialBettingCallTypeEnum;
import com.digitalchocolate.socailbetting.utils.StatusEnum;
import com.digitalchocolate.socailbetting.utils.TimerStatusEnum;
import com.digitalchocolate.socailbetting.webapp.OpponentExpireVO;


public class OpponentSearchExpireLisener implements ExpirationListener<OpponentExpireVO>{
	private static final Logger log = Logger.getLogger(OpponentSearchExpireLisener.class);
	
	@Autowired
    @Qualifier("ruleDao")
	private SocialBettingRuleDao ruleDao;
	
	@Autowired
    @Qualifier("eventDao")
    private EventDao eventDao;
	
	@Autowired
    @Qualifier("bettingMessageProducer")
	private BettingMessageProducer bettingMessageProducer;
	
	public SocialBettingRuleDao getRuleDao() {
		return ruleDao;
	}

	public void setRuleDao(SocialBettingRuleDao ruleDao) {
		this.ruleDao = ruleDao;
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

	@Override
	public void expired(OpponentExpireVO expiredEventId) {
		log.info("processing expired");
		String eventId = expiredEventId.getEventId();
		Long projectId = expiredEventId.getProjectId();
		BetTypeEnum betType = expiredEventId.getBetType();
		BettingEvent event = eventDao.getEventByIds(projectId, betType.value(), eventId);
		if(event != null)
		{
			StatusEnum status = event.getStatus();
			TimerStatusEnum timerStatus = event.getTimerStatus();
			if(status == StatusEnum.WAIT_OPPONENT && timerStatus == TimerStatusEnum.OPPONENT_TIMER_STARTED)
			{
				event.setStatus(StatusEnum.EXPIRED);
				event.setTimerStatus(TimerStatusEnum.OPPONENT_TIMER_EXPIRED);
				log.info("before update opponent event.........");
				List<TransactionParticipantModel> notifs = new ArrayList<TransactionParticipantModel>();			
				List<BettingOpponent> opponents = event.getOpponents();
				BettingOpponent expiredOpponent = opponents.get(0);
				BetNotification expiredBN= SocialBettingDataConverter.convertToNotification(expiredOpponent.getOpponentId(), event, expiredOpponent.getPlatformUserId(), event.getCurrency(), SocialBettingCallTypeEnum.EXPIRED);
				notifs.add(expiredBN);
				//ruleDao.atomicSave(notifs, event, false);
				this.bettingMessageProducer.atomicSave(notifs, event, false);
				
			}else
			{
				log.info(new StringBuilder("opponent timer expired but the event has matched status: status=").append(status.value()).append(" timer status=").append(timerStatus.value()).toString());            
			}
			
		}	
	}

}
