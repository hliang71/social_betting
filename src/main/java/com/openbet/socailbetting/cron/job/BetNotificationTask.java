package com.openbet.socailbetting.cron.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.openbet.socailbetting.cron.BaseNotificationTask;
import com.openbet.socailbetting.dao.BaseRuleDao;
import com.openbet.socailbetting.engine.RuleEngine;
import com.openbet.socailbetting.model.rule.CallbackRule;
import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.EngineSupportedMethodEnum;

public class BetNotificationTask implements BaseNotificationTask{
	private static final Logger log = Logger.getLogger(BetNotificationTask.class);
	
	@Autowired
    @Qualifier("ruleEngine")
	private RuleEngine ruleEngine;
	
	private BaseRuleDao ruleDao;

	public BaseRuleDao getRuleDao() {
		return ruleDao;
	}

	public void setRuleDao(BaseRuleDao ruleDao) {
		this.ruleDao = ruleDao;
	}

	@Override
	public void sendBetNotification(Integer batchSize,Integer shardNum, Integer begin) {
		List<CallbackRule> rules = this.ruleDao.getCallbackRulesByShardNum(shardNum);
		for(CallbackRule rule : rules)
		{ 
			try
			{
				String callbackUrl = rule.getCallbackUrl();	
				BetTypeEnum betEnum = rule.getBetType();
				String betType = (betEnum == null) ? null : betEnum.value();
				Long projectId = rule.getProjectId();
			    
				
				Map<String, Object> parameters = new HashMap<String, Object>();
				
				parameters.put("batchSize", batchSize);
				parameters.put("projectId", projectId);
				parameters.put("betType", betType);	
				parameters.put("begin", begin);
				//log.info("################################# BEGIN IS:"+begin+"###############");
				this.ruleEngine.exeRule(null, EngineSupportedMethodEnum.CALLBACK, projectId, betEnum, parameters);
				
			}catch(Exception e)
			{
				log.info("failed to run callback for rule skipping :"+rule.getId(), e);
			}
		}
	}

	public RuleEngine getRuleEngine() {
		return ruleEngine;
	}

	public void setRuleEngine(RuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}
	
	

}
