package com.openbet.socailbetting.cron.job;

import com.openbet.socailbetting.cron.SocialBettingTask;

public interface ProcessDLQTask extends SocialBettingTask {
	public void redeliverDLQ(final String from, final String to);
}
