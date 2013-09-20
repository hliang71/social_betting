package com.openbet.socailbetting.cron;

public interface BaseNotificationTask extends SocialBettingTask{
	public void sendBetNotification(Integer batchSize, Integer shardNumber, Integer begin);
}
