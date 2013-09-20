package com.openbet.socailbetting.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.openbet.socailbetting.dao.BaseRuleDao;
import com.openbet.socailbetting.exception.RuleEngineException;
import com.openbet.socailbetting.exception.SocialBettingInternalException;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.model.BettingOpponent;
import com.openbet.socailbetting.model.BettingTarget;
import com.openbet.socailbetting.utils.ErrCode;
import com.openbet.socailbetting.utils.StatusEnum;

public class DefaultAddOpponentTargetCommand extends BaseRuleExecutor{
    public static final String OPP_ID="opponentId";
    public static final String TIER_NUM="playerLevel";
    public static final String AMOUNT="amount";
    public static final String PROJECT_ID="projectId";
    public static final String PLATFORM_USER_ID = "platformUserId";
    private static final Logger log = Logger.getLogger(DefaultAddOpponentTargetCommand.class);
    private BaseRuleDao ruleDao;
    
	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) 
	{
		log.info("in default add opponent command");
		Boolean opponentParticipateInBet = (Boolean)this.getValue();
		if(!opponentParticipateInBet)
		{
			throw new RuleEngineException(ErrCode.RULE_NOT_SUPPORTED, "only OPPONENT_PARTICIPATE_IN_BET is supported by this game");
		}
		
		Map param = (Map)parameters;
		String opponentId = (String) param.get(OPP_ID);
		Integer tierNumber = (Integer) param.get(TIER_NUM);
		Double amt = (Double)param.get(AMOUNT);
		if(log.isDebugEnabled()) log.debug("bet amt is:"+amt);
		Long projectId = (Long)param.get(PROJECT_ID);
		String platformUserId = (String) param.get(PLATFORM_USER_ID);
		if(amt != null && amt == 0.0)
		{
			throw new RuleEngineException(ErrCode.MISSING_BET_AMOUNT, "failed to add opponent, missing bet amount");		
		}
		if(StringUtils.isBlank(opponentId))
		{
			throw new RuleEngineException(ErrCode.MISSING_OPPONENT_ID, "failed to add opponent, missing opponent id");
			
		}
		if(tierNumber == null)
		{
			throw new SocialBettingInternalException("failed to add opponent, missing tier number of the opponent");		
		}
		if(StringUtils.isBlank(platformUserId))
		{
			throw new RuleEngineException(ErrCode.SECURITY_PLATFORM_USER_ID_REQUIRED, "platform user id is required.");		

		}
		
		BettingOpponent opponent = new BettingOpponent();
		opponent.setOpponentId(opponentId);
		opponent.setPlatformUserId(platformUserId);
		opponent.setPosition(tierNumber);
		opponent.setBetAmt(amt);
		List<BettingOpponent> opps = event.getOpponents();
		if(opps == null)
		{
			opps = new ArrayList<BettingOpponent>();
			event.setOpponents(opps);
		}
		opps.add(opponent);
		if(opponentParticipateInBet)
		{
			opponent.setTargetId(opponentId);
			List<BettingTarget> targets = event.getTargets();
			if(targets == null)
			{
				targets = new ArrayList<BettingTarget>();
				event.setTargets(targets);
			}
			for(BettingTarget target : targets)
			{
				if(log.isDebugEnabled()) log.debug("target accumulated bet amt:"+target.getAccumulatedAmt());
			}
			BettingTarget target = new BettingTarget();
			target.setTargetId(opponentId);
			
			Double accumulatedAmt = target.getAccumulatedAmt() == null? 0.0 : target.getAccumulatedAmt();
			if(amt != null)
			{
				accumulatedAmt += amt;	
			}
			target.setAccumulatedAmt(accumulatedAmt);
			Double seededOdds = this.ruleDao.getOddsByProjectTier(projectId, tierNumber);
			if(log.isDebugEnabled()) log.debug("the seeded odds is:"+seededOdds);
			if(seededOdds == null)
			{
				throw new SocialBettingInternalException("Cannot resolve seeded odds for tier "+tierNumber);
			}
			target.setSeededOdds(seededOdds);
			targets.add(target);
		}	
		StatusEnum status = event.getStatus();
		if(status != StatusEnum.WAIT_OPPONENT)
		{
			event.setStatus(StatusEnum.WAIT_OPPONENT);
		}
		return event;
	}

	public BaseRuleDao getRuleDao() {
		return ruleDao;
	}

	public void setRuleDao(BaseRuleDao ruleDao) {
		this.ruleDao = ruleDao;
	}
	
	

}
