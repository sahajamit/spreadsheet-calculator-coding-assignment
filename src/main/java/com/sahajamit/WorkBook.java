package com.sahajamit;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.sahajamit.exceptions.CyclicDependencyException;
import com.sahajamit.model.*;
import com.sahajamit.utils.PrettyPrinter;
import com.sahajamit.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class WorkBook {

	private final LinkedList<SpreadsheetCell> topologicalList;
	private final HashMap<Integer, HashSet<SpreadsheetCell>> dependenciesMap;
	private int n; // number of columns (width)
	private int m; // number of rows (height)
	private SpreadsheetCell[][] spreadsheetCellMatrices;
	private int unsolvedCells;
	boolean cyclicDependent;

	public boolean isCyclicDependent() {
		return cyclicDependent;
	}

	public void setCyclicDependent(boolean cyclicDependent) {
		this.cyclicDependent = cyclicDependent;
	}

	public WorkBook() {
		topologicalList = new LinkedList<SpreadsheetCell>();
		dependenciesMap = new HashMap<Integer, HashSet<SpreadsheetCell>>();
		setCyclicDependent(false);
	}

	public void readInput(File csvFile) throws RuntimeException, IOException {

		n = (int) Utils.getCSVColCount(csvFile.toPath());
		m = (int) Utils.getCSVRowCount (csvFile.toPath());

		CSVParser parser = new CSVParserBuilder()
				.withSeparator(',')
				.withIgnoreQuotations(true)
				.build();

		Reader reader = Files.newBufferedReader(csvFile.toPath());
		CSVReader csvReader = new CSVReaderBuilder(reader)
				.withSkipLines(0)
				.withCSVParser(parser)
				.build();

		try{
            unsolvedCells = n * m;
            spreadsheetCellMatrices = new SpreadsheetCell[m][n];

            String[] record;
            for (int row = 0; row < m; row++) {
                record = csvReader.readNext();
                for (int col = 0; col < n; col++) {
                    String data = record[col];
                    SpreadsheetCell curSpreadsheetCell = spreadsheetCellMatrices[row][col] = new SpreadsheetCell(row, col, data);
                    if (curSpreadsheetCell.getReferences().size() > 0) {
                        addToDependenciesMap(curSpreadsheetCell);
                    } else {
                        topologicalList.add(curSpreadsheetCell);
                    }
                }
            }
        }catch (Exception e){
		    throw e;
        }finally {
            csvReader.close();
        }
	}

	private void addToDependenciesMap(SpreadsheetCell curSpreadsheetCell) {
		LinkedList<ReferenceToken> curCellDeps = curSpreadsheetCell.getReferences();
		for (ReferenceToken tok : curCellDeps) {
			HashSet<SpreadsheetCell> refBy;
			if (dependenciesMap.containsKey(tok.hashCode())) {
				refBy = dependenciesMap.get(tok.hashCode());
			} else {
				dependenciesMap.put(tok.hashCode(), refBy = new HashSet<SpreadsheetCell>());
			}
			refBy.add(curSpreadsheetCell);
		}
	}

	public void evaluate() throws CyclicDependencyException, RuntimeException {
		while (topologicalList.size() > 0) {
			SpreadsheetCell curSpreadsheetCell = topologicalList.removeFirst();
			evaluate(curSpreadsheetCell);
			unsolvedCells--;
			resolveDependencies(curSpreadsheetCell);
		}
		if (unsolvedCells != 0) {
			setCyclicDependent(true);
			throw new CyclicDependencyException("CyclicDependencyFound: Unable to solve the workbook");
		}
	}

	private void resolveDependencies(SpreadsheetCell curSpreadsheetCell) {
		// get all the cells dependent on this cell
		if (dependenciesMap.containsKey(curSpreadsheetCell.hashCode())) {
			HashSet<SpreadsheetCell> curSpreadsheetCellDeps = dependenciesMap.get(curSpreadsheetCell.hashCode());
			if (curSpreadsheetCellDeps.size() > 0) { // if there are cells dependent on this one
				for (SpreadsheetCell depSpreadsheetCell : curSpreadsheetCellDeps) {
					depSpreadsheetCell.setUnresolvedRefs(depSpreadsheetCell.getUnresolvedRefs() - 1);
					if (depSpreadsheetCell.getUnresolvedRefs() == 0) // if all references resolved then add to topological list
						topologicalList.add(depSpreadsheetCell);
				}
			}
		}
	}

	private double evaluate(SpreadsheetCell curSpreadsheetCell) throws RuntimeException {

		if (curSpreadsheetCell.isEvaluated())
			return curSpreadsheetCell.getEvaluatedValue();

//            Double value = ExpressionEvaluator.evaluateAndPrintResult(curSpreadsheetCell);
//            curSpreadsheetCell.setEvaluatedValue(value);

		Stack<Double> RPNStack = new Stack<Double>();
		LinkedList<Token> curCellTokens = curSpreadsheetCell.getTokenList();
		OperatorToken lastOperator = null;
		try{
            for (Token tok : curCellTokens) {
                if (tok.getClass().equals(ValueToken.class)) {
                    RPNStack.push(((ValueToken) tok).getParsedValue());
                } else if (tok.getClass().equals(ReferenceToken.class)) {
                    ReferenceToken refTok = (ReferenceToken) tok;
                    SpreadsheetCell refSpreadsheetCell = spreadsheetCellMatrices[refTok.getRefRow()][refTok.getRefCol()];
                    RPNStack.push(evaluate(refSpreadsheetCell));
                } else if (tok.getClass().equals(OperatorToken.class)) {
                    lastOperator = (OperatorToken) tok;
//                    RPNStack = opTok.getParsedValue().apply(RPNStack);
                }else if (tok.getClass().equals(BracketToken.class)) {
                    if(tok.getToken().equals(")"))
                        RPNStack = lastOperator.getParsedValue().apply(RPNStack);
                    else
                        continue;
                } else {
                    throw new RuntimeException("Error: Invalid token: " + tok.toString());
                }
            }
            curSpreadsheetCell.setEvaluatedValue(RPNStack.pop());
            curSpreadsheetCell.setEvaluated(true);
            return curSpreadsheetCell.getEvaluatedValue();

        }catch (IllegalArgumentException e1){
	    	if(e1.getMessage().toLowerCase().contains("cannot divide by 0")){
				curSpreadsheetCell.setEvaluatedValue(0); //error value for divisibility by zero
				curSpreadsheetCell.setEvaluated(true);
				curSpreadsheetCell.setZeroDivisibilityError(true);
			}
		} catch(Exception e){
	        e.printStackTrace();
	        throw e;
        }finally {
			return curSpreadsheetCell.getEvaluatedValue();
		}
	}

	public String printWorkbook(boolean results) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (results) {
					if (spreadsheetCellMatrices[i][j].isEvaluated()) {
						String value;
						if(spreadsheetCellMatrices[i][j].getZeroDivisibilityError())
							value = "#DIV/0!";
						else
							value = String.format("%.5f", spreadsheetCellMatrices[i][j].getEvaluatedValue());
						stringBuilder.append(value);
					} else {
						stringBuilder.append(Spreadsheet.NOT_EVALUATED);
						if (dependenciesMap.containsKey(spreadsheetCellMatrices[i][j].hashCode())) {
							stringBuilder.append(Spreadsheet.CYCLIC_DEPENDENCY);
						}
					}
				} else {
					stringBuilder.append(spreadsheetCellMatrices[i][j].getContents());
				}
				if (!(i == m - 1 && j == n - 1))
					stringBuilder.append(System.lineSeparator());
			}
		}
		return stringBuilder.toString();
	}

	public void dumpToCsv(Path path) throws IOException {
		String[][] matrix = new String[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (spreadsheetCellMatrices[i][j].isEvaluated()) {
					if(spreadsheetCellMatrices[i][j].getZeroDivisibilityError())
						matrix[i][j] = "#DIV/0!";
					else
						matrix[i][j] = String.format("%.5f", spreadsheetCellMatrices[i][j].getEvaluatedValue());
				} else {
					matrix[i][j] = Spreadsheet.NOT_EVALUATED;
					if (dependenciesMap.containsKey(spreadsheetCellMatrices[i][j].hashCode())) {
						matrix[i][j] = matrix[i][j] + Spreadsheet.CYCLIC_DEPENDENCY;
					}
				}
			}
		}

		String outputCSV = Arrays.stream(matrix)
				.map(row-> Arrays.stream(row)
					.map(col->col.toString())
						.collect(Collectors.joining(", ")))
				.collect(Collectors.joining("\n"));
		Files.write(path,outputCSV.getBytes());

	}

	public void prettyPrintWorkbook(boolean results) {
		String[][] matrix = new String[m + 1][n + 1];
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (i == 0 || j == 0) {
					if (i == 0 && j == 0) {
						matrix[i][j] = " ";
					} else if (i == 0) {
						matrix[i][j] = String.valueOf(j-1);
					} else {
						matrix[i][j] = Utils.getRowName(i - 1);
					}
				} else {
					if (results) {
						if (spreadsheetCellMatrices[i - 1][j - 1].isEvaluated()) {
							if(spreadsheetCellMatrices[i - 1][j - 1].getZeroDivisibilityError())
								matrix[i][j] = "#DIV/0!";
							else
								matrix[i][j] = String.format("%.5f", spreadsheetCellMatrices[i - 1][j - 1].getEvaluatedValue());
						} else {
							matrix[i][j] = Spreadsheet.NOT_EVALUATED;
							if (dependenciesMap.containsKey(spreadsheetCellMatrices[i - 1][j - 1].hashCode())) {
								matrix[i][j] = matrix[i][j] + Spreadsheet.CYCLIC_DEPENDENCY;
							}
						}
					} else {
						matrix[i][j] = spreadsheetCellMatrices[i - 1][j - 1].getContents();
					}
				}
			}
		}
		if (results) {
			System.out.println("Output CSV:");
		} else {
			System.out.println("Input CSV:");
		}
		final PrettyPrinter printer = new PrettyPrinter(System.out);
		printer.print(matrix);
	}
}
