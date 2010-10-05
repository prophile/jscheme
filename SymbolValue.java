package com.hovercatsw.alistair.jscheme;

public class SymbolValue extends Value
{
	private String actualValue;

	public SymbolValue(String value)
	{
		actualValue = value;
	}

	String getName()
	{
		return actualValue;
	}
}
