package com.digitalchocolate.socailbetting.webapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.digitalchocolate.socailbetting.utils.BetHistoryStatusEnum;
import com.digitalchocolate.socailbetting.utils.BetTypeEnum;

public class HistoryRequestBean extends BaseRequestDTO{
	
	private Integer begin;
	private BetTypeEnum betType;
	private Date endDate;
	private Integer limit;
	private Date startDate;
	private BetHistoryStatusEnum status = BetHistoryStatusEnum.NEW;
	
	
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public BetTypeEnum getBetType() {
		return betType;
	}
	public void setBetType(BetTypeEnum betType) {
		this.betType = betType;
	}
	
	public BetHistoryStatusEnum getStatus() {
		return status;
	}
	public void setStatus(BetHistoryStatusEnum status) {
		this.status = status;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
	public Integer getBegin() {
		return begin;
	}
	public void setBegin(Integer begin) {
		this.begin = begin;
	}
	@Override
	public String getRequestContent() {
		String beg = begin == null? "" : begin.toString();
		String bType = betType == null? "" : betType.value();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String endDateStr = null;		
		if(this.endDate != null)
		{
			endDateStr = format.format(this.endDate);
		}
		String edateStr = StringUtils.isBlank(endDateStr) ? "" : endDateStr;
		String lt = limit == null? "" :limit.toString();
        
		String startDateStr = null;	
		if(this.startDate != null)
		{
			startDateStr = format.format(this.startDate);
		}
		String sdateStr = StringUtils.isBlank(startDateStr) ? "" : startDateStr;
		String statusStr =(status == null) ? "" : ( (status == BetHistoryStatusEnum.NEW)? "" : ((status == BetHistoryStatusEnum.LIVE) ? "LIVE" : status.value()) );
		
		return new StringBuilder(beg).append(bType).append(edateStr).append(lt).append(super.getOpponentId()).append(super.getPlatformUserId()).append(super.getProjectId()).append(sdateStr).append(statusStr).toString();
	}
	
}
