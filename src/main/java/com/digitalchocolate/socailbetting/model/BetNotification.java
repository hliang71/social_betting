package com.digitalchocolate.socailbetting.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Version;

import com.digitalchocolate.socailbetting.utils.BetTypeEnum;
import com.digitalchocolate.socailbetting.utils.HashUtils;
import com.digitalchocolate.socailbetting.utils.NotificationStatusEnum;
import com.digitalchocolate.socailbetting.utils.SocialBettingCallTypeEnum;


@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class BetNotification extends TransactionParticipantModel implements Serializable{

	private static final long serialVersionUID = 122297962204397216L;
	private static final Logger log = Logger.getLogger(BetNotification.class);
	
	public static final String CALL_DETAIL_WIN="win";
	public static final String CALL_DETAIL_LOSE="lose";
	public static final String CALL_DETAIL_TIE="tie_refund";
	public static final String CALL_DETAIL_PLACE="place_bet";
	public static final String CALL_DETAIL_CREATED="create_new_bet";
	public static final String CALL_DETAIL_EXPIRED="not_matched_refund";
	public static final String CALL_DETAIL_TIMEOUT="completion_timeout";
	private static final String dayFormat ="yyyyMMdd";
	private static final int HASH_SIZE=30;
	
	@Id	
	private String id;
	
	@Version
	private Integer version=0;
	
	private Long p_id;
	private String p_uid;
	private String type; // currency type;
	private Double amount;
	private String sig;
	private Boolean sbp_call = true;
	private String sbp_event_id;
	private SocialBettingCallTypeEnum sbp_call_type;
	private String sbp_call_detail;
	
	private NotificationStatusEnum status = NotificationStatusEnum.NEW;
	private Date createdTime = new Date();
	private BetTypeEnum betType;
	private Long projectId;
	private Integer day;
	private String content;
	
	public BetNotification()
	{
		this.id = UUID.randomUUID().toString();
		try
		{
			this.day = HashUtils.hash(this.id, HASH_SIZE);
		}catch(Exception e)
		{
			log.error("failed to set the day.");
		}
	}
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
	public Long getP_id() {
		return p_id;
	}
	public void setP_id(Long p_id) {
		this.p_id = p_id;
	}
	public String getP_uid() {
		return p_uid;
	}
	public void setP_uid(String p_uid) {
		this.p_uid = p_uid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getSig() {
		return sig;
	}
	public void setSig(String sig) {
		this.sig = sig;
	}
	public Boolean getSbp_call() {
		return sbp_call;
	}
	public void setSbp_call(Boolean sbp_call) {
		this.sbp_call = sbp_call;
	}
	public String getSbp_event_id() {
		return sbp_event_id;
	}
	public void setSbp_event_id(String sbp_event_id) {
		this.sbp_event_id = sbp_event_id;
	}
	public SocialBettingCallTypeEnum getSbp_call_type() {
		return sbp_call_type;
	}
	public void setSbp_call_type(SocialBettingCallTypeEnum sbp_call_type) {
		this.sbp_call_type = sbp_call_type;
	}
	public String getSbp_call_detail() {
		return sbp_call_detail;
	}
	public void setSbp_call_detail(String sbp_call_detail) {
		this.sbp_call_detail = sbp_call_detail;
	}
	
	public NotificationStatusEnum getStatus() {
		return status;
	}
	public void setStatus(NotificationStatusEnum status) {
		this.status = status;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public BetTypeEnum getBetType() {
		return betType;
	}
	public void setBetType(BetTypeEnum betType) {
		this.betType = betType;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	public String getContent() {
		String amt = this.amount == null? "" : this.amount.toString();
		String tp = StringUtils.isBlank(this.type)? "" : this.type;
		String pid = this.p_id == null? "" : this.p_id.toString();
		String puid = StringUtils.isBlank(this.p_uid)? "" : this.p_uid;
		String sbpCall = this.sbp_call == null? "" : this.sbp_call.toString();
		String sbpEId = StringUtils.isBlank(this.sbp_event_id)? "":this.sbp_event_id;
		String sbpCType = this.sbp_call_type == null? "" : this.sbp_call_type.value();
		String sbpCDetail = StringUtils.isBlank(this.sbp_call_detail)? "" : this.sbp_call_detail;
		StringBuilder builder = new StringBuilder();
		builder.append(amt);
		builder.append(tp);
		builder.append(pid);
		builder.append(puid);
		builder.append(sbpCall);
		builder.append(sbpEId);
		builder.append(sbpCType);
		builder.append(sbpCDetail);
		return builder.toString();	
	}
	
	public String getUniqueKey()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(this.sbp_event_id);
		builder.append(this.sbp_call_type);
		builder.append(this.p_uid);
		return builder.toString();
	}
	public Integer getDay() {
		return day;
	}
	public void setDay(Integer day) {
		this.day = day;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
