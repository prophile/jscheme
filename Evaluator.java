package com.hovercatsw.alistair.jscheme;

import java.util.List;
import java.util.ArrayList;

public class Evaluator
{
	private Scope globalScope;
	private Scope localScope;

	public Evaluator()
	{
		globalScope = new Scope();
		localScope  = new Scope(globalScope);
		// bind builtins here
	}

	public void pushScope()
	{
		localScope = new Scope(localScope);
	}

	public void popScope()
	{
		localScope = localScope.getEnclosingScope();
	}

	public void declare(String name, Value value)
	{
		getGlobalScope().bind(name, value);
	}

	public void set(String name, Value value)
	{
		getCurrentLocalScope().bind(name, value);
	}

	public Value lookup(String name)
	{
		return getCurrentLocalScope().lookup(name);
	}

	private Scope getCurrentLocalScope()
	{
		return localScope;
	}

	private Scope getGlobalScope()
	{
		return globalScope;
	}

	public Value evaluate(Value value)
	{
		if (value instanceof BasicValue)
			return evaluateBasicValue((BasicValue)value);
		if (value instanceof LambdaValue)
			return evaluateLambdaValue((LambdaValue)value);
		if (value instanceof QuotedValue)
			return evaluateQuotedValue((QuotedValue)value);
		if (value instanceof SymbolValue)
			return evaluateSymbolValue((SymbolValue)value);
		if (value instanceof ListValue)
			return evaluateListValue((ListValue)value);
		if (value instanceof BuiltinValue)
			return evaluateBuiltinValue((BuiltinValue)value);
		assert false : "bad value type";
		return null;
	}

	private Value evaluateBuiltinValue(BuiltinValue value)
	{
		return value.getCallback().evaluateBuiltin(this, value);
	}

	private Value evaluateBasicValue(BasicValue value)
	{
		return value;
	}

	private Value evaluateLambdaValue(LambdaValue value)
	{
		return value;
	}

	private Value evaluateQuotedValue(QuotedValue value)
	{
		return value.getUnderlyingValue();
	}

	private Value evaluateSymbolValue(SymbolValue value)
	{
		return getCurrentLocalScope().lookup(value.getName());
	}

	private void bindLambda(Scope scope, LambdaValue lambda, List<Value> bindings)
	{
		List<String> keys = lambda.getVariables();
		int length = keys.size();
		assert (length == bindings.size()) : "wrong number of arguments in lambda application";
		for (int i = 0; i < length; ++i)
		{
			scope.bind(keys.get(i), bindings.get(i));
		}
	}

	private Value evaluateListValue(ListValue value)
	{
		// grab the lambda
		Value first = value.firstValue();
		first = evaluate(first);
		assert (first instanceof LambdaValue) : "not a lambda";
		LambdaValue lambda = (LambdaValue)first;
		// grab the bound values
		ArrayList<Value> bindings = new ArrayList<Value>();
		for (Value val : value.trailingValues())
		{
			bindings.add(evaluate(val));
		}
		// create the lambda scope
		pushScope();
		// make bindings
		bindLambda(localScope, lambda, bindings);
		// evaluate the body
		Value returnValue = evaluate(lambda.getBody());
		// pop the scope
		popScope();
		return returnValue;
	}
}
