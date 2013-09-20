package com.digitalchocolate.socailbetting.executor;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.digitalchocolate.socailbetting.exception.RuleEngineException;
import com.digitalchocolate.socailbetting.exception.SocialBettingInternalException;
import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.model.BettingTarget;
import com.digitalchocolate.socailbetting.utils.ErrCode;

public class DefaultCalculateOddsCommand  extends BaseRuleExecutor{
	private static final Logger log = Logger.getLogger(DefaultCalculateOddsCommand.class);
	private static final String AMOUNT = "amount";
	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		log.info("IN default calculate odds command");
		List<BettingTarget> targets = event.getTargets();
		//List<Double> seededOdds = new ArrayList<Double>();
		Map<String, Object> param = (Map<String, Object>) parameters;
		Double takeOutPercentage = (Double)this.getValue();
		Double totalSeededOdds = 0.0;
		if(takeOutPercentage == null)
		{
			throw new RuleEngineException(ErrCode.TAKEOUT_PERCENTAGE_UNDEFINED, "failed to calculate the odds, take out percentage is undefinedd." );                             
		}
		event.setTakeOutPercentage(takeOutPercentage);
		Double betAmtOther = null; // need it during search bet which only has one target has bet amount.
		Double seededOddsOther = null;
		for(BettingTarget target : targets) // total seeded odds.
		{
			Double sOdds = target.getSeededOdds();
			if(log.isDebugEnabled()) log.debug("seeded odds is:"+sOdds);
			if(log.isDebugEnabled()) log.debug("accumulated amount :"+target.getAccumulatedAmt());

			//seededOdds.add(sOdds);
			totalSeededOdds += sOdds;
			if(betAmtOther == null || betAmtOther == 0.0)
			{
				Double amount = target.getAccumulatedAmt();
				if(amount != null && amount != 0.0)
				{
					betAmtOther = amount;
					seededOddsOther = sOdds;
				}
			}
		}
		if(log.isDebugEnabled()) log.debug("betamt other = "+betAmtOther+" seededOddsOther:"+seededOddsOther);
		if(betAmtOther == null || betAmtOther == 0.0)
		{
			throw new SocialBettingInternalException( "at least one bet amt should exist");
		}
		if(seededOddsOther == null || seededOddsOther == 0.0)
		{
			throw new SocialBettingInternalException( "seeded odds is not defined");

		}
		if(log.isDebugEnabled()) log.debug("total seeded odds is:"+totalSeededOdds);
		for(BettingTarget target : targets) // set true odds
		{
			Double calculatedAmt = target.getAccumulatedAmt();
			
			if(calculatedAmt == null || calculatedAmt == 0.0)
			{				
				calculatedAmt = betAmtOther*(totalSeededOdds/seededOddsOther - 1);
				target.setAccumulatedAmt(calculatedAmt);
				param.put(AMOUNT, calculatedAmt);// to be able to pickup by max min rule.
				if(log.isDebugEnabled()) log.debug("calculate amt is :"+calculatedAmt);
			}
			
			Double sOdds = target.getSeededOdds();
			Double prob = sOdds/totalSeededOdds;
			Double trueOdds = 1/ prob;
			target.setTrueOdds(trueOdds);
			if(log.isDebugEnabled()) log.debug("trueOdds is:"+trueOdds);
		}
		Double firstAmt = null;
		Double firstTruePayout = null;
		for(BettingTarget target : targets) //calculate offered odds and validate matched amt.
		{
			Double trueOdds = target.getTrueOdds();
			Double amt = target.getAccumulatedAmt();
			Double truePayout = amt*(trueOdds - 1);
			target.setTruePayout(truePayout);
			Double offeredPayout = truePayout*(1- takeOutPercentage);
			target.setOfferedPayout(offeredPayout);
			Double offeredOdds = offeredPayout/amt + 1;
			target.setOfferedOdds(offeredOdds);
			if(firstAmt == null && firstTruePayout == null)
			{
				firstAmt = amt;
				firstTruePayout = truePayout;
			}else
			{
				if((Math.abs(firstTruePayout - amt) > 1) || (Math.abs(firstAmt - truePayout) > 1))
				{
					log.info("the placing bet amount does not matched. player 1 bet amount is "+firstAmt+" payout is "+firstTruePayout+". player 2 bet amt: "+amt+" payout :"+truePayout);
					throw new RuleEngineException(ErrCode.BET_AMT_NOT_MATCHED,"invalide bet amt, amt has to be matched with player 1.");
				}
			}
		}
		return event;
	}
}
