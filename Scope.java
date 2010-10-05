package com.hovercatsw.alistair.jscheme;

import java.util.HashMap;

public class Scope
{
	private Scope enclosingScope;
	private HashMap<String, Value> variables = new HashMap<String, Value>();

	public Scope()
	{
		enclosingScope = null;
	}

	public Scope(Scope enclosing)
	{
		enclosingScope = enclosing;
	}

	public void bind(String key, Value val)
	{
		variables.put(key, val);
	}

	public Value lookup(String key)
	{
		Value rv;
		if ((rv = variables.get(key)) != null)
			return rv;
		else if (enclosingScope != null)
			return enclosingScope.lookup(key);
		else
			return null;
	}
}
