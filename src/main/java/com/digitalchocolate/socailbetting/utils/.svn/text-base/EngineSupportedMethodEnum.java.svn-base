package com.digitalchocolate.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum EngineSupportedMethodEnum {
	
	PLACE_BET("placeBet"),
	SEARCH_BET("searchBet"),
	GENERATE_BET("createBet"),
	UPDATE_BET("updateBet"),
	COMPLETION_EXPIRED("completionExpired"),
	CALLBACK("callback");
	
	private final String value;
	
	private EngineSupportedMethodEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	@JsonCreator
	public static EngineSupportedMethodEnum forValue(String v) 
	{ 
		if("placeBet".equals(v))
		{
			return EngineSupportedMethodEnum.PLACE_BET;
		}else if("searchBet".equals(v))
		{
			return EngineSupportedMethodEnum.SEARCH_BET;
		}else if("createBet".equals(v))
		{
			return EngineSupportedMethodEnum.GENERATE_BET;
		}else if("updateBet".equals(v))
		{
			return EngineSupportedMethodEnum.UPDATE_BET;
		}else if("completionExpired".equals(v))
		{
			return EngineSupportedMethodEnum.COMPLETION_EXPIRED;
		}else if("callback".equals(v))
		{
			return EngineSupportedMethodEnum.CALLBACK;
		}else
		{
			return null;
		}
	}	

}
