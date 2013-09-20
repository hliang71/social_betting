package com.openbet.socailbetting.executor;

import java.util.Map;

import com.openbet.socailbetting.exception.RuleEngineException;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.utils.ErrCode;

public class DefaultValidateMaxBetCommand extends BaseRuleExecutor{
	private static final String AMOUNT = "amount";

	@SuppressWarnings("unchecked")
	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		Double maxAmt = (Double)this.getValue();
		if(maxAmt == null || maxAmt == 0.0)
		{
			throw new RuleEngineException(ErrCode.UNDEFINED_MAX_BET_AMOUNT, "failed to validate the bet, max amount of bet is undefined or is zero.");
		}
		Map<String, Object> param = (Map<String, Object>) parameters;
		Double amount =(Double) param.get(AMOUNT);
		if(amount == null || amount == 0)
		{
			throw new RuleEngineException(ErrCode.MISSING_BET_AMOUNT, "failed to validate the bet, missing bet amount or bet amount is zero");
		}
		if(amount > maxAmt)
		{
			throw new RuleEngineException(ErrCode.MAX_BET_VIOLATION, "failed to validate the bet, max amount to bet is "+maxAmt);
		}
		return event;
	}

}
