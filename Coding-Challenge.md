**Coding Challenge**

A spreadsheet consists of a two-dimensional array of cells, labeled A0, A1, etc. Rows are identified using letters, columns by numbers. Each cell contains either an integer (its value) or an expression. Expressions always start with a &#39;=&#39; and can contain integers, cell references, operators &#39;+&#39;, &#39;-&#39;, &#39;\*&#39;, &#39;/&#39; and parentheses &#39;(&#39;, &#39;)&#39; with the usual rules of evaluation.

Write a program (in Java, Scala or Kotlin) to read the input from a file, evaluate the values of all the cells, and write the output to an output file.

The input and output files should be in CSV format.

For example, the following CSV input:

2,4,1,=A0+A1\*A2

=A3\*(A0+1),=B2,0,=A0+1

should produce the following output file:

**2.00000,4.00000,1.00000,6.00000**

**18.00000,0.00000,0.00000,3.00000**

The project should include unit tests, a build script (maven, gradle, sbt) and a README file describing how to build the artifacts.

After the build process, the program should run with the following command:

java -jar spreasheet.jar -i inputfile.csv -o outputfile.csv

**Notes**

- Your program should detect cyclic dependencies in the input data, report these in a sensible

- manner, and exit with a non-zero exit code.

- All numbers in the input are positive integers, but internal calculations and output should be in

- double precision floating point.

- You can assume that there are no more than 26 rows (A to Z). However, columns can be up to

- 5,000,000.

- Additional points will be given for any speed optimization which utilizes multi-threading during

- the compute of the spreadsheet values