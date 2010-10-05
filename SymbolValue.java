package com.hovercatsw.alistair.jscheme;

public class SymbolValue extends BasicValue
{
	private String actualValue;

	public SymbolValue(String value)
	{
		actualValue = value;
	}

	String getValue()
	{
		return actualValue;
	}
}
