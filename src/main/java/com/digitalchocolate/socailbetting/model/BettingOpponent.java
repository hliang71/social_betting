package com.digitalchocolate.socailbetting.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;



public class BettingOpponent  implements Serializable{
	
	private static final long serialVersionUID = 8047006911634556172L;

	@Id
	private String id;// system id leave it blank so system will generate it automatically 
	
	private String opponentId;
	private String platformUserId;
	private String targetId; //target fb id not a system id. required to update the target's odds
	private Integer position; // tier number
	private Double betAmt;
	private String betId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOpponentId() {
		return opponentId;
	}
	public void setOpponentId(String opponentId) {
		this.opponentId = opponentId;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public Double getBetAmt() {
		return betAmt;
	}
	public void setBetAmt(Double betAmt) {
		this.betAmt = betAmt;
	}
	public String getBetId() {
		return betId;
	}
	public void setBetId(String betId) {
		this.betId = betId;
	}
	public String getPlatformUserId() {
		return platformUserId;
	}
	public void setPlatformUserId(String platformUserId) {
		this.platformUserId = platformUserId;
	}
}
