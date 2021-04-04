package com.sahajamit.model;

public class TokenFactory {

	public Token makeToken(String str) throws RuntimeException {
		if (Operators.isValidOperator(str))
			return new OperatorToken(Operators.get(str));
		else if (str.matches(ReferenceToken.refPatternRegex))
			return new ReferenceToken(str);
		else if (str.matches(ValueToken.valuePatternRegex))
			return new ValueToken(str);
		else if (str.matches(BracketToken.bracketPatternRegex))
			return new BracketToken(str);
		else
			throw new RuntimeException("Error: Invalid token: " + str);
	}
}
