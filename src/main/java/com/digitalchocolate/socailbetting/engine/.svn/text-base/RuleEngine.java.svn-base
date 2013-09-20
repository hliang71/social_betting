package com.digitalchocolate.socailbetting.engine;


import java.util.Map;

import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.utils.BetTypeEnum;
import com.digitalchocolate.socailbetting.utils.EngineSupportedMethodEnum;

public interface RuleEngine {
	//List<RuleExecutor> runRule(String methodName, Long projectId, BetTypeEnum betType);
	
	BettingEvent exeRule(BettingEvent event, EngineSupportedMethodEnum name, Long projectId, BetTypeEnum betType, Map<String, Object> parameters);
}
