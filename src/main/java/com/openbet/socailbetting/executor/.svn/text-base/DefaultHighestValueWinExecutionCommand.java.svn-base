package com.openbet.socailbetting.executor;

import java.util.List;

import org.apache.log4j.Logger;

import com.openbet.socailbetting.exception.RuleEngineException;
import com.openbet.socailbetting.exception.SocialBettingInternalException;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.model.BettingTarget;
import com.openbet.socailbetting.utils.ErrCode;
import com.openbet.socailbetting.utils.TimerStatusEnum;

public class DefaultHighestValueWinExecutionCommand extends BaseRuleExecutor{
    private static final Logger log = Logger.getLogger(DefaultHighestValueWinExecutionCommand.class);
	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		List<BettingTarget> targets = event.getTargets();
		if(targets == null || targets.isEmpty())
		{
			throw new SocialBettingInternalException("internal error: no betting targets exist for evaluation the result.");
		}
		
		Double maxValue = null;
		Integer maxTargetIdx = null;
		boolean isTie = false;
		for(int i = 0; i < targets.size(); i++)
		{
			BettingTarget target = targets.get(i);
			String answer = target.getGameResult();
			Double value = null;
			try
			{
				value = Double.valueOf(answer);
			}catch(Exception e)
			{
				throw new RuleEngineException(ErrCode.GAME_RESULT_IS_NOT_NUMBER, "the game result should be a number.");
			}
			if(value != null  && !value.equals(Double.valueOf(TimerStatusEnum.COMPLETION_TIMER_EXPIRED.value())))
			{
				if(maxValue == null)
				{
					maxValue = value;
					maxTargetIdx = i;
				}else
				{
					if(value > maxValue)
					{
						maxValue = value;
						maxTargetIdx = i;
						isTie = false;
					}else if(value.equals(maxValue))
					{
						isTie = true;
					}else
					{
						isTie = false;
					}
				}
			}
		}
		if(isTie)
		{
			event.setTie(isTie);
		}
		if(maxTargetIdx != null && !isTie) 
		{
			BettingTarget target = targets.get(maxTargetIdx);
			target.setIsWinner(true);
		}else
		{
			log.info("all opponents exceeded the allowed completion time. the event id is "+event.getId());
		}
		return event;
	}
}
