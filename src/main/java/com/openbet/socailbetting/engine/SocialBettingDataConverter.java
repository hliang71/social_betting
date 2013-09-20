package com.openbet.socailbetting.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import sun.util.logging.resources.logging;

import com.openbet.socailbetting.exception.SocialBettingInternalException;
import com.openbet.socailbetting.model.BetNotification;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.model.BettingOpponent;
import com.openbet.socailbetting.model.BettingTarget;
import com.openbet.socailbetting.utils.BetHistoryStatusEnum;
import com.openbet.socailbetting.utils.SocialBettingCallTypeEnum;
import com.openbet.socailbetting.utils.StatusEnum;
import com.openbet.socailbetting.utils.TimerStatusEnum;
import com.openbet.socailbetting.webapp.BetHistoryDTO;
import com.openbet.socailbetting.webapp.BetOpponentDTO;
import com.openbet.socailbetting.webapp.BetSettlementDTO;
import com.openbet.socailbetting.webapp.OpponentTierDTO;

public class SocialBettingDataConverter {
	private static final Logger log = Logger.getLogger(BetOpponentDTO.class);
	public static BetOpponentDTO convertToSearchResultDTO(BettingEvent event)
	{
		
		List<BettingTarget> targets = event.getTargets();
		if(targets == null || targets.isEmpty())
		{
			throw new SocialBettingInternalException("Betting target is undefined.");
		}
		BettingTarget target = targets.get(0);// note: mongo db does not perserve the order but this is from rule engine and has not been persisted yet. so order hold true
		BettingTarget IBetOn = targets.get(targets.size() -1);
		List<BettingOpponent> opponents = event.getOpponents();
		if(opponents == null || opponents.isEmpty())
		{
			throw new SocialBettingInternalException("Betting opponent list is empty.");
		}
		
		BettingOpponent opponent = opponents.get(0);
		Double betAmt = opponent.getBetAmt();
		Double trueOdds = target.getTrueOdds();
		if(trueOdds < 1)
		{
			throw new SocialBettingInternalException("offered odds is less than 1, takeout percentage too high?");
		}
		Double amtToMatch = betAmt * (trueOdds -1);
		Double oddsOfferedToMe = IBetOn.getOfferedOdds();
		
		if(oddsOfferedToMe == null || oddsOfferedToMe == 0.0)
		{
			throw new SocialBettingInternalException("Rule engine incomplete calculation on odds.");

		}
		if(oddsOfferedToMe < 1)
		{
			throw new SocialBettingInternalException("calculated offered odds is less than 1. takeout percentage too high?");
		}
		Double payOut = amtToMatch * (oddsOfferedToMe -1);
		BetOpponentDTO opDTO = new BetOpponentDTO();
		opDTO.setBetAmt(amtToMatch);
		opDTO.setId(opponent.getOpponentId());
		opDTO.setIsWinner(false);
		opDTO.setCurrency(event.getCurrency());
		opDTO.setContextIdentifier(event.getContextIdentifier());
		opDTO.setOdds(oddsOfferedToMe);
		opDTO.setTier(event.getLevel());
		opDTO.setBetId(event.getId());
        opDTO.setPayOut(payOut);
       
		return opDTO;
	}
	
