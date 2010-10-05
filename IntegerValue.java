package com.hovercatsw.alistair.jscheme;

public class IntegerValue extends BasicValue
{
	private long actualValue;

	public IntegerValue(long value)
	{
		actualValue = value;
	}

	long getValue()
	{
		return actualValue;
	}
}
