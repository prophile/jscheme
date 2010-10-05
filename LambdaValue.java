package com.hovercatsw.alistair.jscheme;

import java.util.List;
import java.util.ArrayList;

public class LambdaValue extends Value
{
	private List<String> variables;
	private Value body;

	public LambdaValue(List<String> variables, Value body)
	{
		this.variables = new ArrayList<String>();
		this.variables.addAll(variables);
		this.body = body;
	}

	public Value getBody()
	{
		return body;
	}

	public List<String> getVariables()
	{
		return variables;
	} 
}