	public static BetSettlementDTO converToSettlementDTO(BettingEvent event)
	{
		BetSettlementDTO settleDTO = new BetSettlementDTO();
		
		if(event.getStatus() != StatusEnum.SETTLED)
		{
			settleDTO.setIsSettled(false);
			return settleDTO;
		}
		settleDTO.setIsSettled(true);
		settleDTO.setTie(event.isTie());
		List<BetOpponentDTO> opps = settleDTO.getOpponents();
		if(opps == null)
		{
			opps = new ArrayList<BetOpponentDTO>();
			settleDTO.setOpponents(opps);
		}
		String eventId = event.getId();
		List<BettingTarget> targets = event.getTargets();
		List<BettingOpponent> opponents = event.getOpponents();
		if(targets.isEmpty())
		{
			throw new SocialBettingInternalException("failed to settle the event, target is not set. event id is "+eventId);

		}
		if(opponents.isEmpty())
		{
			throw new SocialBettingInternalException("failed to settle the event, opponents is not set. event id is "+eventId);
		}
		Map<String, BettingTarget> targetIdToTargets = new HashMap<String, BettingTarget>();
		for(BettingTarget target: targets)
		{
			String targetId = target.getTargetId();
			if(StringUtils.isBlank(targetId))
			{
				throw new SocialBettingInternalException("failed to settle the event, target id is not set. event id is "+eventId);

			}
			targetIdToTargets.put(targetId, target);
		}
		for(BettingOpponent opponent : opponents)
		{
			String targetId = opponent.getTargetId();
			String opponentId = opponent.getOpponentId();
			if(StringUtils.isBlank(targetId))
			{
				throw new SocialBettingInternalException("failed to settle the event, target id is not set in opponent. event id is "+eventId);

			}
			BettingTarget target = targetIdToTargets.get(targetId);
			BetOpponentDTO opponentDTO = new BetOpponentDTO();
			List<String> receiverIds = settleDTO.getPayoutReceiverIds();
			if(receiverIds == null)
			{
				receiverIds = new ArrayList<String>();
				settleDTO.setPayoutReceiverIds(receiverIds);
			}
			opponentDTO.setId(opponent.getOpponentId());
			Double betAmt = opponent.getBetAmt();
			if(betAmt == null || betAmt == 0.0)
			{
				throw new SocialBettingInternalException("failed to settle the bet, the bet amount is missing, eventId is"+eventId);
			}
			opponentDTO.setBetAmt(betAmt);
			String currency = event.getCurrency();
			if(StringUtils.isBlank(currency))
			{
				throw new SocialBettingInternalException("failed to settle the bet, the currency is not set in event:"+eventId);
			}
			opponentDTO.setCurrency(currency);
			Double offOdds = target.getOfferedOdds();
			if(offOdds == null || offOdds == 0.0)
			{
				throw new SocialBettingInternalException("failed to settle the bet, the offered odds is not set in event:"+eventId);
			}
			opponentDTO.setOdds(offOdds);

			Integer level = opponent.getPosition();
			if (level == null)
			{
				throw new SocialBettingInternalException("failed to settle the bet, the level is not set in event:"+eventId);
			}
			opponentDTO.setTier(level);
			opponentDTO.setBetId(eventId);
			Boolean isWinner = target.getIsWinner();
			opponentDTO.setIsWinner(isWinner);
			if(isWinner)
			{
				Double payoutAmt = betAmt * (offOdds -1);
				Double currentAmt = settleDTO.getAmtPayout();
				currentAmt += payoutAmt;
				settleDTO.setAmtPayout(currentAmt);
				receiverIds.add(opponentId);
				opponentDTO.setPayOut(payoutAmt);
			}
			opps.add(opponentDTO);
		}
		return settleDTO;
	}
    
	public static BetHistoryDTO convertTOBetHistory(BettingEvent event, String playerId)
	{
		BettingOpponent self = null;
		String selfTargetId = null;
		BettingTarget selfTarget = null;
		List<BettingOpponent> others = new ArrayList<BettingOpponent>();
		List<BettingOpponent> opps = event.getOpponents();
		List<BettingTarget> targets = event.getTargets();
		for(BettingOpponent opp : opps)
		{
			if(playerId.trim().equals(opp.getOpponentId()))
			{
				self = opp;
				selfTargetId = self.getTargetId();
			}else
			{
				others.add(opp);
			}	
		}
		if(StringUtils.isNotBlank(selfTargetId))
		{
			for(BettingTarget target : targets)
			{
				if(selfTargetId.trim().equals(target.getTargetId()))
				{
					selfTarget = target;
				}
			}
		}
		BetHistoryDTO result = new BetHistoryDTO();
		result.setContextIdentifier(event.getContextIdentifier());
		result.setBetId(event.getId());
		result.setTie(event.isTie());
		result.setProjectId(event.getProjectId());
		List<OpponentTierDTO> tiers = result.getOpponentUserTiers();
		if(tiers == null)
		{
			tiers = new ArrayList<OpponentTierDTO>();
			result.setOpponentUserTiers(tiers);
		}
		result.setBetType(event.getBetType());
		Double betAmt = (self == null)? null : self.getBetAmt() ;
		if(self != null)
		{
			result.setPlayerTier(self.getPosition());
			result.setBetAmt(betAmt);
			result.setPlayerId(self.getOpponentId());
		}
		for(BettingOpponent op: others)
		{
			OpponentTierDTO tier = new OpponentTierDTO();
			tier.setOpponentId(op.getOpponentId());
			tier.setTier(op.getPosition());
			tiers.add(tier);
		}
		result.setCurrency(event.getCurrency());
		Double takeoutPercentage = event.getTakeOutPercentage();
		result.setTakeOutPercentage(takeoutPercentage);
		result.setBetPlaceTime(event.getCreatedDate());
		result.setBetMatchedTime(event.getMatchedDate());
		result.setBetSettleTime(event.getSettledDate());
		if(selfTarget != null)
		{
			Double offeredOdds = selfTarget.getOfferedOdds();
			if(offeredOdds != null && betAmt != null && offeredOdds > 1)
			{
				Double payOut = betAmt * (offeredOdds -1);
				Double trueOdds =selfTarget.getTrueOdds();
				result.setPayOut(payOut);
				result.setTrueOdds(trueOdds);
			}		
			result.setOdds(offeredOdds);	
			result.setWinner(selfTarget.getIsWinner());
			String gameResult = selfTarget.getGameResult();
			String expireValue = TimerStatusEnum.COMPLETION_TIMER_EXPIRED.value();
			if(StringUtils.isNotBlank(gameResult) && !gameResult.trim().equals(expireValue))
			{
				result.setCompletionResult(gameResult);
			}
		}
		String statusValue = event.getStatus() == null? null : event.getStatus().value();
		String realValue = (statusValue == null) ? null : (("WAIT_OPPONENT").equals(statusValue) ? "CREATED" : (("LIVE").equals(statusValue) ? "MATCHED" : statusValue) );
		
		result.setStatus(realValue);
		return result;
	}
	
