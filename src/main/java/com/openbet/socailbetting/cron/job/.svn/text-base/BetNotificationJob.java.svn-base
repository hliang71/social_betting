package com.openbet.socailbetting.cron.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.openbet.socailbetting.cron.BaseBetNotificationJob;
import com.openbet.socailbetting.cron.BaseNotificationTask;

public class BetNotificationJob extends BaseBetNotificationJob{
	private static final Logger log = Logger.getLogger(BetNotificationJob.class);
	public static final String JOB_NAME="socialBetting_notification";
	public static final long JOB_ID=Integer.valueOf(JOB_NAME.hashCode()).longValue();
	private static final int MAX_POOL_SIZE=600;
	private Integer batchSize;
	private Integer shardNumber;
	private Integer threadPoolSize;
	
	private static final ExecutorService pool = Executors.newFixedThreadPool(MAX_POOL_SIZE);;

	@Override
	public void executeCron(JobExecutionContext ctx) throws JobExecutionException {
		if(log.isDebugEnabled()) log.debug("IN executeCron :.....");
		BaseNotificationTask task = (BaseNotificationTask)this.getTask();
		List<Future> list = new ArrayList<Future>();
		for(int i = 0; i < threadPoolSize; i++)
		{
			
			try
			{
				int begin = i* batchSize;
				Future f = pool.submit(new WorkerThread(task, batchSize
					,shardNumber, begin));
				list.add(f);
			}catch(Exception e)
			{
				log.error("failed to exe the worker thread.", e);
			}

		}
		for(Future f : list)
        {
        	try
        	{
        		f.get();
        		if(!f.isDone())
        		{
        			log.error("task execution failed.");
        		}
        	}catch(Exception e)
        	{
        		log.info("processing expired failed."+e);
        	}
        }
		/**try
		{
			task.sendBetNotification(this.getBatchSize(), shardNumber);
		}catch(Exception e)
		{
			log.error("run notification task failed:", e);
		}*/
	}

	@Override
	public String getJobName() {
		
		return JOB_NAME;
	}

	@Override
	public long getJobId() {
		int sNum = shardNumber == null? 0 : shardNumber;
		return JOB_ID+sNum;
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public Integer getShardNumber() {
		return shardNumber;
	}

	public void setShardNumber(Integer shardNumber) {
		this.shardNumber = shardNumber;
	}

	public Integer getThreadPoolSize() {
		return threadPoolSize;
	}

	public void setThreadPoolSize(Integer threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
		

	}

	public ExecutorService getPool() {
		return pool;
	}
	
	public class WorkerThread implements Callable
	{

        private BaseNotificationTask task;
        private Integer begin;
        private Integer batchSize;
        private Integer shardNum;
        
        public WorkerThread (BaseNotificationTask task, Integer batchSize, Integer shardNum, Integer begin) {
            this.task = task;
            this.batchSize = batchSize;
            this.shardNum = shardNum;
            this.begin = begin;
        }
        
       

		@Override
		public Object call() throws Exception {
			try
    		{
    			task.sendBetNotification(batchSize, shardNumber, begin);
    		}catch(Exception e)
    		{
    			log.error("run notification task failed:", e);
    		}
			return null;
		}
	}
}
