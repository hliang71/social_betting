package com.digitalchocolate.socailbetting.exception;

import com.digitalchocolate.socailbetting.utils.ErrCode;
/**
 * this exception is client input validation exception.
 * @author hliang
 *
 */
public class RuleEngineException extends SocialBettingValidationException{
	
	private static final long serialVersionUID = 4436854216380680592L;
			
	public RuleEngineException(ErrCode errCode, String errMsg)
	{
		super(errCode, errMsg);
	}
	
	public RuleEngineException(ErrCode errCode, String errMsg, Throwable t)
	{
		super(errCode, errMsg, t);
	}

}
