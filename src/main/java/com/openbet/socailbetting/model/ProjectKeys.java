package com.openbet.socailbetting.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class ProjectKeys {
	
	@Id
	private String id;
	
    private Long projectId;
    private String key;
	
    
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
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	} 
	public void generateKey()
	{
		this.key = UUID.randomUUID().toString();
	}
}
