package com.digitalchocolate.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum BetHistoryStatusEnum {
	NEW("NEW"),
	CREATED("CREATED"),	
	MATCHED("MATCHED"),
	LIVE("ALIVE"),
	SETTLED("SETTLED"),
	EXPIRED("EXPIRED");
	
	private final String value;
	
	private BetHistoryStatusEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	public String getStatusValue() 
	{ 
		if("CREATED".equals(this.value))
		{
			return StatusEnum.WAIT_OPPONENT.value();
		}
	    else if("MATCHED".equals(this.value))
		{
			return StatusEnum.LIVE.value();
		}
		else if("SETTLED".equals(this.value))
		{
			return StatusEnum.SETTLED.value();
		}else if("EXPIRED".equals(this.value))
		{
			return StatusEnum.EXPIRED.value();
		}else if("ALIVE".equals(this.value))
		{
			return "ALIVE";
		}else if("NEW".equals(this.value))
		{
			return null;
		}else
		{
			return null;
		} 
	}
	
	@JsonCreator
	public static BetHistoryStatusEnum forValue(String v) 
	{ 
		if("CREATED".equals(v))
		{
			return BetHistoryStatusEnum.CREATED;
		}
	    else if("MATCHED".equals(v))
		{
			return BetHistoryStatusEnum.MATCHED;
		}
		else if("SETTLED".equals(v))
		{
			return BetHistoryStatusEnum.SETTLED;
		}else if("LIVE".equals(v))
		{
			return BetHistoryStatusEnum.LIVE;
		}else if("EXPIRED".equals(v))
		{
			return BetHistoryStatusEnum.EXPIRED;
		}else
		{
			return null;
		}
	}
}
