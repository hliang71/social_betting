package com.digitalchocolate.socailbetting.executor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.digitalchocolate.socailbetting.exception.RuleEngineException;
import com.digitalchocolate.socailbetting.executor.listener.OpponentSearchExpireLisener;
import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.utils.BetTypeEnum;
import com.digitalchocolate.socailbetting.utils.ConcurrentRedisMap;
import com.digitalchocolate.socailbetting.utils.ErrCode;
import com.digitalchocolate.socailbetting.utils.ExpiringMap;
import com.digitalchocolate.socailbetting.utils.TimerStatusEnum;
import com.digitalchocolate.socailbetting.webapp.OpponentExpireVO;

public class DefaultStartOpponentTimerCommand  extends BaseRuleExecutor{
	private static final String ID = "DefaultStartOpponentTimerCommand";
	private static final Logger log = Logger.getLogger(DefaultStartOpponentTimerCommand.class);
		
	private static final int CHECK_INTERVAL = 3;
		
	private OpponentSearchExpireLisener opponentListener;
	
	private Map<Integer, ExpiringMap<String, OpponentExpireVO>> timers = new ConcurrentHashMap<Integer, ExpiringMap<String, OpponentExpireVO>>();
	private ConcurrentMap<Integer, Integer> persistTimeKeys;
	
	public ExpiringMap<String, OpponentExpireVO> getTimer(Integer timeToLive) {
		return timers.get(timeToLive);
	}

    public void init()
    {
    	log.info("init opponent timer......");
    	persistTimeKeys = new ConcurrentRedisMap<Integer,Integer>(ID, this.getCtx(), null);
    	Set<Integer> timeKeySet = this.persistTimeKeys.keySet();
    	ApplicationContext ctx = this.getCtx();
    	for(Integer key : timeKeySet)
    	{
    		
			String identifier = ID+key;
			ExpiringMap<String, OpponentExpireVO> timer = new ExpiringMap<String, OpponentExpireVO>(key, CHECK_INTERVAL, identifier, ctx);
			timer.addExpirationListener(opponentListener);
			timer.getExpirer().startExpiringIfNotStarted();
			this.setTimer(key, timer);
    	}
    	this.persistTimeKeys.clear();
    }
  
	public void setTimer(Integer timeToLive, ExpiringMap<String, OpponentExpireVO> timer) {
		this.timers.put(timeToLive, timer);
	}



	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		log.info("IN start opponent timer");
		TimerStatusEnum status = event.getTimerStatus();
		if(status != TimerStatusEnum.OPPONENT_TIMER_STARTED)
		{
			Integer maxTimeFindOpponent = (Integer)this.getValue();
			
			if(maxTimeFindOpponent == null || maxTimeFindOpponent <= 0)
			{
				throw new RuleEngineException(ErrCode.MAX_TIME_FIND_OPPONENT_UNDEFINE, "max time to find opponent is undefined in opponent rule");
			}
			ExpiringMap<String, OpponentExpireVO> timer = this.getTimer(maxTimeFindOpponent);
			this.persistTimeKeys.putIfAbsent(maxTimeFindOpponent, maxTimeFindOpponent);

			if(timer == null)
			{
				ApplicationContext ctx = this.getCtx();
				String identifier = ID+maxTimeFindOpponent;
				timer = new ExpiringMap<String, OpponentExpireVO>(maxTimeFindOpponent, CHECK_INTERVAL, identifier, ctx);
				timer.addExpirationListener(opponentListener);
				timer.getExpirer().startExpiringIfNotStarted();
				this.setTimer(maxTimeFindOpponent, timer);
			}
			String id = event.getId();
			if(StringUtils.isBlank(id))
			{
				id = this.getEventId(event);
				event.setId(id);
			}
			Long projectId = event.getProjectId();
			BetTypeEnum betType = event.getBetType();
			OpponentExpireVO vo = new OpponentExpireVO();
			vo.setEventId(id);
			vo.setProjectId(projectId);
			vo.setBetType(betType);
			timer.put(id, vo);
			event.setTimerStatus(TimerStatusEnum.OPPONENT_TIMER_STARTED);
		}
		return event;
	}



	public OpponentSearchExpireLisener getOpponentListener() {
		return opponentListener;
	}



	public void setOpponentListener(OpponentSearchExpireLisener opponentListener) {
		this.opponentListener = opponentListener;
	}

	public ConcurrentMap<Integer, Integer> getPersistTimeKeys() {
		return persistTimeKeys;
	}

	public void setPersistTimeKeys(ConcurrentMap<Integer, Integer> persistTimeKeys) {
		this.persistTimeKeys = persistTimeKeys;
	}
	
}
