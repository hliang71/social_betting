package com.openbet.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum StartCompletionEvaluateEnum {
	ALL_OPPONENTS_COMPLETED("ALL_OPPONENTS_COMPLETED");
	
	private final String value;
	
	private StartCompletionEvaluateEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	@JsonCreator
	public static StartCompletionEvaluateEnum forValue(String v) 
	{ 
		if("ALL_OPPONENTS_COMPLETED".equals(v))
		{
			return StartCompletionEvaluateEnum.ALL_OPPONENTS_COMPLETED;
		}
		return null;
	}
}
