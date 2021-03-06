package com.openbet.socailbetting.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.web.client.RestTemplate;

import com.openbet.socailbetting.dao.impl.SocialBettingRuleDao;
import com.openbet.socailbetting.exception.SocialBettingInternalException;
import com.openbet.socailbetting.exception.SocialBettingValidationException;
import com.openbet.socailbetting.model.ContentType;
import com.openbet.socailbetting.model.CustomizerType;
import com.openbet.socailbetting.model.TierType;
import com.openbet.socailbetting.model.rule.CallbackRule;
import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.ErrCode;
import com.openbet.socailbetting.utils.MD5Encryter;
import com.openbet.socailbetting.utils.RuleNamesEnum;

public class RestCRMClient {
	private static final Logger log = Logger.getLogger(RestCRMClient.class);
	private static final Integer EXPIRED_TIME= 6;
	private static final Integer START_IDX=0;
	private static final Integer END_IDX=3;
	@Autowired
    @Qualifier("ruleDao")
    private SocialBettingRuleDao dao;
	
	@Autowired
    @Qualifier("redisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	private RestTemplate restTemplate;
	 
 
    private volatile String userLevelUrl;
    
    
   
    @SuppressWarnings("unchecked")
	public Integer getTier(final String platformUserId,final String opponentId, Long projectId, BetTypeEnum betType) {
    	if(StringUtils.isBlank(userLevelUrl))
    	{
    		CallbackRule callbackRule = (CallbackRule)dao.getRule(RuleNamesEnum.CALLBACK, projectId, betType);
    		String userLevelUrl = callbackRule.getUserLevelUrl();
    		this.setUserLevelUrl(userLevelUrl);
    	}
    	if(StringUtils.isBlank(userLevelUrl))
    	{
    		throw new SocialBettingInternalException("userLevelUrl is undefined in callback rule.");
    	} 
    	if(StringUtils.isBlank(platformUserId))
    	{
    		throw new SocialBettingValidationException(ErrCode.SECURITY_PLATFORM_USER_ID_REQUIRED, "cannot get tier from crm, platform user id is required.");
    	}
    	if(projectId == null && projectId == 0)
    	{
    		throw new SocialBettingValidationException(ErrCode.SECURITY_PROJECT_ID_REQUIRED, "projectId is required for getting tier from crm.");
    	}
    	String projSecret = null;
    	String redisKey = platformUserId+projectId;
    	Integer tier = null;
    	try
    	{
    		String tierStr = this.redisTemplate.opsForValue().get( redisKey );
    		tier = Integer.valueOf(tierStr);
    	}catch(Exception e)
    	{
    		if(log.isDebugEnabled()) log.debug("no tier is store in redis data store"+e);
    	}
    	if(tier != null && tier != 0)
    	{
    		log.info("found in redis data store");
    		return tier;
    	}
    	
    	try
    	{
    		projSecret = this.dao.getKey(projectId);
    	}catch(Exception e)
    	{
    		log.info("failed to get the project secret:"+e.toString());
    	}
    	String key = StringUtils.isBlank(projSecret)? "" : projSecret;
    	String content = new StringBuilder(platformUserId).append(key).toString();
    	String sig = MD5Encryter.encryptToHex(content);
    	if(log.isDebugEnabled()) log.debug("crm sig is:"+sig);
    	Integer result = null;
    	try
    	{
    		
        	CustomizerType customizer = this.getRestTemplate().getForObject(userLevelUrl, CustomizerType.class, projectId, platformUserId, sig);
        	if(log.isDebugEnabled()) log.debug("customizer is:"+customizer);
        	List<ContentType> contents = customizer.getContent();
        	List<TierType> tiers = new ArrayList<TierType>();
        	if(contents != null)
        	{
        		for(ContentType con : contents)
        		{
        			List<TierType> ts = con.getTier();
        			if(ts != null && !ts.isEmpty())
        			{
        				tiers.addAll(ts);
        			}
        		}
        	}
        	Collections.sort(tiers);
        	int size = tiers.size();
        	if(size > 0)
        	{
        		TierType maxTier = tiers.get(size -1);
        		String maxValue = maxTier.getValue();
        		result = Integer.valueOf(maxValue);
        	}	
    	}catch(Exception e)
    	{
    		log.info("user does not belong to any defined crm user group."+e.toString());
    	}
    	if(result != null)
    	{
    		final String tierKey = result+"-"+projectId;
    		this.redisTemplate.opsForValue().set( redisKey, (result +""));
    		this.redisTemplate.expire( redisKey, EXPIRED_TIME, TimeUnit.SECONDS );
            this.redisTemplate.execute(new SessionCallback() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public Object execute(RedisOperations operations) throws DataAccessException 
				{
					if(StringUtils.isNotBlank(opponentId))
					{
						ListOperations<String, String> lop = operations.opsForList();
						List<String> content = lop.range(tierKey, START_IDX, END_IDX);
						if(!content.contains(opponentId))
						{
							lop.leftPush(tierKey, opponentId);
							lop.trim(tierKey, START_IDX, END_IDX);
						}
					}
					return null;	
				}
			}
           );
    	}
    	log.info("user tier is : "+result);
    	return result;
    }

	public RestTemplate getRestTemplate() {
		
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		//( (CommonsClientHttpRequestFactory) this.restTemplate.getRequestFactory() ).setReadTimeout( READ_TIMEOUT );
		//( (CommonsClientHttpRequestFactory) this.restTemplate.getRequestFactory() ).setConnectTimeout(CONNECT_TIMEOUT);
		
	}

	public String getUserLevelUrl() {
		return userLevelUrl;
	}

	public void setUserLevelUrl(String userLevelUrl) {
		this.userLevelUrl = userLevelUrl;
	}

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
    
}

