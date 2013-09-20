package com.digitalchocolate.socailbetting.webapp;

import org.apache.commons.lang.StringUtils;

public class UpdateBetRequestBean extends BaseRequestDTO{
	private String betId;
	private String gameResult;
	
	public String getBetId() {
		return betId;
	}
	public void setBetId(String betId) {
		this.betId = betId;
	}
	public String getGameResult() {
		return gameResult;
	}
	public void setGameResult(String gameResult) {
		this.gameResult = gameResult;
	}
	@Override
	public String getRequestContent() {
		String bId = StringUtils.isBlank(betId)? "" : betId;
		String gr = StringUtils.isBlank(gameResult)? "" : gameResult;
		return new StringBuilder(bId).append(gr).append(super.getOpponentId()).append(super.getPlatformUserId()).append(super.getProjectId()).toString();
	}
	
}
