package com.digitalchocolate.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum RuleNamesEnum {
	ODDS("ODDS"), OPPONENT("OPPONENT"), SETTLEMENT("SETTLEMENT"), CALLBACK("CALLBACK");
	
	private final String value;
	
	private RuleNamesEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	@JsonCreator
	public static RuleNamesEnum forValue(String v) 
	{ 
		if("ODDS".equals(v))
		{
			return RuleNamesEnum.ODDS;
		}else if("OPPONENT".equals(v))
		{
			return RuleNamesEnum.OPPONENT;
		}else if("SETTLEMENT".equals(v))
		{
			return RuleNamesEnum.SETTLEMENT;
		}else if("CALLBACK".equals(v))
		{
			return RuleNamesEnum.CALLBACK;
		}else
		{
			return null;
		}
	}
}
