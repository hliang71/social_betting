package com.digitalchocolate.socailbetting.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.digitalchocolate.socailbetting.dao.BaseRuleDao;
import com.digitalchocolate.socailbetting.executor.BaseRuleExecutor;
import com.digitalchocolate.socailbetting.executor.RuleExecutor;
import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.model.rule.BaseRule;
import com.digitalchocolate.socailbetting.utils.BetTypeEnum;
import com.digitalchocolate.socailbetting.utils.EngineSupportedMethodEnum;
import com.digitalchocolate.socailbetting.utils.RuleNamesEnum;

@Service("ruleEngine")
public class DefaultRuleEngineImpl extends AbstractRuleEngine implements RuleEngine{
	
	@Autowired
    @Qualifier("ruleDao")
	private BaseRuleDao ruleDao;
	
	private static final Logger log = Logger.getLogger(DefaultRuleEngineImpl.class);
	
	@Override
	public BettingEvent exeRule(BettingEvent event, EngineSupportedMethodEnum name, Long projectId, BetTypeEnum betType, Map<String, Object> parameters)
	{
		List<RuleExecutor> executors = this.runRule(name, projectId, betType);
		for (RuleExecutor executor : executors)
		{
			event = executor.execute(event, parameters);
		}
		return event;
	}
	
	
	private List<RuleExecutor> runRule(EngineSupportedMethodEnum name, Long projectId, BetTypeEnum betType)
	{
		List<RuleExecutor> result = new ArrayList<RuleExecutor>();		
		String ruleOrders = new StringBuilder(name.value()).append("Orders").toString();
		for(RuleNamesEnum engineName : subEngines)
		{
			BaseRule rule = ruleDao.getRule(engineName, projectId, betType);
			
			try
			{
				Map map = PropertyUtils.describe(rule);
				if(log.isDebugEnabled()) log.debug("rule map is :"+map);
				List<String> executorOrders =(List<String>) map.get(ruleOrders);
				if(executorOrders != null)
				{
					for(String field : executorOrders)
					{
						String exeName = new StringBuilder(field).append("_").append(betType.toString()).toString();  
						String beanName = this.getText(exeName);
						ApplicationContext ctx = this.getCtx();
						BaseRuleExecutor executor = ctx.getBean(beanName, BaseRuleExecutor.class);
						Object value = map.get(field);
						executor.setRule(field);
						executor.setValue(value);
						result.add(executor);
					}
				}
			}catch(Exception e)
			{
				log.error("run rule failed", e);
			}
		}
		return result;
	}
	
	
	public BaseRuleDao getRuleDao() {
		return ruleDao;
	}
	
	public void setRuleDao(BaseRuleDao ruleDao) {
		this.ruleDao = ruleDao;
	}
	
}
