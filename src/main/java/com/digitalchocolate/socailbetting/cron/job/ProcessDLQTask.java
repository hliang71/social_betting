package com.digitalchocolate.socailbetting.cron.job;

import com.digitalchocolate.socailbetting.cron.SocialBettingTask;

public interface ProcessDLQTask extends SocialBettingTask {
	public void redeliverDLQ(final String from, final String to);
}
