package com.openbet.socailbetting.model.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.openbet.socailbetting.utils.LevelEnum;


@Document
public class OpponentRule extends BaseRule implements Serializable{

	private static final long serialVersionUID = 7996216416712402796L;
	@Transient
	public static final List<String> createBetOrders = new ArrayList<String>();//{"opponentParticipateBet", "maxTimeFindOpponent", "opponentIsFriend", "allowedRelativeLevels", "requiredNumOpponents"};
	@Transient
	public static final List<String> placeBetOrders=new ArrayList<String>();//{"opponentParticipateBet","requiredNumOpponent"};
	@Transient
	public static final List<String> searchBetOrders= new ArrayList<String>(); //{"opponentParticipateBet", "requiredNumOpponent"};
	static
	{
		createBetOrders.add("opponentParticipateBet");
		createBetOrders.add("maxTimeFindOpponent");
		createBetOrders.add("opponentIsFriend");
		createBetOrders.add("allowedRelativeLevels");
		createBetOrders.add("requiredNumOpponents");
		
		placeBetOrders.add("opponentParticipateBet");
		placeBetOrders.add("requiredNumOpponents");
		searchBetOrders.add("opponentParticipateBet");
		searchBetOrders.add("requiredNumOpponents");
	}
	@Id	
	private String id;
	
	private Integer requiredNumOpponents; // validate opponents number
	private Boolean userAllowedTOSelectOpponentCriteria;
	private List<LevelEnum> allowedRelativeLevels; // set expected level
	private Boolean opponentIsFriend; // addFriends
	private Integer maxTimeFindOpponent; // start opponent timer
	private Boolean opponentParticipateBet; //addOpponent and or targets
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getRequiredNumOpponents() {
		return requiredNumOpponents;
	}
	public void setRequiredNumOpponents(Integer requiredNumOpponents) {
		this.requiredNumOpponents = requiredNumOpponents;
	}
	public Boolean getUserAllowedTOSelectOpponentCriteria() {
		return userAllowedTOSelectOpponentCriteria;
	}
	public void setUserAllowedTOSelectOpponentCriteria(
			Boolean userAllowedTOSelectOpponentCriteria) {
		this.userAllowedTOSelectOpponentCriteria = userAllowedTOSelectOpponentCriteria;
	}
	public List<LevelEnum> getAllowedRelativeLevels() {
		return allowedRelativeLevels;
	}
	public void setAllowedRelativeLevels(List<LevelEnum> allowedRelativeLevels) {
		this.allowedRelativeLevels = allowedRelativeLevels;
	}
	public Boolean getOpponentIsFriend() {
		return opponentIsFriend;
	}
	public void setOpponentIsFriend(Boolean opponentIsFriend) {
		opponentIsFriend = opponentIsFriend;
	}
	public Integer getMaxTimeFindOpponent() {
		return maxTimeFindOpponent;
	}
	public void setMaxTimeFindOpponent(Integer maxTimeFindOpponent) {
		this.maxTimeFindOpponent = maxTimeFindOpponent;
	}
	public Boolean getOpponentParticipateBet() {
		return opponentParticipateBet;
	}
	public void setOpponentParticipateBet(Boolean opponentParticipateBet) {
		this.opponentParticipateBet = opponentParticipateBet;
	}
	public List<String> getCreateBetOrders() {
		return createBetOrders;
	}
	public List<String> getPlaceBetOrders() {
		return placeBetOrders;
	}
	public List<String> getSearchBetOrders() {
		return searchBetOrders;
	}
}
