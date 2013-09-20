package com.digitalchocolate.socailbetting.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class Bet implements Serializable{

	private static final long serialVersionUID = -996103221095604890L;
	@Id	
	private String id;
	
	private String eventId;
	private String opponentId;
	private String targetId;
	private Double amount;
	private String currency;
	private Date createdDate = new Date();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
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
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
}
