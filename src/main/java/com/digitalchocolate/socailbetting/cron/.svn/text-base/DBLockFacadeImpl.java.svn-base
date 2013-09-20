package com.digitalchocolate.socailbetting.cron;

import org.apache.log4j.Logger;

import com.digitalchocolate.socailbetting.dao.BaseRuleDao;

public class DBLockFacadeImpl implements DBLockFacade {
	private static final Logger log = Logger.getLogger(DBLockFacadeImpl.class);
	private BaseRuleDao ruleDao;

	@Override
	public boolean releaseCronJobLock(Long jobId, String jobName) {
		boolean released = false;
		int counter = 0;
		if(log.isTraceEnabled())log.trace("Before releasing the lock.............");
		while(!released && counter < 300)
		{
			released=ruleDao.release(jobId, jobName);
			if(!released)
			{
				if(log.isDebugEnabled())
				log.debug("failed to release the lock will try again after 1 second......");
			}
			try
			{
				Thread.sleep(10);
			}catch(InterruptedException e)
			{
				if (log.isTraceEnabled()) log.trace("Interrupted!", e);
			}
			counter++;
		}
		if(!released)
		{
			log.error("###############failed to release the lock for job: "+jobName);
		}
		return released;
	}
	@Override
	public boolean releaseCronJobLock(String jobName) {
		boolean released = false;
		int counter = 0;
		if(log.isTraceEnabled())log.trace("Before releasing the lock.............for job"+jobName);
		while(!released && counter < 300)
		{
			released=ruleDao.release(jobName);
			if(!released)
			{
				if(log.isDebugEnabled())
				log.debug("failed to release the lock will try again after 1 second......");
			}
			try
			{
				Thread.sleep(10);
			}catch(InterruptedException e)
			{
				if (log.isTraceEnabled()) log.trace("Interrupted!", e);
			}
			counter++;
		}
		if(!released)
		{
			log.error("###############failed to release the lock for job: "+jobName);
		}
		return released;
	}


	@Override
	public boolean lockForCronJob(Long jobId, String jobName, int expiredInSeconds) {
		boolean isLock = false;
		int lockCount = 0;
		while(!isLock && lockCount < 10)
		{
			if(log.isTraceEnabled()) log.trace("getting the lock.........."+lockCount);
			try
			{
				isLock=ruleDao.lock(jobId, jobName, expiredInSeconds);
			}catch(Exception e)
			{
				if(log.isDebugEnabled())log.debug("failed to acquired the lock......", e);
			}
			try
			{
				Thread.sleep(10);
			}catch(InterruptedException e)
			{
				if (log.isTraceEnabled()) log.trace("Interrupted!", e);
			}
			lockCount++;
		}
		return isLock;
	}

	public BaseRuleDao getRuleDao() {
		return ruleDao;
	}

	public void setRuleDao(BaseRuleDao ruleDao) {
		this.ruleDao = ruleDao;
	}

}
