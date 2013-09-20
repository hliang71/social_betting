package com.digitalchocolate.socailbetting.executor;

import org.springframework.context.ApplicationContext;

import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.utils.StartCompletionEvaluateEnum;
import com.digitalchocolate.socailbetting.utils.StatusEnum;

public class DefaultStartEvaluationCommand extends BaseRuleExecutor{
	private static final String PREFIX="START_EVALUATION_COMMAND";

	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		StatusEnum status = event.getStatus();
		StartCompletionEvaluateEnum criteria = (StartCompletionEvaluateEnum)this.getValue();
		String criteriaValue = criteria.value();
		String commandName = new StringBuilder(PREFIX).append("_").append(criteriaValue).toString();
		commandName = this.getText(commandName);
		
		if(status == StatusEnum.LIVE)
		{
			ApplicationContext ctx = super.getCtx();
			BaseRuleExecutor exe = ctx.getBean(commandName, BaseRuleExecutor.class);
			event = exe.execute(event, parameters);
		}
		return event;
	}

}
