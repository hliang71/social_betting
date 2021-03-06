package com.openbet.socailbetting.executor;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.openbet.socailbetting.dao.impl.SocialBettingRuleDao;
import com.openbet.socailbetting.exception.SocialBettingInternalException;
import com.openbet.socailbetting.model.BetNotification;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.model.SocialBettingTransaction;
import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.MD5Encryter;
import com.openbet.socailbetting.utils.TransactionStatusEnum;

public class DefaultCallbackCommand extends BaseRuleExecutor{
	private static final Logger log = Logger.getLogger(DefaultCallbackCommand.class);
	private static final String BATCH_SIZE = "batchSize";
	private static final String PROJECT_ID = "projectId";
	private static final String BET_TYPE = "betType";
	private static final String BEGIN = "begin";
	public static final Integer DEFAULT_BATCH_SIZE=10;
	private static final Integer EXPIRE_THREDHOLD=10;
	private static final int READ_TIMEOUT=100;
	private static final int CONNECT_TIMEOUT=100;
	
	
	private RestTemplate restTemplate;
	
	@Autowired
    @Qualifier("ruleDao")
    private SocialBettingRuleDao dao;

	public RestTemplate getRestTemplate() {
		
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		//( (CommonsClientHttpRequestFactory) this.restTemplate.getRequestFactory() ).setReadTimeout( READ_TIMEOUT );
		//( (CommonsClientHttpRequestFactory) this.restTemplate.getRequestFactory() ).setConnectTimeout(CONNECT_TIMEOUT);
		
	}

	public SocialBettingRuleDao getDao() {
		return dao;
	}

	public void setDao(SocialBettingRuleDao dao) {
		this.dao = dao;
	}

	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		Map<String, Object> param = (Map<String, Object>)parameters;
		String callbackUrl = (String) this.getValue();
		Integer batchSize =(Integer) param.get(BATCH_SIZE);
		String betType = (String)param.get(BET_TYPE);
		Long projectId = (Long)param.get(PROJECT_ID);
		Integer begin = (Integer) param.get(BEGIN);
		if(batchSize == null)
		{
			batchSize = DEFAULT_BATCH_SIZE;
		}
		if(projectId == null || projectId == 0l)
		{
			throw new SocialBettingInternalException("project id is required for callback");
		}
		if(StringUtils.isBlank(betType))
		{
			throw new SocialBettingInternalException("betType is required for callback");
		}
		if(StringUtils.isBlank(callbackUrl))
		{
			throw new SocialBettingInternalException("callback url is required for callback");
		}
		Long platformId = this.dao.lookupPlatformIdByProjectId(projectId);
		if(platformId == null)
		{
			throw new SocialBettingInternalException("platform id is not defined for project:"+projectId);
		}
		
		List<BetNotification> notifications = this.dao.getNewNotifications(projectId, betType, batchSize, begin);
		
		
		String projSecret = null;
		
		try
    	{
    		projSecret = this.dao.getKey(projectId);
    	}catch(Exception e)
    	{
    		log.info("failed to get the project secret:"+e.toString());
    	}
    	String key = StringUtils.isBlank(projSecret)? "" : projSecret;
    	
		
		for(BetNotification notif: notifications)
		{
			
				//this.restTemplate.getForObject(callbackUrl, String.class, notif);
			notif.setP_id(platformId);
			String content = notif.getContent();
			
			content = new StringBuilder(content).append(key).toString();
			log.info("####################CONTENT IS:"+content);
	    	String sig = MD5Encryter.encryptToHex(content);
	    	if(log.isDebugEnabled()) log.debug("callback sig is:"+sig);
	    	notif.setSig(sig);
			
			String txId = notif.getTransactionId();
			
			SocialBettingTransaction tx = this.dao.getTransacitonById(txId);
			if(tx != null)
			{
				TransactionStatusEnum status = tx.getStatus();			
				if(status == TransactionStatusEnum.COMMITED)
				{
					this.dao.atomicPost(notif, this.restTemplate, callbackUrl);
				}else
				{
					BettingEvent  bEvent = this.dao.findEventByTransactionId(projectId, betType, txId);
					if(bEvent != null)
					{
						this.dao.atomicPost(notif, this.getRestTemplate(), callbackUrl);
					}else
					{
						Date createdTime = notif.getTransactionStartTime();
						Date expectedExpireTime = null;
						if(createdTime != null)
						{
							expectedExpireTime = DateUtils.addMinutes(createdTime, EXPIRE_THREDHOLD);		
						}
						if(createdTime == null || new Date().after(expectedExpireTime) )
						{
							this.dao.delete(notif);
						}
					}
				}
				
			}else
			{
				this.dao.delete(notif);
			}
			
		}
		return event;
	}
}
