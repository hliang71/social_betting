package com.openbet.socailbetting.webapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.openbet.socailbetting.exception.SocialBettingSecurityException;
import com.openbet.socailbetting.utils.ErrCode;

public class ReconcileRequestBean implements SecurityRequest{
	
	private String contentKey;
	private String sig;
	private List<String> eventIds = new ArrayList<String>();
	public String getContentKey() {
		return contentKey;
	}
	public void setContentKey(String contentKey) {
		this.contentKey = contentKey;
	}
	public String getSig() {
		return sig;
	}
	public void setSig(String sig) {
		this.sig = sig;
	}
	public List<String> getEventIds() {
		return eventIds;
	}
	public void setEventIds(List<String> eventIds) {
		this.eventIds = eventIds;
	}
	
	@Override
	public String getRequestContent() {
		return null;
	}
	@Override
	public String getSecurityContent() {
		if(StringUtils.isBlank(contentKey))
		{
			throw new SocialBettingSecurityException(ErrCode.SECURITY_MISSING_CONTENT_KEY, "to reconcile the bet content key is required.");
		}
		
		return contentKey;
	}
	
}
