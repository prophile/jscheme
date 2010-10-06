package com.hovercatsw.alistair.jscheme;

import java.util.List;
import java.util.ArrayList;

public class Evaluator
{
	private Scope globalScope;
	private Scope localScope;

	private void bindBuiltin(String name, BuiltinCallback callback, String... args)
	{
		ArrayList<String> parameters = new ArrayList<String>();
		for (String arg : args)
			parameters.add(arg);
		BuiltinValue builtin = new BuiltinValue(callback);
		LambdaValue lambda = new LambdaValue(parameters, builin);
		declare(name, lambda);
	}

	private void bindCAR()
	{
		bindBuiltin("__builtin_car", new BuiltinCallback() {
			public Value evaluateBuiltin(Evaluator evaluator, BuiltinValue value) {
				ListValue list = (ListValue)evaluator.lookup("__list");
				return list.firstValue();
			}
		}, "__list");
	}

	private void bindCDR()
	{
		bindBuiltin("__builtin_cdr", new BuiltinCallback() {
			public Value evaluateBuiltin(Evaluator evaluator, BuiltinValue value) {
				ListValue list = (ListValue)evaluator.lookup("__list");
				return list.trailingValues();
			}
		}, "__list");
	}

	private void bindCONS()
	{
		bindBuiltin("__builtin_car", new BuiltinCallback() {
			public Value evaluateBuiltin(Evaluator evaluator, BuiltinValue value) {
				Value base = evaluator.lookup("__car");
				ListValue list = (ListValue)evaluator.lookup("__cdr");
				return new ListValue(base, list);
			}
		}, "__car", "__cdr");
	}

	private void bindIf()
	{
		bindBuiltin("__builtin_if", new BuiltinCallback() {
			public Value evaluateBuiltin(Evaluator evaluator, BuiltinValue value) {
				Value condition, lhs, rhs;
				condition = evaluator.lookup("__condition");
				lhs       = evaluator.lookup("__lhs");
				rhs       = evaluator.lookup("__rhs");
				condition = evaluator.evaluate(condition);
				BooleanValue bcondition = (BooleanValue)condition;
				return bcondition.getValue() ? lhs : rhs;
			}
		}, "__condition", "__lhs", "__rhs");
	}

	private void bindLambda()
	{
		bindBuiltin("__builtin_lambda", new BuiltinCallback() {
			public Value evaluateBuiltin(Evaluator evaluator, BuiltinValue value) {
				Value parameters, body;
				parameters = evaluator.lookup("__parameters");
				body       = evaluator.lookup("__body");
				ArrayList<String> variables = new ArrayList<String>();
				for (Value val : (ListValue)parameters)
				{
					variables.add(((SymbolValue)val).getName());
				}
				return new LambdaValue(variables, body);
			}
		}, "__parameters", "__body");
	}

	private void bindDefine()
	{
		bindBuiltin("__builtin_define", new BuiltinCallback() {
			public Value evaluateBuiltin(Evaluator evaluator, BuiltinValue value) {
				Value key, value;
				key   = evaluator.lookup("__key");
				value = evaluator.lookup("__value");
				SymbolValue keySym = (SymbolValue)key;
				evaluator.bind(key, evaluator.evaluate(value));
				return value;
			}
		}, "__key", "__value");
	}

	private void bindSet()
	{
		bindBuiltin("__builtin_set!", new BuiltinCallback() {
			public Value evaluateBuiltin(Evaluator evaluator, BuiltinValue value) {
				Value key, value;
				key   = evaluator.lookup("__key");
				value = evaluator.lookup("__value");
				SymbolValue keySym = (SymbolValue)key;
				evaluator.set(key, evaluator.evaluate(value), 1);
				return value;
			}
		}, "__key", "__value");
	}

	private void bindStdOut()
	{
		bindBuiltin("__builtin_stdout", new BuiltinCallback {
			public Value evaluateBuiltin(Evaluator evaluator, BuiltinValue value) {
				Value message;
				String output;
				message = evaluator.lookup("__message");
				if (message instanceof StringValue)
					output = ((StringValue)message).getValue();
				else if (message instanceof SymbolValue)
					output = ((SymbolValue)message).getName();
				else if (message instanceof IntegerValue)
					output = "" + ((IntegerValue)message).getValue();
				else if (message instanceof RealValue)
					output = "" + ((RealValue)message).getValue();
				else if (message instanceof BooleanValue)
					output = ((BooleanValue)message).getValue() ? "#t" : "#f";
				return value;
			}
		}, "__message");
	}

	private void bindQuote()
	{
		bindBuiltin("__builtin_quote", new BuiltinCallback {
			public Value evaluateBuiltin(Evaluator evaluator, BuiltinValue value) {
				Value val = evaluator.lookup("__value");
				if (val instanceof BasicValue)
					return val;
				else
					return new QuotedValue(val);
			}
		}, "__value");
	}

	private void bindBuiltins()
	{
		bindIf();
		bindLambda();
		bindDefine();
		bindSet();
		bindStdOut();
		bindQuote();
		bindCONS();
		bindCAR();
		bindCDR();
	}

	public Evaluator()
	{
		globalScope = new Scope();
		localScope  = new Scope(globalScope);
		bindBuiltins();
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

	public void set(String name, Value value, int level)
	{
		Scope target = getCurrentLocalScope();
		while (level > 0)
		{
			target = target.getEnclosingScope();
			--level;
		}
		target.bind(name, value);
	}

	public void set(String name, Value value)
	{
		set(name, value, 0);
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
