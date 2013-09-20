package com.openbet.socailbetting.cron;

import org.apache.log4j.Logger;
import org.quartz.listeners.SchedulerListenerSupport;

public class NotificationJobSchedulerListener  extends SchedulerListenerSupport
{
	private static final Logger log = Logger.getLogger(NotificationJobSchedulerListener.class);
	private String clazName;
	private DBLockFacade lockFacade;
	
	public String getClazName()
	{
		return clazName;
	}
	public void setClazName(String clazName)
	{
		this.clazName = clazName;
	}
	public void schedulerStarted()
	{
		if(log.isTraceEnabled()) log.trace("scheduler started........");
	}
	public void schedulerShutdown()
	{
		log.info("Shutting down the scheduler.....");
		
		String jobName = "socialBetting_notification";
		long jobId=718432732L;
		try
		{
			BaseBetNotificationJob job = (BaseBetNotificationJob)Class.forName(clazName).newInstance();
			jobName=job.getJobName();
			
			
		}catch(Exception e)
		{
			log.error("Job listener did not setup correctly.", e);
		}
		lockFacade.releaseCronJobLock(jobName);
	}
	public DBLockFacade getLockFacade() {
		return lockFacade;
	}
	public void setLockFacade(DBLockFacade lockFacade) {
		this.lockFacade = lockFacade;
	}
	
	
	
}
