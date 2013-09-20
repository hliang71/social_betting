package com.openbet.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum StatusEnum {	
	NEW("NEW"),
	WAIT_OPPONENT("WAIT_OPPONENT"),	
	LIVE("LIVE"),
	SETTLED("SETTLED"),
	EXPIRED("EXPIRED"),
	RECONCILED("RECONCILED");
	
	private final String value;
	
	private StatusEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	@JsonCreator
	public static StatusEnum forValue(String v) 
	{ 
		if("NEW".equals(v))
		{
			return StatusEnum.NEW;
		}
	    else if("LIVE".equals(v))
		{
			return StatusEnum.LIVE;
		}else if("WAIT_OPPONENT".equals(v))
		{
			return StatusEnum.WAIT_OPPONENT;
		}
		else if("SETTLED".equals(v))
		{
			return StatusEnum.SETTLED;
		}else if("EXPIRED".equals(v))
		{
			return StatusEnum.EXPIRED;
		}else if("RECONCILED".equals(v))
		{
			return StatusEnum.RECONCILED;
		}else
		{
			return null;
		}
	}
}
