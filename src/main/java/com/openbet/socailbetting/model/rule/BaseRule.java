package com.openbet.socailbetting.model.rule;

import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.RuleNamesEnum;

public class BaseRule {
	
	private BetTypeEnum betType;
	private Long projectId;
	private RuleNamesEnum name;
	
	
	public BetTypeEnum getBetType() {
		return betType;
	}
	public void setBetType(BetTypeEnum betType) {
		this.betType = betType;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public RuleNamesEnum getName() {
		return name;
	}
	public void setName(RuleNamesEnum name) {
		this.name = name;
	}
	
	
}
