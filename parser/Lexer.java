package com.hovercatsw.alistair.jscheme.parser;

import java.util.regex.*;
import static java.lang.Character.isWhitespace;

public class Lexer
{
	private Lexeme currentLexeme;
	private String currentLexemeData;

	private String source;
	private int sourceIndex;

	private void skipWhitespace()
	{
		while (sourceIndex < source.length() &&
		       isWhitespace(source.charAt(sourceIndex)))
			sourceIndex++;
	}

	private boolean match(Pattern patt)
	{
		String substring = source.substring(sourceIndex);
		Matcher m = patt.matcher(substring);
		// System.err.println("I'm covered in beeeeees, also, pattern = " + patt.pattern() + " in " + substring);
		if (!m.lookingAt())
			return false;
		int length = m.end();
		assert length > 0 : "got a " + length + "-length token?  (= \"" + m.group() + "\")";
		currentLexemeData = source.substring(sourceIndex, sourceIndex + length);
		sourceIndex += length;
		return true;
	}

	private boolean match(String patt)
	{
		return match(Pattern.compile("\\A" + patt));
	}

	private void next()
	{
		skipWhitespace();
		if (sourceIndex == source.length())
		{
			currentLexeme = Lexeme.EOF;
			currentLexemeData = "";
			return;
		}
		if (match("\\("))                                   currentLexeme = Lexeme.LPAREN;
		else if (match("\\)"))                              currentLexeme = Lexeme.RPAREN;
		else if (match("\\-?[0-9]+\\.[0-9]+e[0-9]+"))       currentLexeme = Lexeme.REAL_CONSTANT;
		else if (match("\\-?[0-9]+\\.[0-9]+"))              currentLexeme = Lexeme.REAL_CONSTANT;
		else if (match("\\-?[0-9]+"))                       currentLexeme = Lexeme.INTEGER_CONSTANT;
		else if (match("#t"))                               currentLexeme = Lexeme.BOOLEAN_TRUE_CONSTANT;
		else if (match("#f"))                               currentLexeme = Lexeme.BOOLEAN_FALSE_CONSTANT;
		else if (match("'"))                                currentLexeme = Lexeme.QUOTE;
		else if (match("\"\""))                             currentLexeme = Lexeme.STRING_CONSTANT;
		else if (match("\".*[^\\\\]\""))                    currentLexeme = Lexeme.STRING_CONSTANT;
		else if (match("\".*\\\\\\\\\""))                   currentLexeme = Lexeme.STRING_CONSTANT; // aah
		else if (match("[^\\p{Space})]+"))                  currentLexeme = Lexeme.SYMBOL_CONSTANT;
		else {
			assert false : "this can never happen, ever (L = " + source.length() + ", SI = " + sourceIndex + ")";
		}
	}

	public Lexer(String sourceString)
	{
		source = sourceString;
		sourceIndex = 0;
		next();
	}

	public Lexeme currentLexeme()
	{
		return currentLexeme;
	}

	public String currentLexemeData()
	{
		return currentLexemeData;
	}

	public Lexeme advance()
	{
		next();
		return currentLexeme();
	}
}
