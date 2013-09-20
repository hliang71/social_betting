package com.digitalchocolate.socailbetting.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.digitalchocolate.socailbetting.dao.EventDao;
import com.digitalchocolate.socailbetting.dao.impl.SocialBettingRuleDao;
import com.digitalchocolate.socailbetting.engine.RuleEngine;
import com.digitalchocolate.socailbetting.engine.SocialBettingDataConverter;
import com.digitalchocolate.socailbetting.exception.SocialBettingInternalException;
import com.digitalchocolate.socailbetting.exception.SocialBettingSecurityException;
import com.digitalchocolate.socailbetting.exception.SocialBettingValidationException;
import com.digitalchocolate.socailbetting.model.BetNotification;
import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.model.BettingOpponent;
import com.digitalchocolate.socailbetting.model.BettingTarget;
import com.digitalchocolate.socailbetting.model.TransactionParticipantModel;
import com.digitalchocolate.socailbetting.model.rule.CallbackRule;
import com.digitalchocolate.socailbetting.model.rule.OddsRule;
import com.digitalchocolate.socailbetting.model.rule.OpponentRule;
import com.digitalchocolate.socailbetting.model.rule.SettlementRule;
import com.digitalchocolate.socailbetting.service.BettingMessageProducer;
import com.digitalchocolate.socailbetting.service.BettingService;
import com.digitalchocolate.socailbetting.utils.BetTypeEnum;
import com.digitalchocolate.socailbetting.utils.EngineSupportedMethodEnum;
import com.digitalchocolate.socailbetting.utils.ErrCode;
import com.digitalchocolate.socailbetting.utils.MD5Encryter;
import com.digitalchocolate.socailbetting.utils.RuleNamesEnum;
import com.digitalchocolate.socailbetting.utils.SocialBettingCallTypeEnum;
import com.digitalchocolate.socailbetting.utils.StatusEnum;
import com.digitalchocolate.socailbetting.utils.TimerStatusEnum;
import com.digitalchocolate.socailbetting.webapp.BaseRequestDTO;
import com.digitalchocolate.socailbetting.webapp.BetHistoryDTO;
import com.digitalchocolate.socailbetting.webapp.BetHistoryResult;
import com.digitalchocolate.socailbetting.webapp.BetOpponentDTO;
import com.digitalchocolate.socailbetting.webapp.BetSettlementDTO;
import com.digitalchocolate.socailbetting.webapp.BettingEventsDTO;
import com.digitalchocolate.socailbetting.webapp.BettingResultDTO;
import com.digitalchocolate.socailbetting.webapp.GenerateBetRequestBean;
import com.digitalchocolate.socailbetting.webapp.HistoryRequestBean;
import com.digitalchocolate.socailbetting.webapp.OddsUid;
import com.digitalchocolate.socailbetting.webapp.OpponentsOdds;
import com.digitalchocolate.socailbetting.webapp.OpponentsOddsRequest;
import com.digitalchocolate.socailbetting.webapp.OpponentsOddsResult;
import com.digitalchocolate.socailbetting.webapp.PlaceBetRequestBean;
import com.digitalchocolate.socailbetting.webapp.RuleDTO;
import com.digitalchocolate.socailbetting.webapp.RuleRequestBean;
import com.digitalchocolate.socailbetting.webapp.SearchRequestBean;
import com.digitalchocolate.socailbetting.webapp.TierOpponentDTO;
import com.digitalchocolate.socailbetting.webapp.TiersOddsRequest;
import com.digitalchocolate.socailbetting.webapp.TiersOddsResult;
import com.digitalchocolate.socailbetting.webapp.TiersResult;
import com.digitalchocolate.socailbetting.webapp.UpdateBetRequestBean;



@Service("bettingService")
public class BettingServiceImpl implements BettingService{
	private static final Logger log = Logger.getLogger(BettingServiceImpl.class);
	private static final Integer LIMIT = 10;
	private static final Integer MAX_ALLOW_CONTEXT_LENGTH=100;
	private static final Integer START_IDX=0;
	private static final Integer END_IDX=2;
	private static final String BY_PASS="-3.1415926";
	                                                                                                   
