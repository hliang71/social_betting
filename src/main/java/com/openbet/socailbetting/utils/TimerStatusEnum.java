package com.openbet.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum TimerStatusEnum {
	OPPONENT_TIMER_EXPIRED("OPPONENT_TIMER_EXPIRED"),
	OPPONENT_TIMER_STARTED("OPPONENT_TIMER_STARTED"),
	OPPONENT_TIMER_DISARMED("OPPONENT_TIMER_DISARMED"),
	COMPLETION_TIMER_EXPIRED("-3.1415926");
	
	private final String value;
	
	private TimerStatusEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	@JsonCreator
	public static TimerStatusEnum forValue(String v) 
	{ 
		if("OPPONENT_TIMER_EXPIRED".equals(v))
		{
			return TimerStatusEnum.OPPONENT_TIMER_EXPIRED;
		}
	    else if("OPPONENT_TIMER_STARTED".equals(v))
		{
			return TimerStatusEnum.OPPONENT_TIMER_STARTED;
		}else if("OPPONENT_TIMER_DISARMED".equals(v))
		{
			return TimerStatusEnum.OPPONENT_TIMER_DISARMED;
		}else if("-3.1415926".equals(v))
		{
			return TimerStatusEnum.COMPLETION_TIMER_EXPIRED;
		}else
		{
			return null;
		}
	}	
}
