package com.openbet.socailbetting.service.impl;

import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import com.openbet.socailbetting.service.BrokerShutdownHook;

public class BrokerShutdownHookImpl implements Runnable, ApplicationContextAware, BrokerShutdownHook {
	private static final Logger log = Logger.getLogger(BrokerShutdownHookImpl.class);
    ApplicationContext applicationContext;
    @Autowired
    @Qualifier("brokerSB")
    private BrokerService bs;
    
    public void setBs(BrokerService bs) {
		this.bs = bs;
	}

	public void run() {
        this.shutdown();
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) applicationContext).close();
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

	
	

	public BrokerService getBs() {
		return bs;
	}

	
	public void shutdown()
	{
		log.info("############shut down broker ###################");
		BrokerService bs = this.getBs();
		try
		{
			bs.stop();
		}catch(Exception e)
		{
			log.info("shut down broker failed:", e);
		}
	}
	
}
