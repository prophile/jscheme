package com.hovercatsw.alistair.jscheme;

public class StringValue extends BasicValue
{
	private String actualValue;

	public StringValue(String value)
	{
		actualValue = value;
	}

	String getValue()
	{
		return actualValue;
	}
}
