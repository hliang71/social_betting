package com.openbet.socailbetting.model.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.openbet.socailbetting.utils.CurrencyEnum;

@Document
public class OddsRule extends BaseRule implements Serializable{
	
	private static final long serialVersionUID = -8327916598634570466L;
	@Transient
	public static final List<String> createBetOrders = new ArrayList<String>();	
	@Transient
	public static final List<String> placeBetOrders= new ArrayList<String>();//{"takeOutPercentage", "minBetAmt", "maxBetAmt"};
	@Transient
	public static final List<String> searchBetOrders= new ArrayList<String>();//{"takeOutPercentage"};
    static
    {
    	createBetOrders.add("minBetAmt");
    	createBetOrders.add("maxBetAmt");
    	
    	placeBetOrders.add("takeOutPercentage");
    	placeBetOrders.add("minBetAmt");
    	placeBetOrders.add("maxBetAmt");
    	
    	searchBetOrders.add("takeOutPercentage");
    	searchBetOrders.add("minBetAmt");
    	searchBetOrders.add("maxBetAmt");
    }

	@Id	
	private String id;
	
	private Double takeOutPercentage;// calculate all targets ' true odds
	private CurrencyEnum currency;
	private Double suggestedBetAmt;
	private Double minBetAmt;
	private Double maxBetAmt;
	private Double maxRisk;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Double getTakeOutPercentage() {
		return takeOutPercentage;
	}
	public void setTakeOutPercentage(Double takeOutPercentage) {
		this.takeOutPercentage = takeOutPercentage;
	}
	public CurrencyEnum getCurrency() {
		return currency;
	}
	public void setCurrency(CurrencyEnum currency) {
		this.currency = currency;
	}
	public Double getSuggestedBetAmt() {
		return suggestedBetAmt;
	}
	public void setSuggestedBetAmt(Double suggestedBetAmt) {
		this.suggestedBetAmt = suggestedBetAmt;
	}
	public Double getMinBetAmt() {
		return minBetAmt;
	}
	public void setMinBetAmt(Double minBetAmt) {
		this.minBetAmt = minBetAmt;
	}
	public Double getMaxBetAmt() {
		return maxBetAmt;
	}
	public void setMaxBetAmt(Double maxBetAmt) {
		this.maxBetAmt = maxBetAmt;
	}
	public Double getMaxRisk() {
		return maxRisk;
	}
	public void setMaxRisk(Double maxRisk) {
		this.maxRisk = maxRisk;
	}
	public List<String> getPlaceBetOrders() {
		return placeBetOrders;
	}
	public List<String> getSearchBetOrders() {
		return searchBetOrders;
	}
	
	public List<String> getCreateBetOrders() {
		return createBetOrders;
	}
	
	
}