	@Autowired
    @Qualifier("ruleDao")
    private SocialBettingRuleDao dao;
	
	@Autowired
    @Qualifier("eventDao")
    private EventDao eventDao;
	
	@Autowired
    @Qualifier("crmClient")
	private RestCRMClient crmClient;
	
	@Autowired
    @Qualifier("ruleEngine")
	private RuleEngine ruleEngine;

	@Autowired
    @Qualifier("redisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
    @Qualifier("bettingMessageProducer")
	private BettingMessageProducer bettingMessageProducer;
	
	@Override
	public RuleDTO getRules(RuleRequestBean request) {
		log.info("IN GET RULES!!!!1");
		Long projectId = request.getProjectId();
		BetTypeEnum betType = request.getBetType();
		if(betType == null)
		{
			throw new SocialBettingValidationException(ErrCode.INVALID_BET_TYPE, "failed to get rules bet type is invalid or missing.");
		}
	    if(log.isDebugEnabled()) log.debug("bettype is "+ betType.value());
		OddsRule oddsRule = (OddsRule)dao.getRule(RuleNamesEnum.ODDS, projectId, betType);
		OpponentRule opRule = (OpponentRule)dao.getRule(RuleNamesEnum.OPPONENT, projectId, betType);
		SettlementRule settleRule = (SettlementRule)dao.getRule(RuleNamesEnum.SETTLEMENT, projectId, betType);
		CallbackRule callbackRule = (CallbackRule)dao.getRule(RuleNamesEnum.CALLBACK, projectId, betType);
		String userLevelUrl = callbackRule.getUserLevelUrl();
		RuleDTO result = new RuleDTO();
		try
		{
			BeanUtils.copyProperties(oddsRule, result);
			BeanUtils.copyProperties(opRule, result);
			BeanUtils.copyProperties(settleRule, result);
		}catch(Exception e)
		{
			log.error("failed to copy bean properties to rule response.", e);
			throw new SocialBettingInternalException("failed to copy bean properties to rule response.", e);
		}
		
		String platformId = request.getPlatformUserId();
		Integer tier = null;
		if(StringUtils.isBlank(this.crmClient.getUserLevelUrl()))
		{
			this.crmClient.setUserLevelUrl(userLevelUrl);
		}
		String opponentId = request.getOpponentId();
		tier=crmClient.getTier(platformId,opponentId, projectId, betType);
		if(tier != null)
		{
			result.setPlayerTier(tier);
			result.setEligibleForBet(true);
		}else
		{
			result.setEligibleForBet(false);
		}
		return result;
	}

	@Override
	public BettingEventsDTO searchBets(SearchRequestBean request) {
		log.info("IN SEARCH BET.");
		String platformUserid = request.getPlatformUserId();
		if(StringUtils.isBlank(platformUserid))
		{
			throw new SocialBettingSecurityException(ErrCode.SECURITY_PLATFORM_USER_ID_REQUIRED, "platform user id is required.");
		}
		Long projectId = request.getProjectId();
		if(projectId == null)
		{
			throw new SocialBettingSecurityException(ErrCode.SECURITY_PROJECT_ID_REQUIRED, "project id is required");
		}
		BetTypeEnum betType = request.getBetType();
		String opponentId = request.getOpponentId();
		Integer playerLevel = this.crmClient.getTier(platformUserid,opponentId, projectId, betType);
		request.setPlayerLevel(playerLevel);
		
		boolean friendEnable = request.getTargetMe();
		
		Integer begin = request.getBegin();
		String contextId = request.getContextIdentifier();
		if(StringUtils.isNotBlank(contextId))
		{
			if(contextId.length() > MAX_ALLOW_CONTEXT_LENGTH)
			{
				throw new SocialBettingValidationException(ErrCode.CONTEXT_TOO_LONG, "Max length for context is "+MAX_ALLOW_CONTEXT_LENGTH);
			}
		}
		if(begin == null)
		{
			begin = 0;
		}
		if(playerLevel == null)
		{
			throw new SocialBettingInternalException("invalid platform user id, this user is not in the targeted group.");
		}
		Integer limit = request.getLimit();
		if(limit == null || limit == 0)
		{
			limit = LIMIT;
		}
		List<BettingEvent> matchedEvents = this.eventDao.findBets(projectId,opponentId, playerLevel,betType, StatusEnum.WAIT_OPPONENT, friendEnable,contextId, limit, begin);
		if(log.isDebugEnabled()) log.debug("result:"+ matchedEvents);
		List<BetOpponentDTO> matchedBets = new ArrayList<BetOpponentDTO>();
		Map parameters = null;
		try
		{
			parameters = PropertyUtils.describe(request);
		}catch(Exception e)
		{
			if(log.isDebugEnabled()) log.debug("failed to get some of the fields.", e);
		}
		if(parameters == null)
		{
			throw new SocialBettingInternalException( "failed to parse the searchBet request.");

		}
		Map temp = null;
		for(BettingEvent event : matchedEvents)
		{
			try
			{
				temp = new HashMap(parameters);
				event = this.ruleEngine.exeRule(event, EngineSupportedMethodEnum.SEARCH_BET, projectId, betType, temp);
				BetOpponentDTO opDTO = SocialBettingDataConverter.convertToSearchResultDTO(event);
				matchedBets.add(opDTO);
			}catch(Exception e)
			{
				log.error("higher event did not pass rule engine. ignored", e);
			}
		}
		Map<Integer, List<BetOpponentDTO>> resultMap = new HashMap<Integer, List<BetOpponentDTO>>();
		
		for(BetOpponentDTO betOpponentDTO : matchedBets)
		{
			Integer level = betOpponentDTO.getTier();
			List<BetOpponentDTO> ops = resultMap.get(level);
			if(ops == null)
			{
				ops = new ArrayList<BetOpponentDTO>();
				resultMap.put(level, ops);
			}
			ops.add(betOpponentDTO);
		}
		Iterator<Integer> itr = resultMap.keySet().iterator();
		BettingEventsDTO result = new BettingEventsDTO();
		List<TierOpponentDTO> tierOpponents= result.getResults();
		while(itr.hasNext())
		{
			Integer nextTier = itr.next();
			List<BetOpponentDTO> betOpponents = resultMap.get(nextTier);
			TierOpponentDTO tierOpp = new TierOpponentDTO();
			tierOpp.setTier(nextTier);
			tierOpp.setCandidates(betOpponents);
			tierOpponents.add(tierOpp);
		}
		result.setBetType(betType);
		return result;
	}

	@Override
	public BettingResultDTO generateBets(GenerateBetRequestBean request) {
		BettingEvent event = new BettingEvent();
		Long projectId =request.getProjectId();
		String platformUserid = request.getPlatformUserId();
		String contextIdentifier = request.getContextIdentifier();
		String opponentId = request.getOpponentId();
		if(StringUtils.isNotBlank(contextIdentifier))
		{
			if(contextIdentifier.length() > MAX_ALLOW_CONTEXT_LENGTH)
			{
				throw new SocialBettingValidationException(ErrCode.CONTEXT_TOO_LONG, "Max length for context is "+MAX_ALLOW_CONTEXT_LENGTH);
			}
		}
		if(StringUtils.isBlank(platformUserid))
		{
			throw new SocialBettingSecurityException(ErrCode.SECURITY_PLATFORM_USER_ID_REQUIRED, "platform user id is required.");
		}
		if(projectId == null)
		{
			throw new SocialBettingSecurityException(ErrCode.SECURITY_PROJECT_ID_REQUIRED, "project id is required");
		}
		BetTypeEnum betType = request.getBetType();
		Integer playerLevel = this.crmClient.getTier(platformUserid,opponentId, projectId, betType);
		request.setPlayerLevel(playerLevel);
		
		
		String currency = request.getCurrency();
		if(projectId == null)
		{
			throw new SocialBettingValidationException(ErrCode.SECURITY_PROJECT_ID_REQUIRED, "project is required.");
		}
		if(StringUtils.isBlank(currency))
		{
			throw new SocialBettingValidationException(ErrCode.MISSING_CURRENCY, "Failed to generate bet. currency is required.");
		}
		if(betType == null)
		{
			throw new SocialBettingValidationException(ErrCode.INVALID_BET_TYPE, "invalid bet type, bet type is required.");
		}
		event.setBetType(betType);
		event.setCurrency(currency);
		event.setProjectId(projectId);
		if(StringUtils.isNotBlank(contextIdentifier))
		{
			event.setContextIdentifier(contextIdentifier);
		}
		Map parameters = null;
		try
		{
			parameters = PropertyUtils.describe(request);
		}catch(Exception e)
		{
			if(log.isDebugEnabled()) log.debug("failed to get some of the fields.", e);
		}
		if(parameters == null)
		{
			throw new SocialBettingInternalException( "failed to parse the generateBet request.");

		}
		event = this.ruleEngine.exeRule(event, EngineSupportedMethodEnum.GENERATE_BET, projectId, betType, parameters);
		// save bet first to similar to atomic ops.
		String eventId = event.getId();
		if(StringUtils.isBlank(eventId))
		{
			throw new SocialBettingInternalException("event id is blank which should be set by the rule engine.");
		}
		/**Bet bet = new Bet();
		String betId = UUID.randomUUID().toString();
		bet.setId(betId);
		bet.setOpponentId(request.getOpponentId());
		bet.setTargetId(request.getOpponentId());
		bet.setAmount(request.getAmount());
		bet.setCurrency(request.getCurrency());
		bet.setEventId(eventId);
		
	    dao.saveBet(bet);*/
	    BettingOpponent opponent = event.getOpponents().get(0);
	    //opponent.setBetId(betId);
	    BetNotification bn = SocialBettingDataConverter.convertToNotification(request.getOpponentId(), event, platformUserid, currency, SocialBettingCallTypeEnum.CREATED);
	    List<TransactionParticipantModel> notifs = new ArrayList<TransactionParticipantModel>();
		notifs.add(bn);
	    //dao.atomicSave(notifs, event, true);
		bettingMessageProducer.atomicSave(notifs, event, true);
		BettingResultDTO result = new BettingResultDTO();
		result.setBetId(eventId);
		result.setSuccess(true);
		return result;
	}

	@Override
	public BettingResultDTO placeBets(PlaceBetRequestBean request) {
		Long projectId = request.getProjectId();
		String platformUserid = request.getPlatformUserId();
		if(StringUtils.isBlank(platformUserid))
		{
			throw new SocialBettingSecurityException(ErrCode.SECURITY_PLATFORM_USER_ID_REQUIRED, "platform user id is required.");
		}
		if(projectId == null)
		{
			throw new SocialBettingSecurityException(ErrCode.SECURITY_PROJECT_ID_REQUIRED, "project id is required");
		}
		String eventId = request.getBetId();
		if(StringUtils.isBlank(eventId))
		{
			throw new SocialBettingValidationException(ErrCode.MISSING_EVENT_ID, "eventId is required for placing the bet.");
			
		}
		BettingEvent event = eventDao.getEventById(eventId);
		if(event == null)
		{
			throw new SocialBettingValidationException(ErrCode.EVENT_NOT_FOUND, "cannot place the bet, the betting event is not found!");
		}
		StatusEnum status = event.getStatus();
		if(status == StatusEnum.EXPIRED )
		{
			throw new SocialBettingValidationException(ErrCode.MAX_TIME_FIND_OPPONENT_EXCEED, "The event you bet is expired.");
		}

		BetTypeEnum betType = event.getBetType();
		String opponentId = request.getOpponentId();
		Integer playerLevel = this.crmClient.getTier(platformUserid, opponentId, projectId, betType);
		request.setPlayerLevel(playerLevel);
		Map parameters = null;
		try
		{
			parameters = PropertyUtils.describe(request);
		}catch(Exception e)
		{
			if(log.isDebugEnabled()) log.debug("failed to get some of the fields.", e);
		}
		if(parameters == null)
		{
			throw new SocialBettingInternalException( "failed to parse the placeBet request.");

		}
		
		
		event = this.ruleEngine.exeRule(event, EngineSupportedMethodEnum.PLACE_BET, projectId, betType, parameters);
		/**Bet bet = new Bet();
		String betId = UUID.randomUUID().toString();
		bet.setId(betId);
		bet.setOpponentId(request.getOpponentId());
		bet.setTargetId(request.getOpponentId());
		bet.setAmount(request.getAmount());
		bet.setCurrency(request.getCurrency());
		bet.setEventId(eventId);
		
	    dao.saveBet(bet);*/
	    //TODO need to refactor the below code into post processer logic. to make this scalable.
	    List<BettingOpponent> opps = event.getOpponents();
	    BettingOpponent other = null;
	    for(BettingOpponent opp : opps)
	    {
	    	if(!opp.getOpponentId().equals(request.getOpponentId()))
	    	{
	    		other = opp;
	    	}
	    }
	    if(other == null)
	    {
	    	throw new SocialBettingValidationException(ErrCode.BET_ON_YOURSELF_NOT_ALLOWED, "you cannot bet on yourself.");
	    }
	    BetNotification matchedBn = SocialBettingDataConverter.convertToNotification(other.getOpponentId(), event, other.getPlatformUserId(), request.getCurrency(), SocialBettingCallTypeEnum.MATCHED);

	    BetNotification placedBn = SocialBettingDataConverter.convertToNotification(request.getOpponentId(), event, platformUserid, request.getCurrency(), SocialBettingCallTypeEnum.PLACED);
	    List<TransactionParticipantModel> notifs = new ArrayList<TransactionParticipantModel>();
		notifs.add(matchedBn);
		notifs.add(placedBn);
	    //dao.atomicSave(notifs, event, false);
		this.bettingMessageProducer.atomicSave(notifs, event, false);
	    BettingResultDTO result = new BettingResultDTO();
		result.setBetId(event.getId());
		result.setSuccess(true);
		return result;
	}

	@Override
	public BetSettlementDTO updateBets(UpdateBetRequestBean request) {
		String gameResult = request.getGameResult();
		String eventId = request.getBetId();
		Long projectId = request.getProjectId();
		String opponentId = request.getOpponentId();
		if(StringUtils.isBlank(opponentId))
		{
			throw new SocialBettingValidationException(ErrCode.MISSING_OPPONENT_ID, "cannot update bet, opponent id is missing");
		}
		if(StringUtils.isBlank(gameResult))
		{
			throw new SocialBettingValidationException(ErrCode.MISSING_GAME_RESULT, "the game result is required for update bet.");
		}
		
		if(StringUtils.isBlank(eventId))
		{
			throw new SocialBettingValidationException(ErrCode.MISSING_EVENT_ID, "event id is required for update the bet.");
		}
		BettingEvent event = this.eventDao.getEventById(eventId);
		if(event == null)
		{
			throw new SocialBettingValidationException(ErrCode.EVENT_NOT_FOUND, "can not update bet, the betting event is not found!");
		}
		if(event.getStatus() == StatusEnum.SETTLED)
		{
			throw new SocialBettingValidationException(ErrCode.EVENT_IS_SETTLED, "update bet is not allowed, the event is settled.");
		}
		String targetId = null; 
		List<BettingOpponent> opponents = event.getOpponents();
		boolean found = false;
		for(BettingOpponent opponent: opponents)
		{
			String oppId = opponent.getOpponentId();
			if(opponentId.trim().equals(oppId))
			{
				found = true;
				targetId = opponent.getTargetId();
			}
		}
		
		if(!found)
		{
			throw new SocialBettingValidationException(ErrCode.OPPONENT_ID_EVENT_ID_NOT_MATCHED, new StringBuilder("cannot find opponent:").append(opponentId).append(" in event:").append(eventId).toString());
		}
		if(StringUtils.isBlank(targetId))
		{
			throw new SocialBettingInternalException("target id is not set. which should be set during place bet phase.do you passed the right player id?");
		}
		List<BettingTarget> targets = event.getTargets();
		BetTypeEnum betType = event.getBetType();
		BettingTarget target = null;
		for(BettingTarget t: targets)
		{
			if(targetId.trim().equals(t.getTargetId()))
			{
				target = t;
			}
		}
		if(target == null)
		{
			throw new SocialBettingInternalException("opponent and target are not matched. event id is:"+eventId);
		}
		String gResult = target.getGameResult();
		if(StringUtils.isNotBlank(gResult) && gResult.equals(TimerStatusEnum.COMPLETION_TIMER_EXPIRED.value()))
		{
			throw new SocialBettingValidationException(ErrCode.MAX_TIME_TO_COMPLETE_EXCEED, "failed to update the bet with the game result, max time allowed to complete was passed");
		}
		target.setGameResult(gameResult);
		Map parameters = null;
		try
		{
			parameters = PropertyUtils.describe(request);
		}catch(Exception e)
		{
			if(log.isDebugEnabled()) log.debug("failed to get some of the fields.", e);
		}
		if(parameters == null)
		{
			throw new SocialBettingInternalException( "failed to parse the updateBet request.");

		}
		event = this.ruleEngine.exeRule(event, EngineSupportedMethodEnum.UPDATE_BET, projectId, betType, parameters);
		List<TransactionParticipantModel> notifications = null;
		if(event.getStatus() == StatusEnum.SETTLED)
        {
			notifications = this.generateNotification(event, request, SocialBettingCallTypeEnum.SETTLED);
			//this.dao.atomicSave(notifications, event, false);
			this.bettingMessageProducer.atomicSave(notifications, event, false);
        }else
        {
        	this.eventDao.updateEvent(event);
        }
        BetSettlementDTO result= SocialBettingDataConverter.converToSettlementDTO(event);
		return result;
	}

	@Override
	public BetHistoryResult getBetHistory(HistoryRequestBean request) {
		String opponentId = request.getOpponentId();
		Long projectId = request.getProjectId();
		BetTypeEnum betType = request.getBetType();
		int limit = request.getLimit() == null? 0: request.getLimit();
		if(limit == 0 || limit > LIMIT)
		{
			limit = LIMIT;
		}
		int begin = request.getBegin() == null? 0 : request.getBegin();
		Date startDate = request.getStartDate();
		Date endDate = request.getEndDate();
		if(request.getStatus() == null)
		{
			throw new SocialBettingValidationException(ErrCode.INVALID_HISTORY_STATUS,"invalid status, it should be one of the following:CREATED,MATCHED,LIVE,SETTLED or EXPIRED");
		}
		String status = request.getStatus().getStatusValue();
		List<BetHistoryDTO> result = new ArrayList<BetHistoryDTO>();
		List<BettingEvent> events = this.eventDao.getBetHistory(opponentId, projectId,betType, status, startDate, endDate, begin, limit);
		if(events != null)
		{
			for(BettingEvent event : events)
			{
				BetHistoryDTO history = SocialBettingDataConverter.convertTOBetHistory(event, opponentId);
				result.add(history);
			}
		}
		BetHistoryResult history = new BetHistoryResult();
		history.setSuccess(true);
		history.setResult(result);
		return history;
	}
	
	public SocialBettingRuleDao getDao() {
		return dao;
	}

	public void setDao(SocialBettingRuleDao dao) {
		this.dao = dao;
	}

	public RestCRMClient getCrmClient() {
		return crmClient;
	}

	public void setCrmClient(RestCRMClient crmClient) {
		this.crmClient = crmClient;
	}

	public RuleEngine getRuleEngine() {
		return ruleEngine;
	}

	public void setRuleEngine(RuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}
	
	private List<TransactionParticipantModel> generateNotification(BettingEvent event, BaseRequestDTO request, SocialBettingCallTypeEnum callType)
	{
		List<BettingOpponent> opps = event.getOpponents();
		String currency = event.getCurrency();
		BettingOpponent other = null;
		for(BettingOpponent opp : opps)
		{
			if(!opp.getOpponentId().equals(request.getOpponentId()))
			{
				other = opp;
			}
		}
		BetNotification otherBn = null;
		BetNotification thisBn = null;
		if(callType == SocialBettingCallTypeEnum.PLACED)
		{
			otherBn = SocialBettingDataConverter.convertToNotification(other.getOpponentId(), event, other.getPlatformUserId(), currency, SocialBettingCallTypeEnum.MATCHED);
			
			thisBn = SocialBettingDataConverter.convertToNotification(request.getOpponentId(), event, request.getPlatformUserId(), currency, SocialBettingCallTypeEnum.PLACED);
		}else if(callType == SocialBettingCallTypeEnum.SETTLED)
		{
			otherBn = SocialBettingDataConverter.convertToNotification(other.getOpponentId(), event, other.getPlatformUserId(), currency, SocialBettingCallTypeEnum.SETTLED);
			
			thisBn = SocialBettingDataConverter.convertToNotification(request.getOpponentId(), event, request.getPlatformUserId(), currency, SocialBettingCallTypeEnum.SETTLED);
		}
		List<TransactionParticipantModel> notifs = new ArrayList<TransactionParticipantModel>();
		notifs.add(otherBn);
		notifs.add(thisBn);
		return notifs;
	}

	
	
	
	public RedisTemplate<String, String> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public TiersOddsResult findOddsByTiers(TiersOddsRequest request) {
		List<Integer> tiers = request.getTiers();
		long projectId = request.getProjectId();
		String opponentId = request.getOpponentId();
		String platformUserId = request.getPlatformUserId();
		BetTypeEnum betType = request.getBetType();
		if(betType == null)
		{
			throw new SocialBettingValidationException(ErrCode.INVALID_BET_TYPE, "bet type is required!");
		}
		boolean isRelative = request.getIsRelative() == null? false : request.getIsRelative();
		Integer opponentTier = null;
		try
		{
			opponentTier = this.crmClient.getTier(platformUserId,opponentId, projectId, betType);
		}catch(Exception e)
		{
			if(log.isDebugEnabled()) log.debug("failed to get the tier from crm."+e);
		}
		if(opponentTier == null)
		{
			throw new SocialBettingValidationException(ErrCode.NOT_ELIGIBLE_FOR_BET, "the bettor is not from the targetting group!");
		}
		Double opponentSeededOdds = this.dao.getOddsByProjectTier(projectId, opponentTier);
		if(isRelative)
		{
			List<Integer> trueTiers = new ArrayList<Integer>();
			for(Integer rate : tiers)
			{
				Integer trueTier = rate+opponentTier;
				if(trueTier > 0)
				{
					trueTiers.add(trueTier);
				}else
				{
					log.error("oppoent tier is negative after deduction: "+opponentTier+""+rate+(trueTier));
					throw new SocialBettingValidationException(ErrCode.BAD_RELATIVE_TIERS, "invalid relative tier, after applying the true tier is less than 1.");
				}
				
			}
			if(trueTiers.isEmpty())
			{
				throw new SocialBettingValidationException(ErrCode.BAD_RELATIVE_TIERS, "relative tiers did not generate valid tiers");
			}
			tiers = trueTiers;
		}
		
		OddsRule oddsRule = (OddsRule)this.dao.getRule(RuleNamesEnum.ODDS, projectId, betType);
		Double takeOutPercentage = oddsRule.getTakeOutPercentage();
		if(takeOutPercentage == null)
		{
			throw new SocialBettingInternalException("takeout percentage is undefined.");
		}
		if(takeOutPercentage > 1)
		{
			throw new SocialBettingInternalException("takeout percentage should be less than one but it is: "+takeOutPercentage);
		}
		TiersOddsResult result = new TiersOddsResult();
		List<TiersResult> tierResults = result.getTiers();
		
		for(Integer tier : tiers)
		{
			if(tier < 1)
			{
				throw new SocialBettingValidationException(ErrCode.BAD_TIERS_NUMBER, "invalid tier, tier number must larger than 0.");

			}
			String tierKey = tier+"-"+projectId;
			Double p2SeededOdds = this.dao.getOddsByProjectTier(projectId, tier);
			Double opponentOfferedOdds = (p2SeededOdds / opponentSeededOdds) * (1- takeOutPercentage) + 1;
			TiersResult tierResult = new TiersResult();
			tierResult.setOfferedOdds(opponentOfferedOdds);
			tierResult.setTier(tier);
			List<OddsUid> uids = tierResult.getUids();
			ListOperations<String, String> listOps = this.redisTemplate.opsForList();
			List<String> userIds = listOps.range(tierKey, START_IDX, END_IDX);
			int idx = 0;
			for(String uid : userIds)
			{
				if(idx < 3 && uid != null && !opponentId.trim().equals(uid.trim()))
				{
					OddsUid ouid = new OddsUid();
					ouid.setUid(uid);
					uids.add(ouid);
					idx++;
				}
				
			}
			tierResults.add(tierResult);	
		}
		return result;
	}

	@Override
	public OpponentsOddsResult findOddsByUsers(OpponentsOddsRequest request) {
		List<String> opponentPlayerPlatformIds = request.getOpponentPlayerPlatformIds();
		Long projectId = request.getProjectId();
		BetTypeEnum betType = request.getBetType();
		String platformUserId = request.getPlatformUserId();
		String opponentId = request.getOpponentId();
		OpponentsOddsResult result = new OpponentsOddsResult();
		List<OpponentsOdds> odds = result.getResults();
		
		if(betType == null)
		{
			throw new SocialBettingValidationException(ErrCode.INVALID_BET_TYPE, "bet type is required!");
		}
		OddsRule oddsRule = (OddsRule)this.dao.getRule(RuleNamesEnum.ODDS, projectId, betType);
		Double takeOutPercentage = oddsRule.getTakeOutPercentage();
		if(takeOutPercentage == null)
		{
			throw new SocialBettingInternalException("takeout percentage is undefined.");
		}
		if(takeOutPercentage > 1)
		{
			throw new SocialBettingInternalException("takeout percentage should be less than one but it is: "+takeOutPercentage);
		}
		Integer selfTier = this.crmClient.getTier(platformUserId, opponentId, projectId, betType);
		Double selfSeededOdds = this.dao.getOddsByProjectTier(projectId, selfTier);
		for(String platformId : opponentPlayerPlatformIds)
		{	
			if(StringUtils.isNotBlank(platformId) && !platformUserId.trim().equals(platformId.trim()))
			{
				Integer tier = this.crmClient.getTier(platformId,null, projectId, betType);
				if(tier == null)
				{
					throw new SocialBettingValidationException(ErrCode.NOT_ELIGIBLE_FOR_BET, "user "+platformId+" is not allowed to bet.");
				}
				Double opponentSeededOdds = this.dao.getOddsByProjectTier(projectId, tier);
				Double selfOfferedOdds = (opponentSeededOdds / selfSeededOdds) * (1- takeOutPercentage) + 1;			
				OpponentsOdds opOdds = new OpponentsOdds();
				opOdds.setPlatformUserId(platformId);
				opOdds.setOdds(selfOfferedOdds);
				odds.add(opOdds);
			}	
		}
		return result;
	}

	
	public BettingMessageProducer getBettingMessageProducer() {
		return bettingMessageProducer;
	}

	public void setBettingMessageProducer(
			BettingMessageProducer bettingMessageProducer) {
		this.bettingMessageProducer = bettingMessageProducer;
	}

	public EventDao getEventDao() {
		return eventDao;
	}

	public void setEventDao(EventDao eventDao) {
		this.eventDao = eventDao;
	}
}
