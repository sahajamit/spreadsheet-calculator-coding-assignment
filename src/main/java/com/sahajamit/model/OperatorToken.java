package com.sahajamit.model;


public class OperatorToken extends Token {

	public OperatorToken(Operators operator) {
		setToken(operator.getOperator());
	}

	public Operators getParsedValue() {
		return Operators.get(getToken());
	}

}
