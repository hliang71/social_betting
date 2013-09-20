package com.openbet.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum LevelEnum {
	HIGHER("HIGHER"), EQUAL("EQUAL"), LOWER("LOWER");
    
	private final String value;
	
	private LevelEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	@JsonCreator
	public static LevelEnum forValue(String v) 
	{ 
		if("HIGHER".equals(v))
		{
			return LevelEnum.HIGHER;
		}else if("EQUAL".equals(v))
		{
			return LevelEnum.EQUAL;
		}else if("LOWER".equals(v))
		{
			return LevelEnum.LOWER;
		}else
		{
			return null;
		}
	}
}
