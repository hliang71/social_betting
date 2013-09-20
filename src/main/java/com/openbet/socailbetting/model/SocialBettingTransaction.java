package com.openbet.socailbetting.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Version;

import com.openbet.socailbetting.utils.HashUtils;
import com.openbet.socailbetting.utils.TransactionStatusEnum;


@Document
public class SocialBettingTransaction implements Serializable{

	private static final long serialVersionUID = 7788492889729118366L;
	private static final String dayFormat ="yyyyMMdd";
	private static final Logger log = Logger.getLogger(SocialBettingTransaction.class);
	private static final int HASH_SIZE=30;
	@Id	
	private String id;
	
	@Version
	private Integer version=0;
	
	private Date commitTime = new Date();
	
	private TransactionStatusEnum status = TransactionStatusEnum.NEW;
	private Integer day=Integer.valueOf(new SimpleDateFormat(dayFormat).format(new Date()));

	public SocialBettingTransaction()
	{
		try
		{
			this.day = Integer.valueOf(new SimpleDateFormat(dayFormat).format(new Date()));
		}catch(Exception e)
		{
			log.error("failed to set the day.", e);
		}
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		if(StringUtils.isNotBlank(id))
		{
			this.day = HashUtils.hash(id, HASH_SIZE);
		}
	}

	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Date getCommitTime() {
		return commitTime;
	}

	public void setCommitTime(Date commitTime) {
		this.commitTime = commitTime;
	}

	public TransactionStatusEnum getStatus() {
		return status;
	}

	public void setStatus(TransactionStatusEnum status) {
		this.status = status;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}
	
}
