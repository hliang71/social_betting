package com.openbet.socailbetting.executor;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.model.BettingTarget;
import com.openbet.socailbetting.utils.StatusEnum;

public class DefaultValidateAllOpponentsCompletedCommand extends BaseRuleExecutor{

	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		
		List<BettingTarget> targets =event.getTargets();
		boolean allCompleted = true;
		if(targets.isEmpty())
		{
			allCompleted = false;
		}
		for(BettingTarget target : targets)
		{
			String answer = target.getGameResult();
			if(StringUtils.isBlank(answer))
			{
				allCompleted = false;
			}
		}
		if(allCompleted)
		{
			event.setReadyForSettlement(true);
			event.setSettledDate(new Date());
			event.setStatus(StatusEnum.SETTLED);
		}
		return event;
	}
}
