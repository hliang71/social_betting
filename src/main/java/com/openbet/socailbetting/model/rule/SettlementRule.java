package com.openbet.socailbetting.model.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.openbet.socailbetting.utils.CompletionRuleEnum;
import com.openbet.socailbetting.utils.StartCompletionEvaluateEnum;

@Document
public class SettlementRule extends BaseRule implements Serializable{
	
	private static final long serialVersionUID = -7735481972881036184L;
	@Transient
	public static final List<String> createBetOrders=new ArrayList<String>();//{"maxAllowedCompletionTime"};
	@Transient
	public static final List<String> placeBetOrders= new ArrayList<String>(); //{"maxAllowedCompletionTime"};
	@Transient
	public static final List<String> updateBetOrders= new ArrayList<String>(); //{"startComparisonRule","settlementRule" };
	@Transient
	public static final List<String> completionExpiredOrders= new ArrayList<String>();
	static
	{
		createBetOrders.add("maxAllowedCompletionTime");
		placeBetOrders.add("maxAllowedCompletionTime");
		updateBetOrders.add("startComparisonRule");
		updateBetOrders.add("settlementRule");
		completionExpiredOrders.add("startComparisonRule");
		completionExpiredOrders.add("settlementRule");
	}
	@Id	
	private String id;
	private CompletionRuleEnum settlementRule; //subrule to throw exception to stop further evaluation
	private Integer maxAllowedCompletionTime; // seconds need to look at flag to avoid start multiple timer for a single event. throw runtime exception to stop event comparison
	private StartCompletionEvaluateEnum startComparisonRule;// . 
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public StartCompletionEvaluateEnum getEventComparisonRule() {
		return startComparisonRule;
	}
	public void setEventComparisonRule(
			StartCompletionEvaluateEnum eventComparisonRule) {
		this.startComparisonRule = eventComparisonRule;
	}
	
	public CompletionRuleEnum getSettlementRule() {
		return settlementRule;
	}
	public void setSettlementRule(CompletionRuleEnum settlementRule) {
		this.settlementRule = settlementRule;
	}
	public Integer getMaxAllowedCompletionTime() {
		return maxAllowedCompletionTime;
	}
	public void setMaxAllowedCompletionTime(Integer maxAllowedCompletionTime) {
		this.maxAllowedCompletionTime = maxAllowedCompletionTime;
	}
	public StartCompletionEvaluateEnum getStartComparisonRule() {
		return startComparisonRule;
	}
	
	public void setStartComparisonRule(
			StartCompletionEvaluateEnum startComparisonRule) {
		this.startComparisonRule = startComparisonRule;
	}
	public List<String> getCreateBetOrders() {
		return createBetOrders;
	}
	public List<String> getPlaceBetOrders() {
		return placeBetOrders;
	}
	public List<String> getUpdateBetOrders() {
		return updateBetOrders;
	}
	public List<String> getCompletionExpiredOrders() {
		return completionExpiredOrders;
	}
	
}
