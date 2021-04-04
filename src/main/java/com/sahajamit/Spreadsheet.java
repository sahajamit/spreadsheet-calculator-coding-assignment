package com.sahajamit;

import com.sahajamit.exceptions.CyclicDependencyException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Logger;

public class Spreadsheet {

	private static final Logger LOGGER = Logger.getLogger(Spreadsheet.class.getName());
	public static final int STATUS_CODE_FAILURE = 1;

	public static final String NOT_EVALUATED = "Not Evaluated";
	public static final String CYCLIC_DEPENDENCY = " (Cyclic Dependency)";

	private final WorkBook workBook;
	private boolean prettyPrint;
	private Scanner inputScanner;
	private String inputCsvFilePath;
    private String outputCsvFilePath;

	public Spreadsheet(CommandParameters commandParameters) {
		this.inputCsvFilePath = commandParameters.inputCsvFilePath;
		this.outputCsvFilePath = commandParameters.outputCsvFilePath;
		this.prettyPrint = commandParameters.prettyPrint;

		workBook = new WorkBook();
		inputScanner = new Scanner(System.in);
	}

	public Spreadsheet() {
		workBook = new WorkBook();
		inputScanner = new Scanner(System.in);
	}


	public boolean isWorkBookCyclic() {
		return workBook.isCyclicDependent();
	}


	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void processWorkbook() throws IOException {
		try {
			workBook.readInput(new File(inputCsvFilePath));
		} catch (RuntimeException re) {
			LOGGER.severe(re.getMessage());
			System.exit(STATUS_CODE_FAILURE);
		}
	}

	public void evaluate() {
		try {
			workBook.evaluate();
		} catch (IllegalArgumentException iae) {
			LOGGER.severe(iae.getMessage());
			System.exit(STATUS_CODE_FAILURE);
		} catch (RuntimeException re) {
			LOGGER.severe(re.getMessage());
			System.exit(STATUS_CODE_FAILURE);
		} catch (CyclicDependencyException cde) {
			LOGGER.severe(cde.getMessage());
		}
	}

	public String getResults() {
		return workBook.printWorkbook(true);
	}

	public void prettyPrintResults() {
		workBook.prettyPrintWorkbook(false);
		workBook.prettyPrintWorkbook(true);
	}

	public void writeToCSV() throws IOException {
	    workBook.dumpToCsv(Paths.get(this.outputCsvFilePath));
    }

	public void setInputCsvFilePath(String inputCsvFilePath) {
		this.inputCsvFilePath = inputCsvFilePath;
	}
}
