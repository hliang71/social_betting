package com.openbet.socailbetting.executor;

import java.util.Date;

import org.apache.log4j.Logger;

import com.openbet.socailbetting.exception.RuleEngineException;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.utils.ErrCode;
import com.openbet.socailbetting.utils.StatusEnum;
import com.openbet.socailbetting.utils.TimerStatusEnum;

public class DefaultValidateOpponentCommand  extends BaseRuleExecutor{
	private static final Logger log = Logger.getLogger(DefaultValidateOpponentCommand.class);
	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		log.info("in defaultValidateOpponentCommand");
		Integer requiredNumOfOpponents =(Integer) this.getValue();
		if(requiredNumOfOpponents == null || requiredNumOfOpponents == 0)
		{
			throw new RuleEngineException(ErrCode.REQUIRED_NUM_OF_OPPONENT_IS_UNDEFINED, "failed to validate num of opponent, number is undefined or is zero.");
		}
		Integer size = event.getOpponents() == null? 0 : event.getOpponents().size();
		if(size > requiredNumOfOpponents)
		{
			throw new RuleEngineException(ErrCode.EXCEED_MAX_NUM_OF_OPPONENTS_REQUIREMENT, "failed to add the opponent to this event, max allowed opponent number is 2.");
		}
		if(log.isDebugEnabled()) log.debug("the size is "+size);
		if(log.isDebugEnabled()) log.debug("requiredNumberofopponents is "+requiredNumOfOpponents);
		if(log.isDebugEnabled()) log.debug("event status is "+event.getStatus().value());
		if(size.equals(requiredNumOfOpponents))
		{
			if(log.isDebugEnabled()) log.debug("size is required num.");
			if(event.getStatus() != StatusEnum.LIVE)
			{
				event.setStatus(StatusEnum.LIVE);
			}
			event.setMatchedDate(new Date());
			event.setTimerStatus(TimerStatusEnum.OPPONENT_TIMER_DISARMED);
		}
		return event;
	}
}
