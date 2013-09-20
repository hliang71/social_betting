package com.digitalchocolate.socailbetting.engine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import com.digitalchocolate.socailbetting.utils.RuleNamesEnum;

public class AbstractRuleEngine implements ApplicationContextAware{
	static List<RuleNamesEnum> subEngines = new ArrayList<RuleNamesEnum>();//RuleNamesEnum.OPPONENT, RuleNamesEnum.ODDS, RuleNamesEnum.SETTLEMENT}; 
    static 
    {
    	subEngines.add(RuleNamesEnum.OPPONENT);
    	subEngines.add(RuleNamesEnum.ODDS);
    	subEngines.add(RuleNamesEnum.SETTLEMENT);
    	subEngines.add(RuleNamesEnum.CALLBACK);
    }
	
	private ApplicationContext ctx;
	private MessageSourceAccessor messages;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException 
	{
		this.ctx = applicationContext;
		
	}

	public ApplicationContext getCtx() {
		return ctx;
	}

	public void setCtx(ApplicationContext ctx) {
		this.ctx = ctx;
	}
	
	@Autowired
    public void setMessages(MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }
	
	public String getText(String msgKey) {
        return messages.getMessage(msgKey);
    }

}
