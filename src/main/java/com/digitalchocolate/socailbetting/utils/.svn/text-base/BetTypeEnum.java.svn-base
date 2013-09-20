package com.digitalchocolate.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum BetTypeEnum {
	H2H("H2H"), C2C("C2C");
    
	private final String value;
	
	private BetTypeEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	@JsonCreator
	public static BetTypeEnum forValue(String v) 
	{ 
		if("H2H".equals(v))
		{
			return BetTypeEnum.H2H;
		}if("C2C".equals(v))
		{
			return BetTypeEnum.C2C;
		}else
		{
			return null;
		}
	}
}
