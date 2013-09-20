package com.openbet.socailbetting.webapp;

import com.openbet.socailbetting.utils.BetTypeEnum;

public class RuleRequestBean extends BaseRequestDTO{
	private BetTypeEnum betType;
	
	public BetTypeEnum getBetType() {
		return betType;
	}
	public void setBetType(BetTypeEnum betType) {
		this.betType = betType;
	}
	@Override
	public String getRequestContent() {
		String bType = (betType == null)? "" : betType.value();
		return new StringBuilder(bType).append(super.getOpponentId()).append(super.getPlatformUserId()).append(super.getProjectId()).toString();
	}
	
}
