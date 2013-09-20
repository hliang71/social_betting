package com.digitalchocolate.socailbetting.executor;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.digitalchocolate.socailbetting.exception.RuleEngineException;
import com.digitalchocolate.socailbetting.model.BettingEvent;
import com.digitalchocolate.socailbetting.utils.ErrCode;

public class PlayerLevelEqualValidator  extends BaseRuleExecutor{
	private static final Logger log = Logger.getLogger(PlayerLevelEqualValidator.class);
    private static final String SELECTED_LEVEL="selectedTiers";
    private static final String IS_RELATIVE="isRelative";
    private static final String PLAYER_LEVEL="playerLevel";
	@SuppressWarnings("unchecked")
	@Override
	public BettingEvent execute(BettingEvent event, Object parameters) {
		
		Map<String, Object> param = (Map<String, Object>) parameters;
		List<Integer> selectedLevels =  (List<Integer>) param.get(SELECTED_LEVEL);
		Integer playerLevel = (Integer) param.get(PLAYER_LEVEL);
		Boolean isRelative = (param.get(IS_RELATIVE) == null) ? false : (Boolean)param.get(IS_RELATIVE);
		for(Integer selectedLevel : selectedLevels)
		{
			if(selectedLevel != null)
			{
				if(isRelative)
				{
					if(selectedLevel == 0)
					{
						throw new RuleEngineException(ErrCode.SELECTED_LEVEL_NOT_ALLOWED, "the rule disallow same tier bet, the relative tier number must not be zero.");

					}
				}else
				{
					if(selectedLevel == playerLevel)
					{
						throw new RuleEngineException(ErrCode.SELECTED_LEVEL_NOT_ALLOWED, "the rule disallow same tier bet. selected tiers must not equals to player's tier.");
					}
				}
				
			}	
		}
		return event;
	}

}
