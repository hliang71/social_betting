package com.openbet.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum SocialBettingCallTypeEnum {
     SETTLED("SETTLED"), EXPIRED("EXPIRED"), MATCHED("MATCHED"), PLACED("PLACED"), CREATED("CREATED"), TIMEOUT("TIMEOUT");
     
     private final String value;
 	
 	private SocialBettingCallTypeEnum(String value)
 	{
 		this.value = value;
 	}
 	
 	@JsonValue 
 	public String value() { return value; }
 	
 	@JsonCreator
 	public static SocialBettingCallTypeEnum forValue(String v) 
 	{ 
 		if("SETTLED".equals(v))
 		{
 			return SocialBettingCallTypeEnum.SETTLED;
 		}else if("EXPIRED".equals(v))
 		{
 			return SocialBettingCallTypeEnum.EXPIRED;
 		}else if("MATCHED".equals(v))
 		{
 			return SocialBettingCallTypeEnum.MATCHED;
 		}else if("CREATED".equals(v))
 		{
 			return SocialBettingCallTypeEnum.CREATED;
 		}else if("PLACED".equals(v))
 		{
 			return SocialBettingCallTypeEnum.PLACED;
 		}else if("TIMEOUT".equals(v))
 		{
 			return SocialBettingCallTypeEnum.TIMEOUT;
 		}else
 		{
 			return null;
 		}
 	}     
}
