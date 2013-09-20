package com.digitalchocolate.socailbetting.webapp;

import java.util.List;

import com.digitalchocolate.socailbetting.utils.BetTypeEnum;

public class OpponentsOddsRequest extends BaseRequestDTO{
	private BetTypeEnum betType;
	private List<String> opponentPlayerPlatformIds;

	
	@Override
	public String getRequestContent() {
		String bt = betType == null? "":betType.value();
		return new StringBuilder(bt).append(super.getOpponentId()).append(super.getPlatformUserId()).append(super.getProjectId()).toString();
	}

	public BetTypeEnum getBetType() {
		return betType;
	}

	public void setBetType(BetTypeEnum betType) {
		this.betType = betType;
	}

	public List<String> getOpponentPlayerPlatformIds() {
		return opponentPlayerPlatformIds;
	}

	public void setOpponentPlayerPlatformIds(List<String> opponentPlayerPlatformIds) {
		this.opponentPlayerPlatformIds = opponentPlayerPlatformIds;
	}

}
