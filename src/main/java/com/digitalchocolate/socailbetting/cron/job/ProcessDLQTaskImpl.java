package com.digitalchocolate.socailbetting.cron.job;

import javax.management.openmbean.CompositeData;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.web.BrokerFacadeSupport;
import org.apache.activemq.web.LocalBrokerFacade;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ProcessDLQTaskImpl implements ProcessDLQTask, ApplicationContextAware{
    private static final Logger log = Logger.getLogger(ProcessDLQTaskImpl.class);
	private static final int BATCH_SIZE = 10;
   
	private ApplicationContext ctx;
	
	@Override
	public void redeliverDLQ(String from, String to) {
		try 
		{
			BrokerFacadeSupport brokerQuery = this.ctx.getBean("brokerQuery", BrokerFacadeSupport.class);
			final QueueViewMBean queue = brokerQuery.getQueue(from);
			for (int i = 0; i < BATCH_SIZE && queue != null && queue.getQueueSize() > 0; i++) {
				CompositeData[] compdatalist = queue.browse();

				for (CompositeData cdata : compdatalist) {
					String messageID = (String) cdata.get("JMSMessageID");
					queue.moveMessageTo(messageID, to);
				}

			}
			/**long messageCount = queue.getQueueSize();
			int min = Long.valueOf(Math.min(messageCount, BATCH_SIZE)).intValue();
			CompositeData[] compdatalist = queue.browse();
			String[] messageIDs = new String[BATCH_SIZE];
			for (int i = 0; i < min; i++) {
				try
				{
					CompositeData cdata = compdatalist[i];
					log.info("message to be moved:"+cdata);
					String messageID = (String) cdata.get("JMSMessageID");
					messageIDs[i] = messageID;
				}catch(Exception e)
				{
					log.error("invalid message in the DLQ.", e);
				}
			}

			for (String messageID : messageIDs) {
				if(messageID != null)
				{
					try
					{
						log.info("move message to reqular queue. messageID:"+messageID+" to "+to);
						queue.moveMessageTo(messageID, to);
					}catch(Exception e)
					{
						log.error("move message to original queue failed.", e);
					}
				}
			}*/
		} catch (Exception e) {
			log.error("failed to create queue view mbean.",e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = applicationContext;
		
	}

}
