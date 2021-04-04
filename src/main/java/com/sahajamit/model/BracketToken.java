package com.sahajamit.model;


public class BracketToken extends Token {
	public static final String bracketPatternRegex = "[(|)]";

	public BracketToken(String str) {
		setToken(str);
	}

	public String getParsedValue() {
		return getToken();
	}

}
