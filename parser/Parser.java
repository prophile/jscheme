package com.hovercatsw.alistair.jscheme.parser;

import com.hovercatsw.alistair.jscheme.*;

public class Parser
{
	private Lexer lexer;

	public Parser(Lexer lex)
	{
		lexer = lex;
	}

	private Value parseSValue()
	{
		Value returnValue = null;
		switch (lexer.currentLexeme())
		{
			case Lexeme.INTEGER_CONSTANT:
				returnValue = new IntegerValue(Long.parse(lexer.currentLexemeData()));
				lexer.advance();
				break;
			case Lexeme.REAL_CONSTANT:
				returnValue = new RealValue(Double.parse(lexer.currentLexemeData()));
				lexer.advance();
				break;
			case Lexeme.BOOLEAN_TRUE_CONSTANT:
				returnValue = new BooleanValue(true);
				lexer.advance();
				break;
			case Lexeme.BOOLEAN_FALSE_CONSTANT:
				returnValue = new BooleanValue(false);
				lexer.advance();
				break;
			case Lexeme.STRING_CONSTANT:
				returnValue = new StringValue(lexer.currentLexemeData().substring(1, lexer.currentLexemeData().length() - 1));
				lexer.advance();
				break;
			case Lexeme.SYMBOL_CONSTANT:
				returnValue = new SymbolValue(lexer.currentLexemeData());
				lexer.advance();
				break;
			case Lexeme.QUOTE:
				{
					ArrayList<Value> listValue = new ArrayList<Value>;
					listValue.add(new SymbolValue("quote"));
					lexer.advance();
					listValue.add(parseSValue());
					returnValue = new ListValue(listValue);
				}
				lexer.advance();
				break;
			case Lexeme.LPAREN:
				{
					ArrayList<Value> listValue = new ArrayList<Value>;
					lexer.advance();
					Value val;
					while ((val = parseSValue()) != null)
						listValue.add(val);
					boolean caught = lexer.eat(Lexeme.RPAREN);
					assert caught : "no rparen";
					returnValue = new ListValue(listValue);
				}
				break;
			case Lexeme.RPAREN:
			case Lexeme.EOF:
				break;
		}
		return returnValue;
	}

	public Value nextValue()
	{
		return parseSValue();
	}
}
