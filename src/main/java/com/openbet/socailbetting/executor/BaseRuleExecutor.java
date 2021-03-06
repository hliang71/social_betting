package com.openbet.socailbetting.executor;

import java.util.UUID;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.ErrCode;

public abstract class BaseRuleExecutor implements RuleExecutor, ApplicationContextAware{
	protected String rule;
	protected Object value;
	private ApplicationContext ctx;	
	private MessageSourceAccessor messages;

	

	
	@Autowired
    public void setMessages(MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }
	
	public String getText(String msgKey) {
        return messages.getMessage(msgKey);
    }

	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException 
	{
		this.ctx = applicationContext;
		
	}
	
	public ApplicationContext getCtx() {
		return ctx;
	}

	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getEventId(BettingEvent event)
	{
		Long projectId = event.getProjectId();
		BetTypeEnum betType = event.getBetType();
		String eventId = UUID.randomUUID().toString();
		StringBuilder builder = new StringBuilder(projectId.toString());		
		builder.append(ErrCode.SB_SEPARATER.value());
		builder.append(betType.value());
		builder.append(ErrCode.SB_SEPARATER.value());
		builder.append(eventId);	
		return builder.toString();
	}
	public abstract BettingEvent execute(BettingEvent event, Object parameters);
}
