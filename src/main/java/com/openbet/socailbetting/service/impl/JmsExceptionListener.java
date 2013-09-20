package com.openbet.socailbetting.service.impl;


import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.springframework.jms.connection.CachingConnectionFactory;

public class JmsExceptionListener implements ExceptionListener
{
    private static final Logger log = Logger.getLogger(JmsExceptionListener.class);
	
    private CachingConnectionFactory cachingConnectionFactory;
    
    public CachingConnectionFactory getCachingConnectionFactory() {
		return cachingConnectionFactory;
	}

	public void setCachingConnectionFactory(
			CachingConnectionFactory cachingConnectionFactory) {
		this.cachingConnectionFactory = cachingConnectionFactory;
	}

	@Override
	public void onException(JMSException e) {
		log.error("jms throw exception:", e);
		this.cachingConnectionFactory.onException(e);
	}
}
