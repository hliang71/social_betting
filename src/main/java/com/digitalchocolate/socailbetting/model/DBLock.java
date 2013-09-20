package com.digitalchocolate.socailbetting.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Version;

@Document
public class DBLock  implements Serializable {

	private static final long serialVersionUID = -644616577498776791L;
	@Id	
	private String id;
	@Version
	private Integer version=0;
	
	private Long jobId;
	
    private String jobName;
    
    private Boolean locked;
    
    private Date lockDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Date getLockDate() {
		return lockDate;
	}

	public void setLockDate(Date lockDate) {
		this.lockDate = lockDate;
	}
    
}
