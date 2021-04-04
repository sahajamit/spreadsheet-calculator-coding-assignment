package com.sahajamit.model;


import com.sahajamit.model.Token;

public class ValueToken extends Token {

	public static final String valuePatternRegex = "[+-]?\\d+";

	public ValueToken(String str) {
		setToken(str);
	}

	public double getParsedValue() {
		return Double.parseDouble(getToken());
	}
}
