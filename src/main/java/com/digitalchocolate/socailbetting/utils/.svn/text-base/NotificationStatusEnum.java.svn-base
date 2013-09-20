package com.digitalchocolate.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum NotificationStatusEnum {
	NEW("NEW"),NOTIFIED("NOTIFIED");
	private final String value;
	
	private NotificationStatusEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	@JsonCreator
	public static NotificationStatusEnum forValue(String v) 
	{ 
		if("NEW".equals(v))
		{
			return NotificationStatusEnum.NEW;
		}
	    else if("NOTIFIED".equals(v))
		{
			return NotificationStatusEnum.NOTIFIED;
		}else
		{
			return null;
		}
	}	
}
