package com.openbet.socailbetting.executor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;



import com.openbet.socailbetting.exception.RuleEngineException;
import com.openbet.socailbetting.executor.listener.CompletionExpireLisener;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.model.BettingTarget;
import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.ConcurrentRedisMap;
import com.openbet.socailbetting.utils.ErrCode;
import com.openbet.socailbetting.utils.ExpiringMap;
import com.openbet.socailbetting.webapp.EventTargetVO;

public class DefaultStartCompletionTimerCommand extends BaseRuleExecutor{
	private static final String ID = "DefaultStartCompletionTimerCommand";
	private static final Integer CHECK_INTERVAL = 3;
	private static final String TARGET_ID="targetId";
	private static final Logger log = Logger.getLogger(DefaultStartCompletionTimerCommand.class);
	private CompletionExpireLisener completionListener;
	
	private Map<Integer, ExpiringMap<String, EventTargetVO>> timers = new ConcurrentHashMap<Integer, ExpiringMap<String, EventTargetVO>>();
	private ConcurrentMap<Integer, Integer> persistTimeKeys;

	
	 public void init()
	 {
		 log.info("init complition timer.............");
		 persistTimeKeys = new ConcurrentRedisMap<Integer,Integer>(ID, this.getCtx(), null);
		 Set<Integer> timeKeySet = this.persistTimeKeys.keySet();
		 ApplicationContext ctx = this.getCtx();
	     for(Integer key : timeKeySet)
	     {
			String identifier = ID+key;
			ExpiringMap<String, EventTargetVO> timer = new ExpiringMap<String, EventTargetVO>(key, CHECK_INTERVAL, identifier, ctx);
			timer.addExpirationListener(completionListener);
		    timer.getExpirer().startExpiringIfNotStarted();
		    timers.put(key, timer);
	     }
	     this.persistTimeKeys.clear();
	 }
	    
	
	@SuppressWarnings("unchecked")
	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		log.info("start completion timer.");
		Map<String, Object> param = (Map<String, Object>) parameters;
		String targetId =(String) param.get(TARGET_ID); // if this is for place bet then target id is required.
		if(StringUtils.isBlank(targetId)) // if this is for generate bet then we only have one target.
		{
			List<BettingTarget> ts = event.getTargets();
			if (ts.size() == 1)
			{
				BettingTarget t = ts.get(0);
				targetId = t.getTargetId();		
			}
		}
		if(StringUtils.isBlank(targetId))
		{
			log.info("target id is missing in event "+event.getId());
			throw new RuleEngineException(ErrCode.TARGET_ID_REQUIRED, "targetId is required for placing the bet.");
		}
		
		Integer maxAllowedCompletionTime = (Integer)this.getValue();
		if(maxAllowedCompletionTime == null || maxAllowedCompletionTime == 0)
		{
			throw new RuleEngineException(ErrCode.MAX_TIME_TO_COMPLETE_UNDEFINE, "max time to complete the game is not defined.");
		}
		ExpiringMap<String, EventTargetVO> timer = timers.get(maxAllowedCompletionTime);
		this.persistTimeKeys.putIfAbsent(maxAllowedCompletionTime, maxAllowedCompletionTime);
		if(timer == null)
		{
			
			ApplicationContext ctx = this.getCtx();
			String identifier = ID+maxAllowedCompletionTime;
			timer = new ExpiringMap<String, EventTargetVO>(maxAllowedCompletionTime, CHECK_INTERVAL, identifier, ctx);

			//timer = new ExpiringMap<String, EventTargetVO>(maxAllowedCompletionTime, maxAllowedCompletionTime / FACTOR);
			timer.addExpirationListener(completionListener);
		    timer.getExpirer().startExpiringIfNotStarted();
		    timers.put(maxAllowedCompletionTime, timer);
		}
		String eventId = event.getId();
		Long projectId = event.getProjectId();
		BetTypeEnum betType = event.getBetType();
		if(StringUtils.isBlank(eventId))
		{
			eventId = this.getEventId(event);		
			event.setId(eventId);
		}
		EventTargetVO eTarget = new EventTargetVO();
		eTarget.setEventId(eventId);
		eTarget.setTargetId(targetId);
		eTarget.setProjectId(projectId);
		eTarget.setBetType(betType);
		String key = new StringBuilder(eventId).append(targetId).toString();
		timer.put(key, eTarget);
		return event;
	}

	public CompletionExpireLisener getCompletionListener() {
		return completionListener;
	}

	public void setCompletionListener(CompletionExpireLisener completionListener) {
		this.completionListener = completionListener;
	}

	

	
    
	
}
