package com.openbet.socailbetting.utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum TransactionStatusEnum {
	NEW("NEW"),COMMITED("COMMITED");
	
	private final String value;
	
	private TransactionStatusEnum(String value)
	{
		this.value = value;
	}
	
	@JsonValue 
	public String value() { return value; }
	
	@JsonCreator
	public static TransactionStatusEnum forValue(String v) 
	{ 
		if("NEW".equals(v))
		{
			return TransactionStatusEnum.NEW;
		}
	    else if("COMMITED".equals(v))
		{
			return TransactionStatusEnum.COMMITED;
		}else
		{
			return null;
		}
	}
}
