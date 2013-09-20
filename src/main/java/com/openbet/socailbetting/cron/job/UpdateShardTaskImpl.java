package com.openbet.socailbetting.cron.job;

import java.util.List;

import org.apache.log4j.Logger;

import com.openbet.socailbetting.dao.BaseRuleDao;
import com.openbet.socailbetting.model.rule.CallbackRule;

public class UpdateShardTaskImpl implements UpdateShardTask{
	private static final Logger log = Logger.getLogger(UpdateShardTaskImpl.class);
	
	private BaseRuleDao ruleDao;

	public BaseRuleDao getRuleDao() {
		return ruleDao;
	}

	public void setRuleDao(BaseRuleDao ruleDao) {
		this.ruleDao = ruleDao;
	}

	@Override
	public boolean updateShard(Integer begin, Integer pageSize, Integer totalBucks) {
		boolean hasMore = false;
		try
		{
			List<CallbackRule> rules = this.ruleDao.getAllCallbackRules(begin, pageSize);
			if(!rules.isEmpty())
			{
				hasMore = true;
			}
			for(CallbackRule rule : rules)
			{
				this.ruleDao.updateBucket(rule, totalBucks);
			}
		}catch(Exception e)
		{
			log.error("failed to calculate the bucket number for callback rules.", e);
			hasMore = false;
		}
		
		return hasMore;
	}

}
