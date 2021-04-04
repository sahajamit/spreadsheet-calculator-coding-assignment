package com.sahajamit.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {

	private static final int NUM_ROW_CHARS = 26;
	private static final int ASCII_A = 65;

	public static int getRowIndex(String rIndex) {
		int rowIndex = 0;
		for (int i = 0; i < rIndex.length(); i++) {
			rowIndex = rowIndex * NUM_ROW_CHARS + rIndex.charAt(i) - ASCII_A + 1;
		}
		return (--rowIndex);
	}

	public static int getColIndex(String cIndex) {
//		return Integer.valueOf(cIndex) - 1;
		return Integer.valueOf(cIndex);
	}

	public static String getRowName(int rIndex) {
		int num = rIndex % NUM_ROW_CHARS;
		String ch = String.valueOf((char) (ASCII_A + num));
		rIndex = (rIndex) / NUM_ROW_CHARS;
		if (rIndex > 0)
			return getRowName(rIndex - 1) + ch;
		else
			return ch;
	}

	public static long getCSVRowCount(Path csvPath) throws IOException {
			return Files.lines(csvPath).count();
	}

	public static long getCSVColCount(Path csvPath) throws IOException {
		return Files.lines(csvPath).findFirst().toString()
				.chars().filter(ch -> ch == ',').count() + 1;
	}
}
