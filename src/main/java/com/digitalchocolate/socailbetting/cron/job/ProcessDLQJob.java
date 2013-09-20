package com.digitalchocolate.socailbetting.cron.job;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.digitalchocolate.socailbetting.cron.BaseBetNotificationJob;

public class ProcessDLQJob  extends BaseBetNotificationJob{
	private static final Logger log = Logger.getLogger(BetNotificationJob.class);
	public static final String JOB_NAME="process_dlq";
	public static final long JOB_ID=Integer.valueOf(JOB_NAME.hashCode()).longValue();
	private String historyQueue;
	private String historyDLQ;
	private String callbackQueue;
	private String callbackDLQ;
	
	
	@Override
	public void executeCron(JobExecutionContext ctx)
			throws JobExecutionException {
		ProcessDLQTask task = (ProcessDLQTask) this.getTask();
		task.redeliverDLQ(historyDLQ, historyQueue);
		task.redeliverDLQ(callbackDLQ, callbackQueue);
		
	}
	@Override
	public String getJobName() {
		return JOB_NAME;
	}
	@Override
	public long getJobId() {
		return JOB_ID;
	}
	public String getHistoryQueue() {
		return historyQueue;
	}
	public void setHistoryQueue(String historyQueue) {
		this.historyQueue = historyQueue;
	}
	public String getHistoryDLQ() {
		return historyDLQ;
	}
	public void setHistoryDLQ(String historyDLQ) {
		this.historyDLQ = historyDLQ;
	}
	public String getCallbackQueue() {
		return callbackQueue;
	}
	public void setCallbackQueue(String callbackQueue) {
		this.callbackQueue = callbackQueue;
	}
	public String getCallbackDLQ() {
		return callbackDLQ;
	}
	public void setCallbackDLQ(String callbackDLQ) {
		this.callbackDLQ = callbackDLQ;
	}
}
