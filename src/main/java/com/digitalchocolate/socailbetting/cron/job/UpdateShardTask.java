package com.digitalchocolate.socailbetting.cron.job;

import com.digitalchocolate.socailbetting.cron.SocialBettingTask;

public interface UpdateShardTask extends SocialBettingTask{
	public boolean updateShard(Integer begin, Integer pageSize, Integer totalBucks);
}
