package com.sahajamit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SpreadsheetTest {

	private final String filepath;
	private final String expectedResult;
	private Spreadsheet appTest;

	public SpreadsheetTest(String filepath,
						   String expectedResult) {
		this.filepath = filepath;
		this.expectedResult = expectedResult;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> configs() {
		return Arrays.asList(new Object[][]{
				{"input_basic_testcase.csv", "20.00000" + System.lineSeparator() +
						"20.00000" + System.lineSeparator() +
						"20.00000" + System.lineSeparator() +
						"8.66667" + System.lineSeparator() +
						"3.00000" + System.lineSeparator() +
						"112.66667"},
				{"input_cyclic_reference.csv", "Not Evaluated (Cyclic Dependency)" + System.lineSeparator() +
						"Not Evaluated (Cyclic Dependency)" + System.lineSeparator() +
						"Not Evaluated" + System.lineSeparator() +
						"Not Evaluated (Cyclic Dependency)" + System.lineSeparator() +
						"3.00000" + System.lineSeparator() +
						"Not Evaluated"},
				{"input_zero_divisibility.csv", "3.00000" + System.lineSeparator() +
						"#DIV/0!"}
		});
	}

	@Before
	public void initialize() {
		appTest = new Spreadsheet();
	}

	@Test
	public void testApp() throws IOException {
		try {
            appTest.setInputCsvFilePath(filepath);
		} catch (Exception e) {
			System.err.println("Some Exception : ");
			throw e;
		}
		appTest.processWorkbook();
		appTest.evaluate();
		assertEquals(expectedResult, appTest.getResults());
	}
}
