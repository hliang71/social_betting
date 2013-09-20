package com.openbet.socailbetting.exception;

import com.openbet.socailbetting.utils.ErrCode;

public class SocialBettingSecurityException extends RuntimeException{

	private static final long serialVersionUID = 8626750083422744431L;
	
	private String errCode;
	private String errMsg;
		
	public SocialBettingSecurityException(ErrCode errCode, String errMsg)
	{
		super(errMsg);
		this.errCode = errCode.value();
		this.errMsg = errMsg;
	}
	
	public SocialBettingSecurityException(ErrCode errCode, String errMsg, Throwable t)
	{
		super(errMsg, t);
		this.errCode = errCode.value();
		this.errMsg = errMsg;
	}
	
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(ErrCode errCode) {
		this.errCode = errCode.value();
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
}
