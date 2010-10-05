package com.hovercatsw.alistair.jscheme;

class BuiltinValue extends Value
{
	private BuiltinCallback callback;

	public BuiltinValue(BuiltinCallback cbk)
	{
		callback = cbk;
	}

	public BuiltinCallback getCallback()
	{
		return callback;
	}
}
