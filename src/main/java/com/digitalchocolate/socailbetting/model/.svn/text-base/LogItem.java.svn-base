package com.digitalchocolate.socailbetting.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class LogItem implements Serializable{
	
	private static final long serialVersionUID = 7833739656595891905L;
	@Id	
    private String id;
	private List<LogTarget> items = new ArrayList<LogTarget>();
	private String name;
	private Boolean updated = false;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<LogTarget> getItems() {
		return items;
	}
	public void setItems(List<LogTarget> items) {
		this.items = items;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getUpdated() {
		return updated;
	}
	public void setUpdated(Boolean updated) {
		this.updated = updated;
	}
	

}
