package com.hovercatsw.alistair.jscheme;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ListValue extends Value implements Iterable<Value>
{
	private ArrayList<Value> actualValues;

	public ListValue(List<Value> values)
	{
		actualValues = new ArrayList<Value>();
		actualValues.addAll(values);
	}

	@SuppressWarnings("unchecked")
	public ListValue(Value first, ListValue remaining)
	{
		actualValues = (ArrayList<Value>)(remaining.actualValues.clone());
		actualValues.add(0, first);
	}

	public boolean isEmpty()
	{
		return actualValues.isEmpty();
	}

	public Value firstValue()
	{
		assert !(isEmpty()) : "called firstValue on the empty list";
		return actualValues.get(0);
	}

	public ListValue trailingValues()
	{
		assert !(isEmpty()) : "called trailingValues on the empty list";
		ArrayList<Value> copy = new ArrayList<Value>();
		copy.addAll(1, actualValues);
		return new ListValue(copy);
	}

	public Iterator<Value> iterator()
	{
		return actualValues.iterator();
	}
}
