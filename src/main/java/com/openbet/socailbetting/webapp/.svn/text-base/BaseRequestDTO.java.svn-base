package com.openbet.socailbetting.webapp;

import org.apache.commons.lang.StringUtils;

import com.openbet.socailbetting.exception.SocialBettingValidationException;
import com.openbet.socailbetting.utils.ErrCode;

public abstract class BaseRequestDTO implements SecurityRequest{
	private String platformUserId;
	private String opponentId;// this is the game user id.
	private Long projectId;
	private String sig;
	
	
	public String getPlatformUserId() {
		return platformUserId;
	}
	public void setPlatformUserId(String platformUserId) {
		this.platformUserId = platformUserId;
	}
	public String getOpponentId() {
		return opponentId;
	}
	public void setOpponentId(String opponentId) {
		this.opponentId = opponentId;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getSig() {
		return sig;
	}
	public void setSig(String sig) {
		this.sig = sig;
	}
	
	public String getSecurityContent()
	{
		if(StringUtils.isBlank(platformUserId))
		{
			throw new SocialBettingValidationException(ErrCode.SECURITY_PLATFORM_USER_ID_REQUIRED, "platform user id is required.");
		}
		if(StringUtils.isBlank(opponentId))
		{
			throw new SocialBettingValidationException(ErrCode.SECURITY_GAME_OPPONENT_USER_ID_REQUIRED, "dgcId is required.");
		}
		if(projectId == null || projectId == 0)
		{
			throw new SocialBettingValidationException(ErrCode.SECURITY_PROJECT_ID_REQUIRED, "project id is required.");
		}
		return new StringBuilder(opponentId).append(platformUserId).append(projectId).toString();
	}
	
}
