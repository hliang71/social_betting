package com.openbet.socailbetting.webapp;

import org.apache.commons.lang.StringUtils;

import com.openbet.socailbetting.utils.BetTypeEnum;

public class SearchRequestBean extends BaseRequestDTO{
	public static final Integer BEGIN=0;
	
	private Boolean targetMe;
	private String contextIdentifier;
	private BetTypeEnum betType;
	private Integer limit;
	private Integer playerLevel;
	private Integer begin;
	
	public Boolean getTargetMe() {
		return targetMe;
	}
	public void setTargetMe(Boolean targetMe) {
		this.targetMe = targetMe;
	}
	public BetTypeEnum getBetType() {
		return betType;
	}
	public void setBetType(BetTypeEnum betType) {
		this.betType = betType;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public Integer getPlayerLevel() {
		return playerLevel;
	}
	public void setPlayerLevel(Integer playerLevel) {
		this.playerLevel = playerLevel;
	}	
	public Integer getBegin() {
		return begin;
	}
	public void setBegin(Integer begin) {
		this.begin = begin;
	}
	public String getContextIdentifier() {
		return contextIdentifier;
	}
	public void setContextIdentifier(String contextIdentifier) {
		this.contextIdentifier = contextIdentifier;
	}
	@Override
	public String getRequestContent() {
		// betType, friendEnable, limit, playerLevel
		String beginStr = (this.begin == null)? "" : String.valueOf(this.begin);
		String bType = (betType == null)? "" : betType.value();
		String contextId = StringUtils.isBlank(this.contextIdentifier)? "" : this.contextIdentifier;
		String tm = (targetMe == null) ? "" : targetMe.toString();
		String lm = (limit == null) ? "" : limit.toString();	
		String pl = (playerLevel == null)? "" : playerLevel.toString();		
		return new StringBuilder(beginStr).append(bType).append(contextId).append(lm).append(super.getOpponentId()).append(super.getPlatformUserId()).append(pl).append(super.getProjectId()).append(tm).toString();
	}
	
}
