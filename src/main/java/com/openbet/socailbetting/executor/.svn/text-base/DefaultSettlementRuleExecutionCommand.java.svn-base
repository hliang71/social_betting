package com.openbet.socailbetting.executor;

import org.springframework.context.ApplicationContext;

import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.utils.CompletionRuleEnum;

public class DefaultSettlementRuleExecutionCommand extends BaseRuleExecutor{
    private static final String PREFIX="SETTLEMENT_RULE_EXECUTION_COMMAND";
	
    @Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		boolean readyForSettlement = event.getReadyForSettlement() == null? false : event.getReadyForSettlement();
		if(readyForSettlement)
		{
			CompletionRuleEnum rule = (CompletionRuleEnum)this.getValue();
			String ruleValue = rule.value();
			
			String commandName = new StringBuilder(PREFIX).append("_").append(ruleValue).toString();
			commandName = this.getText(commandName);
			
			ApplicationContext ctx = this.getCtx();
			BaseRuleExecutor command = ctx.getBean(commandName, BaseRuleExecutor.class);
			event = command.execute(event, parameters);
		}
		return event;	
	}

}
