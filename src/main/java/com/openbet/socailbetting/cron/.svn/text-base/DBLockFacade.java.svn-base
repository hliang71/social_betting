package com.openbet.socailbetting.cron;

public interface DBLockFacade {
	
	public boolean releaseCronJobLock(Long jobId, String jobName);
	
	public boolean lockForCronJob(Long jobId, String jobName, int expiredInSeconds);
	
	public boolean releaseCronJobLock(String jobName);
	
}
