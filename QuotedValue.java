package com.hovercatsw.alistair.jscheme;

public class QuotedValue extends Value
{
	private Value underlyingValue;

	public QuotedValue(Value underlying)
	{
		underlyingValue = underlying;
	}

	public Value getUnderlyingValue()
	{
		return underlyingValue;
	}
}
