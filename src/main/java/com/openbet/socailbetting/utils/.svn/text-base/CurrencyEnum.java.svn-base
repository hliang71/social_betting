package com.openbet.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum CurrencyEnum {
	PC("PC"), GC("GC");
	
	private final String value;
	
	private CurrencyEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	@JsonCreator
	public static CurrencyEnum forValue(String v) 
	{ 
		if("PC".equals(v))
		{
			return CurrencyEnum.PC;
		}else if("GC".equals(v))
		{
			return CurrencyEnum.GC;
		}else
		{
			return null;
		}
	}
}


