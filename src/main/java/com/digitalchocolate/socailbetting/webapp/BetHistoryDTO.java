package com.digitalchocolate.socailbetting.webapp;

import java.util.Date;
import java.util.List;

import com.digitalchocolate.socailbetting.utils.BetHistoryStatusEnum;
import com.digitalchocolate.socailbetting.utils.BetTypeEnum;

public class BetHistoryDTO {
	private BetTypeEnum betType; 
	private Integer playerTier;   
	private List<OpponentTierDTO> 	opponentUserTiers; 
	private Double	betAmt;
	private String currency;
	private	Double	Odds;
	private Double  payOut; 
	private Double takeOutPercentage; 
	private boolean isWinner; 
	private boolean isTie;
	private String completionResult; 
	private Date betPlaceTime;  
	private Date betMatchedTime;
	private Date betSettleTime;
	private String betId;
	private String playerId;
	private Double trueOdds;
	private Long projectId;
	private String contextIdentifier;
	private String status;
	
	public BetTypeEnum getBetType() {
		return betType;
	}
	public void setBetType(BetTypeEnum betType) {
		this.betType = betType;
	}
	
	public Integer getPlayerTier() {
		return playerTier;
	}
	public void setPlayerTier(Integer playerTier) {
		this.playerTier = playerTier;
	}
	public List<OpponentTierDTO> getOpponentUserTiers() {
		return opponentUserTiers;
	}
	public void setOpponentUserTiers(List<OpponentTierDTO> opponentUserTiers) {
		this.opponentUserTiers = opponentUserTiers;
	}
	public Double getBetAmt() {
		return betAmt;
	}
	public void setBetAmt(Double betAmt) {
		this.betAmt = betAmt;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Double getOdds() {
		return Odds;
	}
	public void setOdds(Double odds) {
		Odds = odds;
	}
	public Double getPayOut() {
		return payOut;
	}
	public void setPayOut(Double payOut) {
		this.payOut = payOut;
	}
	public Double getTakeOutPercentage() {
		return takeOutPercentage;
	}
	public void setTakeOutPercentage(Double takeOutPercentage) {
		this.takeOutPercentage = takeOutPercentage;
	}
	public boolean isWinner() {
		return isWinner;
	}
	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}
	public String getCompletionResult() {
		return completionResult;
	}
	public void setCompletionResult(String completionResult) {
		this.completionResult = completionResult;
	}
	public Date getBetPlaceTime() {
		return betPlaceTime;
	}
	public void setBetPlaceTime(Date betPlaceTime) {
		this.betPlaceTime = betPlaceTime;
	}
	public Date getBetMatchedTime() {
		return betMatchedTime;
	}
	public void setBetMatchedTime(Date betMatchedTime) {
		this.betMatchedTime = betMatchedTime;
	}
	public Date getBetSettleTime() {
		return betSettleTime;
	}
	public void setBetSettleTime(Date betSettleTime) {
		this.betSettleTime = betSettleTime;
	}
	
	public String getBetId() {
		return betId;
	}
	public void setBetId(String betId) {
		this.betId = betId;
	}
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public Double getTrueOdds() {
		return trueOdds;
	}
	public void setTrueOdds(Double trueOdds) {
		this.trueOdds = trueOdds;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public boolean isTie() {
		return isTie;
	}
	public void setTie(boolean isTie) {
		this.isTie = isTie;
	}
	public String getContextIdentifier() {
		return contextIdentifier;
	}
	public void setContextIdentifier(String contextIdentifier) {
		this.contextIdentifier = contextIdentifier;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
