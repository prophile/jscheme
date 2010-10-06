package com.hovercatsw.alistair.jscheme.parser.*;

public class Driver
{
	private Evaluator evaluator;

	public Driver()
	{
		evaluator = new Evaluator();
	}

	private String readFile(File file)
	{
		long length = file.length();
		char[] data = new char[length];
		FileReader reader = new FileReader(file);
		try
		{
			reader.read(data);
		}
		catch (IOException e)
		{
			return "";
		}
		return new String(data);
	}

	public Evaluator getEvaluator()
	{
		return evaluator;
	}

	public void executeString(String string)
	{
		Lexer lexer = new Lexer(source);
		Parser parser = new Parser(lexer);
		Value value;
		Evaluator eval = getEvaluator();
		while ((value = parser.parseSValue()) != null)
		{
			eval.evaluate(value);
		}
	}

	public void executeFile(File file)
	{
		String source = readFile(file);
		executeString(Source);
	}

	public void executeFile(String path)
	{
		executeFile(new File(path));
	}
}
