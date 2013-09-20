package com.openbet.socailbetting.engine;


import java.util.Map;

import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.EngineSupportedMethodEnum;

public interface RuleEngine {
	//List<RuleExecutor> runRule(String methodName, Long projectId, BetTypeEnum betType);
	
	BettingEvent exeRule(BettingEvent event, EngineSupportedMethodEnum name, Long projectId, BetTypeEnum betType, Map<String, Object> parameters);
}
