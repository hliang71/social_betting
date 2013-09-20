package com.digitalchocolate.socailbetting.webapp;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;

public class PlaceBetRequestBean extends BaseRequestDTO{
	private String betId;
	private String targetId;
	private Double amount;
	private Integer playerLevel;
	private String currency;
	
	public String getBetId() {
		return betId;
	}
	public void setBetId(String betId) {
		this.betId = betId;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	
	public Integer getPlayerLevel() {
		return playerLevel;
	}
	public void setPlayerLevel(Integer playerLevel) {
		this.playerLevel = playerLevel;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	@Override
	public String getRequestContent() {
		NumberFormat formatter = new DecimalFormat("#0.00"); 
		 
		String formatedAmt = formatter.format(amount);
		String amt = amount == null? "" : formatedAmt;
		String cy = StringUtils.isBlank(currency)? "" : currency;
		String bId = StringUtils.isBlank(betId)? "" : betId;
		String pl = (playerLevel == null) ? "" : playerLevel+"";
		String tId = StringUtils.isBlank(targetId)? "" : targetId;
		return new StringBuilder(amt).append(bId).append(cy).append(super.getOpponentId()).append(super.getPlatformUserId()).append(pl).append(super.getProjectId()).append(tId).toString();
	}
	
}
