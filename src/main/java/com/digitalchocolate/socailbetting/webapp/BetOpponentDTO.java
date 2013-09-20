package com.digitalchocolate.socailbetting.webapp;

public class BetOpponentDTO {
	private Boolean isWinner;
	private String id;
	private Double betAmt;
	private String currency;
	private Double odds;
	private Integer tier;
	private String betId;
	private Double payOut;
	private String contextIdentifier;
	
	public Boolean getIsWinner() {
		return isWinner;
	}
	public void setIsWinner(Boolean isWinner) {
		this.isWinner = isWinner;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
		return odds;
	}
	public void setOdds(Double odds) {
		this.odds = odds;
	}
	
	public Integer getTier() {
		return tier;
	}
	public void setTier(Integer tier) {
		this.tier = tier;
	}
	
	public String getBetId() {
		return betId;
	}
	public void setBetId(String betId) {
		this.betId = betId;
	}
	public Double getPayOut() {
		return payOut;
	}
	public void setPayOut(Double payOut) {
		this.payOut = payOut;
	}
	public String getContextIdentifier() {
		return contextIdentifier;
	}
	public void setContextIdentifier(String contextIdentifier) {
		this.contextIdentifier = contextIdentifier;
	}
}
