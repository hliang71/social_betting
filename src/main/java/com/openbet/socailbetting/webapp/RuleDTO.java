package com.openbet.socailbetting.webapp;

import java.util.List;

import com.openbet.socailbetting.utils.CurrencyEnum;
import com.openbet.socailbetting.utils.LevelEnum;

public class RuleDTO {
	private Double suggestedBetAmt;
	private Double maxBetAmt;
	private Double minBetAmt;
	private List<LevelEnum> allowedRelativeLevels;
	private Boolean userCanTargetOpponents;
	private Integer maxAllowedCompletionTime;
	private CurrencyEnum currency;
	private Boolean eligibleForBet;
	private Integer maxTimeFindOpponent;
	private Integer playerTier;
	public Double getSuggestedBetAmt() {
		return suggestedBetAmt;
	}
	public void setSuggestedBetAmt(Double suggestedBetAmt) {
		this.suggestedBetAmt = suggestedBetAmt;
	}
	public Double getMaxBetAmt() {
		return maxBetAmt;
	}
	public void setMaxBetAmt(Double maxBetAmt) {
		this.maxBetAmt = maxBetAmt;
	}
	public Double getMinBetAmt() {
		return minBetAmt;
	}
	public void setMinBetAmt(Double minBetAmt) {
		this.minBetAmt = minBetAmt;
	}
	
	public List<LevelEnum> getAllowedRelativeLevels() {
		return allowedRelativeLevels;
	}
	public void setAllowedRelativeLevels(List<LevelEnum> allowedRelativeLevels) {
		this.allowedRelativeLevels = allowedRelativeLevels;
	}
	
	public Boolean getUserCanTargetOpponents() {
		return userCanTargetOpponents;
	}
	public void setUserCanTargetOpponents(Boolean userCanTargetOpponents) {
		this.userCanTargetOpponents = userCanTargetOpponents;
	}
	public void setOpponentIsFriend(Boolean opponentIsFriend) {
		this.userCanTargetOpponents = opponentIsFriend;
	}
	public Integer getMaxAllowedCompletionTime() {
		return maxAllowedCompletionTime;
	}
	public void setMaxAllowedCompletionTime(Integer maxAllowedCompletionTime) {
		this.maxAllowedCompletionTime = maxAllowedCompletionTime;
	}
	
	public CurrencyEnum getCurrency() {
		return currency;
	}
	public void setCurrency(CurrencyEnum currency) {
		this.currency = currency;
	}
	
	public Boolean getEligibleForBet() {
		return eligibleForBet;
	}
	public void setEligibleForBet(Boolean eligibleForBet) {
		this.eligibleForBet = eligibleForBet;
	}
	
	public Integer getMaxTimeFindOpponent() {
		return maxTimeFindOpponent;
	}
	public void setMaxTimeFindOpponent(Integer maxTimeFindOpponent) {
		this.maxTimeFindOpponent = maxTimeFindOpponent;
	}
	public Integer getPlayerTier() {
		return playerTier;
	}
	public void setPlayerTier(Integer playerTier) {
		this.playerTier = playerTier;
	}
}
