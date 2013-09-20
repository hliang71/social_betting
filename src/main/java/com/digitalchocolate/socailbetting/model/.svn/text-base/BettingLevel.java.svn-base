package com.digitalchocolate.socailbetting.model;

import java.io.Serializable;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BettingLevel  implements Serializable{

	private static final long serialVersionUID = -7542883500681238432L;
	@Id	
	private String id;
	
	private Long projectId;
	private Integer level;
	private Double odds;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Double getOdds() {
		return odds;
	}
	public void setOdds(Double odds) {
		this.odds = odds;
	}

}
