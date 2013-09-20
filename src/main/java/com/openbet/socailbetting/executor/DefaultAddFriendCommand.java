package com.openbet.socailbetting.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.openbet.socailbetting.exception.RuleEngineException;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.model.Friend;
import com.openbet.socailbetting.utils.ErrCode;

public class DefaultAddFriendCommand extends BaseRuleExecutor{
	private static final Logger log = Logger.getLogger(DefaultAddFriendCommand.class);
    private static final String NEIGHBORS = "targets";
    private static final String OPPONENT_ID = "opponentId";
    private static final String FRIEND_ENABLE="pickTargets";//this rule only apply at create.
	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		log.info("IN DEFAULT ADD FRIEND COMMAND#######################");
		Boolean opponentIsFriend = (Boolean)this.getValue() == null? false : (Boolean)this.getValue();
		
		Map<String, Object> param = (Map<String, Object>)parameters;
		String opponentId = (String)param.get(OPPONENT_ID);
		boolean friendEnable = (Boolean)param.get(FRIEND_ENABLE);
		if(StringUtils.isBlank(opponentId))
		{
			throw new RuleEngineException(ErrCode.MISSING_OPPONENT_ID, "failed to add friends, miss opponent id");
		}
		if(friendEnable && !opponentIsFriend)
		{
			throw new RuleEngineException(ErrCode.BET_WITH_SPECIFIC_TARGETS_DISALLOWED, "bet with targets disallowed.");
		}
		if(opponentIsFriend && friendEnable)
		{
			List<String> friendIds = (List<String>)param.get(NEIGHBORS);
			if(friendIds == null || friendIds.isEmpty())
			{
				throw new RuleEngineException(ErrCode.IDS_REQUIRED_TO_PICK_TARGETS, "targets are required when pick targets enabled.");
			}
			if(friendIds != null && !friendIds.isEmpty())
			{
				List<Friend> friends = event.getFriends();
				if(friends == null)
				{
					friends = new ArrayList<Friend>();
					event.setFriends(friends);
				}
				for(String friendId : friendIds)
				{
					Friend  f = new Friend();
					f.setOpponentId(opponentId);
					f.setFriendId(friendId);
					friends.add(f);
				}
			}
		}
		return event;
	}
}
