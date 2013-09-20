package com.openbet.socailbetting.service.impl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.openbet.socailbetting.dao.impl.SocialBettingRuleDao;
import com.openbet.socailbetting.exception.SocialBettingInternalException;
import com.openbet.socailbetting.exception.SocialBettingMessageException;
import com.openbet.socailbetting.model.BetNotification;
import com.openbet.socailbetting.model.rule.CallbackRule;
import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.MD5Encryter;
import com.openbet.socailbetting.utils.RuleNamesEnum;
import com.openbet.socailbetting.webapp.BetNotificationMessage;


@Component
public class CallbackQueueListener extends AbstractQueueListener implements MessageListener
{
	private static final Logger log = Logger.getLogger(CallbackQueueListener.class);
	private static final Integer EXPIRED_TIME= 8;
	@Autowired
    @Qualifier("restTemplate")
	private RestTemplate restTemplate;
	
	@Autowired
    @Qualifier("redisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
    public void onMessage( final Message message )
    {
    	String uniqueKey = null;
    	try
    	{
	        TextMessage textMsg = (TextMessage) message;
	        String notificationMsg = textMsg.getText();
	        ObjectMapper mapper = new ObjectMapper();
	        SerializationConfig sConfig = mapper.getSerializationConfig();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sConfig.setDateFormat(format);
			DeserializationConfig dConfig = mapper.getDeserializationConfig();
			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dConfig.setDateFormat(dFormat);
			BetNotificationMessage notiMsg = mapper.readValue(notificationMsg, BetNotificationMessage.class);
			BetNotification notif = notiMsg.getNotif();
			BetTypeEnum betType = notif.getBetType();
			Long projectId = notif.getProjectId();
			
			if(projectId == null)
			{
				throw new SocialBettingInternalException("project id is required.");
			}
			SocialBettingRuleDao socialBettingDao = this.getSocialBettingDao();
			Long platformId = socialBettingDao.lookupPlatformIdByProjectId(projectId);
			if(platformId == null)
			{
				throw new SocialBettingInternalException("platform id is not defined for project:"+projectId);
			}
			notif.setP_id(platformId);
			String content = notif.getContent();
			String projSecret = null;
			
			try
	    	{
	    		projSecret = socialBettingDao.getKey(projectId);
	    	}catch(Exception e)
	    	{
	    		log.info("failed to get the project secret:"+e.toString());
	    	}
	    	String key = StringUtils.isBlank(projSecret)? "" : projSecret;
			content = new StringBuilder(content).append(key).toString();
			log.info("####################CONTENT IS:"+content);
	    	String sig = MD5Encryter.encryptToHex(content);
	    	if(log.isDebugEnabled()) log.debug("callback sig is:"+sig);
	    	notif.setSig(sig);
	    	uniqueKey = notif.getUniqueKey();
	    	String sendSig = this.redisTemplate.opsForValue().get(uniqueKey);
	    	if(StringUtils.isBlank(sendSig))
	    	{
		    	this.redisTemplate.opsForValue().set( uniqueKey, uniqueKey);
	    		this.redisTemplate.expire( uniqueKey, EXPIRED_TIME, TimeUnit.HOURS );
				CallbackRule rule = (CallbackRule)socialBettingDao.getRule(RuleNamesEnum.CALLBACK, projectId, betType);
				if(rule == null)
				{
					throw new SocialBettingInternalException("rule not found for notification, projectId="+projectId+" betType="+betType);                        
				}
				String callbackUrl = rule.getCallbackUrl();		
				if(StringUtils.isNotBlank(callbackUrl))
				{
					Map parameters = null;
					try
					{
						parameters = PropertyUtils.describe(notif);
					}catch(Exception e)
					{
						if(log.isDebugEnabled()) log.debug("failed to get some of the fields.", e);
					}
					if(parameters == null)
					{
						throw new SocialBettingInternalException( "failed to post, no parameters.");
		
					}
					String response = restTemplate.getForObject(callbackUrl, String.class, parameters);
				}
	    	}else
	    	{
	    		log.error("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@DUPLICATE MESSAGE DETECTED@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	    	}
    	}catch(Exception e)
    	{
    		log.error("callback failed.", e);
    		if(uniqueKey != null)
    		{
    			this.redisTemplate.delete(uniqueKey);
    		}
    		throw new SocialBettingMessageException("callback failed!", e);
    	}  
    }

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public RedisTemplate<String, String> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	} 
    
}


