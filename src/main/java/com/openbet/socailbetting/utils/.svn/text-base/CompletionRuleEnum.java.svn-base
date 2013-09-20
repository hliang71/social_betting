package com.openbet.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum CompletionRuleEnum {
	LOWEST_VALUE_WIN("LOWEST_VALUE_WIN"), HIGHEST_VALUE_WIN("HIGHEST_VALUE_WIN");
	
    private final String value;
	
	private CompletionRuleEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	@JsonCreator
	public static CompletionRuleEnum forValue(String v) 
	{ 
		if("LOWEST_VALUE_WIN".equals(v))
		{
			return CompletionRuleEnum.LOWEST_VALUE_WIN;
		}else if("HIGHEST_VALUE_WIN".equals(v))
		{
			return CompletionRuleEnum.HIGHEST_VALUE_WIN;
		}
		return null;
	}
}
