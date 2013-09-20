package com.openbet.socailbetting.executor;

import com.openbet.socailbetting.model.BettingEvent;

public interface RuleExecutor {
	BettingEvent execute(BettingEvent event, Object parameters);
}
