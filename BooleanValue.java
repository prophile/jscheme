package com.hovercatsw.alistair.jscheme;

public class BooleanValue extends BasicValue
{
	private boolean actualValue;

	public BooleanValue(boolean value)
	{
		actualValue = value;
	}

	boolean getValue()
	{
		return actualValue;
	}
}
