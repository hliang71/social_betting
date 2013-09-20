package com.openbet.socailbetting.executor;

import java.util.Map;

import com.openbet.socailbetting.exception.RuleEngineException;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.utils.ErrCode;

public class DefaultValidateMinBetCommand  extends BaseRuleExecutor{
	private static final String AMOUNT = "amount";

	@SuppressWarnings("unchecked")
	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		Double minAmt = (Double)this.getValue();
		Map<String, Object> param = (Map<String, Object>) parameters;
		Double amount = (Double)param.get(AMOUNT);
		if(minAmt == null)
		{
			throw new RuleEngineException(ErrCode.UNDEFINED_MIN_BET_AMOUNT, "failed to validate the min bet amount, min bet amount does not defined in odds rule");
		}
		if(amount == null || amount == 0)
		{
			throw new RuleEngineException(ErrCode.MISSING_BET_AMOUNT, "failed to validate the bet, missing bet amount");
		}
		if(amount < minAmt)
		{
			throw new RuleEngineException(ErrCode.MIN_BET_VIOLATION, "failed to validate the bet, min bet is "+ minAmt);
		}
		return event;
	}

}
