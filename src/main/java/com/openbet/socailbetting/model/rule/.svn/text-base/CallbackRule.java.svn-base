package com.openbet.socailbetting.model.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;



@Document
public class CallbackRule extends BaseRule implements Serializable{

	
	private static final long serialVersionUID = -6135746356370177319L;

	@Transient
	public static final List<String> callbackOrders= new ArrayList<String>();
	static
	{
		callbackOrders.add("callbackUrl");
	}
	
	@Id	
	private String id;
	
	private String callbackUrl;
	private String userLevelUrl;  
	private Integer buckNumber;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCallbackUrl() {
		return callbackUrl;
	}
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	public List<String> getCallbackOrders() {
		return callbackOrders;
	}
	public String getUserLevelUrl() {
		return userLevelUrl;
	}
	public void setUserLevelUrl(String userLevelUrl) {
		this.userLevelUrl = userLevelUrl;
	}
	public Integer getBuckNumber() {
		return buckNumber;
	}
	public void setBuckNumber(Integer buckNumber) {
		this.buckNumber = buckNumber;
	}
	
}
