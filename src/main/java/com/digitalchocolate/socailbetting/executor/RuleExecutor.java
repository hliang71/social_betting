package com.digitalchocolate.socailbetting.executor;

import com.digitalchocolate.socailbetting.model.BettingEvent;

public interface RuleExecutor {
	BettingEvent execute(BettingEvent event, Object parameters);
}
