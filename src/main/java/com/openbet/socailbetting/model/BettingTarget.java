package com.openbet.socailbetting.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;


public class BettingTarget implements Serializable{
	
	private static final long serialVersionUID = 5004909330846209048L;

	@Id
	private String id;// system id leave it blank so system will generate it automatically 
	
	private String targetId;
	private String gameResult;
	private Double accumulatedAmt;
	private Double seededOdds;
	private Double offeredOdds;// in level table the odds column.
	private Boolean isWinner = false;
	private Double trueOdds;
	private Double truePayout;
	private Double offeredPayout;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public String getGameResult() {
		return gameResult;
	}
	public void setGameResult(String gameResult) {
		this.gameResult = gameResult;
	}
	public Double getAccumulatedAmt() {
		return accumulatedAmt;
	}
	public void setAccumulatedAmt(Double accumulatedAmt) {
		this.accumulatedAmt = accumulatedAmt;
	}
	public Double getSeededOdds() {
		return seededOdds;
	}
	public void setSeededOdds(Double seededOdds) {
		this.seededOdds = seededOdds;
	}
	public Double getOfferedOdds() {
		return offeredOdds;
	}
	public void setOfferedOdds(Double offeredOdds) {
		this.offeredOdds = offeredOdds;
	}
	public Boolean getIsWinner() {
		return isWinner;
	}
	public void setIsWinner(Boolean isWinner) {
		this.isWinner = isWinner;
	}
	public Double getTrueOdds() {
		return trueOdds;
	}
	public void setTrueOdds(Double trueOdds) {
		this.trueOdds = trueOdds;
	}
	public Double getTruePayout() {
		return truePayout;
	}
	public void setTruePayout(Double truePayout) {
		this.truePayout = truePayout;
	}
	public Double getOfferedPayout() {
		return offeredPayout;
	}
	public void setOfferedPayout(Double offeredPayout) {
		this.offeredPayout = offeredPayout;
	}
	
}
