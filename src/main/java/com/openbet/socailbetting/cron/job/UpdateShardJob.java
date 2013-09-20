package com.openbet.socailbetting.cron.job;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.openbet.socailbetting.cron.BaseBetNotificationJob;


public class UpdateShardJob extends BaseBetNotificationJob{
	private static final Logger log = Logger.getLogger(UpdateShardJob.class);
	public static final String JOB_NAME="update_bucket";
	public static final long JOB_ID=Integer.valueOf(JOB_NAME.hashCode()).longValue();
	public static final Integer PAGE_SIZE = 2;
	private Integer totalBucket;
	
	@Override
	public void executeCron(JobExecutionContext ctx)
			throws JobExecutionException {
		if(log.isDebugEnabled()) log.debug("IN UpdateShardJob Cron :.....");
		UpdateShardTask task = (UpdateShardTask)this.getTask();
		int begin = 0;
		boolean hasMore = true;
		while(hasMore)
		{
			hasMore = task.updateShard(begin, PAGE_SIZE, totalBucket);
			begin += PAGE_SIZE;
			if(log.isDebugEnabled()) log.debug("has more :..... begin = "+begin);
		}	
	}
	@Override
	public String getJobName() {
		return JOB_NAME;
	}
	@Override
	public long getJobId() {
		return JOB_ID;
	}
	public Integer getTotalBucket() {
		return totalBucket;
	}
	public void setTotalBucket(Integer totalBucket) {
		this.totalBucket = totalBucket;
	}
	
}