	public static BetNotification convertToNotification(String ownerId, BettingEvent event, String platformUserId, String currency, SocialBettingCallTypeEnum sbp_call_type )
	{
		if(StringUtils.isBlank(ownerId) || StringUtils.isBlank(platformUserId))
		{
			throw new SocialBettingInternalException("owner opponent id or platform id is missing.");
		}
		BetNotification noti = new BetNotification();
		noti.setP_uid(platformUserId);
		if(StringUtils.isBlank(currency))
		{
			throw new SocialBettingInternalException("currency is required for notification!");
		}
		noti.setType(currency);
		if(sbp_call_type == null)
		{
			throw new SocialBettingInternalException("call type is required!");
		}
		noti.setSbp_call_type(sbp_call_type);
		noti.setSbp_event_id(event.getId());
		noti.setProjectId(event.getProjectId());
		if(event.getBetType() == null)
		{
			throw new SocialBettingInternalException("bet type is required!");
		}
		noti.setBetType(event.getBetType());
		List<BettingTarget> targets = event.getTargets();
		List<BettingOpponent> opponents = event.getOpponents();
		if(targets == null || opponents == null)
		{
			throw new SocialBettingInternalException("invalid betting event, no opponents or targets.");
		}
		Map<String, BettingTarget> targetMap = new HashMap<String, BettingTarget>();
		for(BettingTarget target : targets)
		{
			String targetId = target.getTargetId();
			targetMap.put(targetId, target);
		}
		BettingOpponent owner = null;
		BettingTarget ownerTarget = null;
		BettingOpponent other = null;
		for(BettingOpponent op : opponents)
		{
			String oppId = op.getOpponentId();
			if(StringUtils.isBlank(oppId))
			{
				throw new SocialBettingInternalException("opponent id is missing");
			}
			if(oppId.equals(ownerId))
			{
				owner = op;
				String targetId = op.getTargetId();
				ownerTarget = targetMap.get(targetId);
			}else
			{
				other = op;
			}
		}
		if(owner == null || ownerTarget == null)
		{
			throw new SocialBettingInternalException("opponent id is not found in the betting event.");
		}
		Double amt = owner.getBetAmt();
		Double offeredPayout = ownerTarget.getOfferedPayout();
		if(amt == null || amt == 0.0)
		{
			throw new SocialBettingInternalException("invalid betting event, bet amount should not be 0");
		}
		
		
		if(sbp_call_type == SocialBettingCallTypeEnum.SETTLED)
		{
			Double winnerPayout = offeredPayout == null? amt : (amt+offeredPayout);
			if(ownerTarget.getIsWinner())
			{			
				noti.setAmount(winnerPayout);
				noti.setSbp_call_detail(BetNotification.CALL_DETAIL_WIN);
			}else if(event.isTie())
			{
				noti.setAmount(amt);
				noti.setSbp_call_detail(BetNotification.CALL_DETAIL_TIE);
			}else
			{
				noti.setAmount(0.0);
				noti.setSbp_call_detail(BetNotification.CALL_DETAIL_LOSE);
			}
		}else if(sbp_call_type == SocialBettingCallTypeEnum.PLACED)
		{
			noti.setAmount(amt*(-1));
			noti.setSbp_call_detail(BetNotification.CALL_DETAIL_PLACE);
		}else if(sbp_call_type == SocialBettingCallTypeEnum.MATCHED)
		{
			noti.setAmount(0.0);
			
			if(other == null || StringUtils.isBlank(other.getOpponentId()))
			{
				throw new SocialBettingInternalException("invalid betting event, matched bet should have 2 opponents!");
			}
			noti.setSbp_call_detail(other.getOpponentId());
		}else if(sbp_call_type == SocialBettingCallTypeEnum.CREATED)
		{
			noti.setAmount(amt*(-1));
			noti.setSbp_call_detail(BetNotification.CALL_DETAIL_CREATED);
		}else if(sbp_call_type == SocialBettingCallTypeEnum.TIMEOUT)
		{
			noti.setAmount(0.0);
			noti.setSbp_call_detail(BetNotification.CALL_DETAIL_TIMEOUT);
		}else
		{
			noti.setAmount(amt);
			noti.setSbp_call_detail(BetNotification.CALL_DETAIL_EXPIRED);
		}
		return noti;
	}
}
