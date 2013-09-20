package com.openbet.socailbetting.webapp;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.openbet.socailbetting.utils.BetTypeEnum;

public class GenerateBetRequestBean extends BaseRequestDTO {
	private String contextIdentifier;
	private Boolean pickTargets;
	private Integer playerLevel;
	private BetTypeEnum betType;
	private Double amount;
	private List<Integer> selectedTiers;
	private List<String> targets;
	private String currency;
	private Boolean isRelative;
	
	public Boolean getPickTargets() {
		return pickTargets;
	}
	public void setPickTargets(Boolean pickTargets) {
		this.pickTargets = pickTargets;
	}
	public Integer getPlayerLevel() {
		return playerLevel;
	}
	public void setPlayerLevel(Integer playerLevel) {
		this.playerLevel = playerLevel;
	}
	public BetTypeEnum getBetType() {
		return betType;
	}
	public void setBetType(BetTypeEnum betType) {
		this.betType = betType;
	}
	
	
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public List<Integer> getSelectedTiers() {
		return selectedTiers;
	}
	public void setSelectedTiers(List<Integer> selectedTiers) {
		this.selectedTiers = selectedTiers;
	}
	
	public List<String> getTargets() {
		return targets;
	}
	public void setTargets(List<String> targets) {
		this.targets = targets;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getContextIdentifier() {
		return contextIdentifier;
	}
	public void setContextIdentifier(String contextIdentifier) {
		this.contextIdentifier = contextIdentifier;
	}
	
	public Boolean getIsRelative() {
		return isRelative;
	}
	public void setIsRelative(Boolean isRelative) {
		this.isRelative = isRelative;
	}
	@Override
	public String getRequestContent() {
		NumberFormat formatter = new DecimalFormat("#0.00"); 		 
		String formatedAmt = formatter.format(amount);
	    String isRel = (this.isRelative == null)? "" : this.isRelative.toString();
		String amt = amount == null? "" : formatedAmt;
		String bType = betType == null? "" : betType.value();
		String contextId = StringUtils.isBlank(this.contextIdentifier)? "" : this.contextIdentifier;
		String cy = StringUtils.isBlank(currency)? "": currency;
		String pt = pickTargets == null? "": pickTargets+"";
		String pl = playerLevel == null? "" : playerLevel+"";
		return new StringBuilder(amt).append(bType).append(contextId).append(cy).append(isRel).append(this.getOpponentId()).append(pt).append(this.getPlatformUserId()).append(pl).append(this.getProjectId()).toString();
	}
	
}
