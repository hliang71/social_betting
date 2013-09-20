package com.openbet.socailbetting.cron;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class BaseBetNotificationJob extends QuartzJobBean{
    private static final Logger log = Logger.getLogger(BaseBetNotificationJob.class);
	private SocialBettingTask task;
	private DBLockFacade lockFacade;
	
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException 
	{
		String jobName = this.getJobName();
		long jobId = this.getJobId();
		Date nextFireTime = context.getNextFireTime();
		Date prevFireTime = context.getScheduledFireTime();
		if(log.isTraceEnabled()) log.trace("next Fire Time="+nextFireTime+" scheduled fire time ="+ prevFireTime);
		Calendar cal = Calendar.getInstance();
		cal.setTime(nextFireTime);
		long nextInMillis=cal.getTimeInMillis();
		cal.setTime(prevFireTime);
		long prevInMillis = cal.getTimeInMillis();
		int expiredInseconds = Long.valueOf((nextInMillis - prevInMillis)/1000).intValue();
		if(log.isTraceEnabled()) log.trace("EXPIRED IN SEC="+expiredInseconds);
		
		boolean locked = lockFacade.lockForCronJob(jobId, jobName, expiredInseconds);
		if(locked)
		{
			try
			{
				executeCron(context);	
			}finally
			{
				lockFacade.releaseCronJobLock(jobId, jobName);
			}
		}
		
	}
		
	public SocialBettingTask getTask() {
		return task;
	}

	public void setTask(SocialBettingTask task) {
		this.task = task;
	}


	public DBLockFacade getLockFacade() {
		return lockFacade;
	}



	public void setLockFacade(DBLockFacade lockFacade) {
		this.lockFacade = lockFacade;
	}


	public abstract void executeCron(JobExecutionContext ctx) throws JobExecutionException ;
	public abstract String getJobName();
	public abstract long getJobId();
}
