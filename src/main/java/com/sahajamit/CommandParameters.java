package com.sahajamit;

import com.beust.jcommander.Parameter;

public class CommandParameters {
    @Parameter(names = {"--inputCsvFile", "-i"}, description = "Input CSV File", required = true)
    public String inputCsvFilePath;

    @Parameter(names = {"--outputCsvFile", "-o"}, description = "Output CSV File", required = true)
    public String outputCsvFilePath;

    @Parameter(names = {"--prettyPrint", "-p"}, description = "Prints the files on console in pretty format")
    public boolean prettyPrint = false;

    @Parameter(names = {"--Help", "-h"}, help = true)
    public boolean help = false;
}
