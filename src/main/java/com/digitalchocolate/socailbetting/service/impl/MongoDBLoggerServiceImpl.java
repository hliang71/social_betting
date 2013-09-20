package com.digitalchocolate.socailbetting.service.impl;



import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import com.digitalchocolate.socailbetting.dao.BaseDao;

import com.digitalchocolate.socailbetting.model.LogTarget;
import com.digitalchocolate.socailbetting.model.rule.OddsRule;
import com.digitalchocolate.socailbetting.model.rule.OpponentRule;
import com.digitalchocolate.socailbetting.model.rule.SettlementRule;
import com.digitalchocolate.socailbetting.service.LogService;
import com.digitalchocolate.socailbetting.utils.RuleNamesEnum;


@Service("logManager")
//@Transactional
public class MongoDBLoggerServiceImpl implements LogService<LogTarget> {
/**
    private static final Logger logger = Logger.getLogger(MongoDBLoggerServiceImpl.class);
   
    @Autowired
    @Qualifier("logItemDao")
    private BaseDao<LogTarget> dao;
    
    
    

    public BaseDao<LogTarget> getDao() {
        return dao;
    }
    
    public void setDao(BaseDao<LogTarget> dao) {
        this.dao = dao;
    }

    

	@Override
    public Long add(LogTarget log) {
        logger.debug("Adding a new LogItem instance");
        return dao.insert(log);
    }
    @Override
    public Object get( String sid) {
    	
        if(sid.equals(RuleNamesEnum.ODDS.value()))
        {
        	return new OddsRule();
        }else if(sid.equals(RuleNamesEnum.OPPONENT.value()))
        {
        	return new OpponentRule();
        }else
        {
        	return new SettlementRule();
        }
    }
    @Override
    public Long updateEvent()
    {
    	return dao.updateEvent();
    }
    @Override
    public Long insertEvent()
    {
    	return dao.insertEvent();
    }
  */  
}