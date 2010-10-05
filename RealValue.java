package com.hovercatsw.alistair.jscheme;

public class RealValue extends BasicValue
{
	private double actualValue;

	public RealValue(double value)
	{
		actualValue = value;
	}

	double getValue()
	{
		return actualValue;
	}
}
