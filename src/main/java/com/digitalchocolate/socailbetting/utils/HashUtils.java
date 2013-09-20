package com.digitalchocolate.socailbetting.utils;

public class HashUtils {

	public static int hash(String input, int size)
	{
		char ch[];
		ch = input.toCharArray();
		int xlength = input.length();
		int i, sum;
		for (sum=0, i=0; i < input.length(); i++)
		{
			int c = ch[i];
			sum += c*c;
		}
		return sum % size;
	}
}
