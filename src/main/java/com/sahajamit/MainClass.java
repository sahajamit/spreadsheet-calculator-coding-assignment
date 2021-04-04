package com.sahajamit;

import com.beust.jcommander.JCommander;

import static com.sahajamit.Spreadsheet.STATUS_CODE_FAILURE;

public class MainClass {
    public static void main(String[] args) throws Exception{
        CommandParameters commandParameters = new CommandParameters();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(commandParameters)
                .build();
        jCommander.parse(args);
        if (commandParameters.help) {
            jCommander.usage();
            System.exit(0);
        }

        Spreadsheet spreadsheet = new Spreadsheet(commandParameters);
        spreadsheet.processWorkbook();
        spreadsheet.evaluate();

        spreadsheet.writeToCSV();

        if (spreadsheet.isPrettyPrint())
            spreadsheet.prettyPrintResults();


        // if the workbook is cyclic return with non-zero exit code
        if (spreadsheet.isWorkBookCyclic()) {
            System.exit(STATUS_CODE_FAILURE);
        }
    }
}
