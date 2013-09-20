package com.digitalchocolate.socailbetting.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.digitalchocolate.socailbetting.exception.RuleEngineException;
import com.digitalchocolate.socailbetting.exception.SocialBettingInternalException;
import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.utils.ErrCode;
import com.digitalchocolate.socailbetting.utils.LevelEnum;

public class DefaultSetExpectedLevelCommand extends BaseRuleExecutor {
	private static final Logger log = Logger.getLogger(DefaultSetExpectedLevelCommand.class);
    private static final String SELECTED_LEVEL="selectedTiers";
    private static final String IS_RELATIVE="isRelative";
    private static final String PLAYER_LEVEL="playerLevel";
    private static final String FRIEND_ENABLE="pickTargets";//only apply to create.
    private static final String PREFIX="OPPONENT_RULE_ALLOWED_RELATIVE_TIERS";
	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		log.info("IN SET EXPECT LEVEL ####################");
		List<LevelEnum> allowedLevels =(List<LevelEnum>) this.getValue();
		LevelEnum[] levels = LevelEnum.values();
		List<LevelEnum> allLevels = new ArrayList(Arrays.asList(levels));
		for(LevelEnum allowedLevel : allowedLevels)
		{
			allLevels.remove(allowedLevel);
		}
		for(LevelEnum disallowedLevel : allLevels)
		{
			String commandName = new StringBuilder(PREFIX).append("_").append(disallowedLevel.value()).toString();
			commandName = this.getText(commandName);		
			ApplicationContext ctx = this.getCtx();
			BaseRuleExecutor command = ctx.getBean(commandName, BaseRuleExecutor.class);
			event = command.execute(event, parameters);
		}
		
		Map<String, Object> param = (Map<String, Object>) parameters;
		List<Integer> selectedLevels =  (List<Integer>) param.get(SELECTED_LEVEL);
		Boolean friendEnable = (Boolean) param.get(FRIEND_ENABLE);
		Integer playerLevel = (Integer) param.get(PLAYER_LEVEL);
		Boolean isRelative = (param.get(IS_RELATIVE) == null) ? false : (Boolean)param.get(IS_RELATIVE);
		
		if(selectedLevels == null || selectedLevels.isEmpty())
		{
			if(friendEnable == null || !friendEnable)
			{
				throw new RuleEngineException(ErrCode.MISSING_PLAYER_SELECTED_LEVELS, "selectedLevel is required when pickTargets is not intented.");
			}
		}
		
		if(playerLevel == null)
		{
			throw new SocialBettingInternalException("failed to set expected level of the event, missing player tier number");	
		}
		event.setLevel(playerLevel);
		List<Integer> expectedLevels = event.getExpectedLevels();
		if(expectedLevels == null)
		{
			expectedLevels = new ArrayList<Integer>();
			event.setExpectedLevels(expectedLevels);
		}
		if(friendEnable == null || !friendEnable)
		{
			for(Integer level : selectedLevels)
			{
				if(log.isDebugEnabled()) log.debug("selected level is "+level);
				if(level == null)
				{
					throw new RuleEngineException(ErrCode.MISSING_PLAYER_SELECTED_LEVELS, "selectedLevel must be number.");
				}
				if(isRelative)
				{
					Integer trueLevel = playerLevel + level;
					if(trueLevel < 1)
					{
						throw new RuleEngineException(ErrCode.BAD_RELATIVE_TIERS, "bad relative tier number, the true tier is less than one after applying the number.");
					}
					expectedLevels.add(trueLevel);
				}else
				{
					if(level < 1)
					{
						throw new RuleEngineException(ErrCode.BAD_TIERS_NUMBER, "tiers number is less than one.");
					}
					expectedLevels.add(level);
				}
			}
		}
		return event;
	}

}
