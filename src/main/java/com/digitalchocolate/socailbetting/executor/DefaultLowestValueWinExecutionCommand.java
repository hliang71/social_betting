package com.digitalchocolate.socailbetting.executor;

import java.util.List;

import org.apache.log4j.Logger;

import com.digitalchocolate.socailbetting.exception.RuleEngineException;
import com.digitalchocolate.socailbetting.exception.SocialBettingInternalException;
import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.model.BettingTarget;
import com.digitalchocolate.socailbetting.utils.ErrCode;
import com.digitalchocolate.socailbetting.utils.TimerStatusEnum;

public class DefaultLowestValueWinExecutionCommand extends BaseRuleExecutor{
    private static final Logger log = Logger.getLogger(DefaultLowestValueWinExecutionCommand.class);
	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		List<BettingTarget> targets = event.getTargets();
		if(targets == null || targets.isEmpty())
		{
			throw new SocialBettingInternalException("internal error: no betting targets exist for evaluation the result.");
		}
		
		Double minValue = null;
		Integer minTargetIdx = null;
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
				throw new RuleEngineException(ErrCode.GAME_RESULT_IS_NOT_NUMBER, "the game result must be a number.");
			}
			if(value != null && !value.equals(Double.valueOf(TimerStatusEnum.COMPLETION_TIMER_EXPIRED.value())))
			{
				if(minValue == null)
				{
					minValue = value;
					minTargetIdx = i;
				}else
				{
					if(value < minValue)
					{
						minValue = value;
						minTargetIdx = i;
						isTie = false;
					}else if(value.equals(minValue))
					{
						isTie = true;
					}else
					{
						isTie = false;
					}
				}
			}
		}
		event.setTie(isTie);
		if(minTargetIdx != null && !isTie)
		{
			BettingTarget target = targets.get(minTargetIdx);
			if(log.isDebugEnabled()) log.debug("set winner to "+ target.getTargetId());
			target.setIsWinner(true);
		}else
		{
			log.info("all opponents exceeded the allowed completion time. the event id is "+event.getId());
		}
		return event;
	}

}
