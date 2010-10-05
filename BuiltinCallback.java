package com.hovercatsw.alistair.jscheme;

abstract public class BuiltinCallback
{
	abstract public Value evaluateBuiltin(Evaluator evaluator, BuiltinValue value);
}
