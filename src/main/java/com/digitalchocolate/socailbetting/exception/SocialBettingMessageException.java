package com.digitalchocolate.socailbetting.exception;

public class SocialBettingMessageException extends SocialBettingInternalException{
	
	private static final long serialVersionUID = -2018643088117287573L;

	public SocialBettingMessageException() {
		super();
	}

	public SocialBettingMessageException(String message) {
		super(message);
	}

	public SocialBettingMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public SocialBettingMessageException(Throwable cause) {
		super(cause);
	}

	protected SocialBettingMessageException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
