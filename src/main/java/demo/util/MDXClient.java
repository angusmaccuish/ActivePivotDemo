package demo.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapStatement;
import org.olap4j.layout.RectangularCellSetFormatter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * IntelliJ setup - Go to Settings > Tools > External Tools and add new tool named MDXQuery
 * <p>
 * Tool Settings:
 * <p>
 * Program:           $JDKPath$/bin/java
 * Arguments:         -classpath "$Classpath$" demo.util.MDXClient --file $FilePath$ --user admin --password adminpwd
 * Working directory: $ModuleFileDir$
 * <p>
 * Add to Keymap
 * <p>
 * Go to Settings > Keymap > External Tools > External Tools > MDXQuery and add your preferred key stroke. I chose CTRL + ALT + F10, which was available,
 * as it mirrors CTRL + SHIFT + F10 to run Java code.
 * <p>
 * Then you can simply navigate to any MDX file in your project and press CTRL + ALT + F10 to execute the query
 * <p>
 * Add a --server arg to override the default (http://localhost:8082/demo/xmla)
 */
public class MDXClient {

    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String SERVER = "server";
    private static final String FILE = "file";
    private static final String DEFAULT_SERVER = "http://localhost:8082/demo/xmla";
    private static final String TRIALS = "trials";
    private static final String VERBOSE = "verbose";

    public static void main(String[] args) throws Exception {

        Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");

        final CommandLine commandLine = parseCommandLine(args);

        final String user = commandLine.getOptionValue(USER);
        final String password = commandLine.getOptionValue(PASSWORD);
        final String server = commandLine.getOptionValue(SERVER, DEFAULT_SERVER);
        final String url = String.format("jdbc:xmla:Server=%s", server);
        final int trials = Integer.parseInt(commandLine.getOptionValue(TRIALS, "1"));
        final boolean verbose = Boolean.parseBoolean(commandLine.getOptionValue(VERBOSE, "false"));

        try (final Connection connection = DriverManager.getConnection(url, user, password)) {

            final OlapConnection olapConnection = connection.unwrap(OlapConnection.class);

            try (final OlapStatement statement = olapConnection.createStatement()) {

                final DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();
                final List<Long> times = new ArrayList<>();

                for (int trial = 1; trial <= trials; trial++) {
                    final String query = loadMDX(commandLine);

                    final PrintWriter writer = new PrintWriter(System.out);

                    final long start = System.currentTimeMillis();

                    try (final CellSet cellSet = statement.executeOlapQuery(query)) {
                        if (trial == 1 || verbose) {
                            new RectangularCellSetFormatter(false).format(cellSet, writer);

                            // For cases where the output just dwarfs the console
                            final File outputFile = File.createTempFile("mdx", ".txt");

                            try (PrintWriter fileWriter = new PrintWriter(new FileWriter(outputFile))) {
                                new RectangularCellSetFormatter(false).format(cellSet, fileWriter);
                                writer.println("Output also written to: " + outputFile.getAbsolutePath());
                            }
                        }
                    }

                    final long timeTaken = System.currentTimeMillis() - start;

                    statistics.accept(timeTaken);
                    times.add(timeTaken);

                    if (trials > 1) {
                        writer.println(String.format("%d: %s ms [%s]", trial, timeTaken, LocalTime.now()));
                        if (trial % 10 == 0) {
                            logStats(statistics, times);
                        }
                    }
                    else {
                        writer.println(String.format("Time taken: %s ms", timeTaken));
                    }

                    writer.flush();
                }

                if (trials > 1) {
                    logStats(statistics, times);
                }
            }

        }
    }

    private static void logStats(DoubleSummaryStatistics statistics, List<Long> times) {
        final double averageTime = statistics.getAverage();
        final double minTime = statistics.getMin();
        final double maxTime = statistics.getMax();

        final double variance =
                times.stream()
                        .mapToDouble(value -> value - averageTime)
                        .map(x -> x * x)
                        .summaryStatistics()
                        .getAverage();

        final double stdDev = Math.sqrt(variance);

        Collections.sort(times);
        final int medianLocation = times.size() / 2;
        final long medianTime = times.get(medianLocation);

        final double filteredAverage =
                times.stream()
                        .mapToDouble(Long::doubleValue)
                        .filter(value -> Math.abs(value - averageTime) < 2 * stdDev)
                        .summaryStatistics()
                        .getAverage();

        System.out.println("==============================================");
        System.out.println(String.format("Trials:   %s", times.size()));
        System.out.println(String.format("Median:   %s ms (%s trials)", medianTime, times.size()));
        System.out.println(String.format("Average:  %.2f ms", averageTime));
        System.out.println(String.format("Average*: %.2f ms *excluding outliers", filteredAverage));
        System.out.println(String.format("Std Dev:  %.2f ms", stdDev));
        System.out.println(String.format("Min time: %.2f ms", minTime));
        System.out.println(String.format("Max time: %.2f ms", maxTime));
        System.out.println("==============================================");
    }

    private static CommandLine parseCommandLine(String[] args) throws ParseException {
        final Options options = new Options();
        options.addOption(Option.builder().required().longOpt(USER).hasArg().desc("Username").build());
        options.addOption(Option.builder().required().longOpt(PASSWORD).hasArg().desc("Password").build());
        options.addOption(Option.builder().longOpt(SERVER).hasArg().desc("Server URL").build());
        options.addOption(Option.builder().required().longOpt(FILE).hasArg().desc("path to MDX file").build());
        options.addOption(Option.builder().longOpt(TRIALS).hasArg().desc("Number of trials (for timing").build());
        options.addOption(Option.builder().longOpt(VERBOSE).hasArg().desc("Verbose mode (show results for each trial").build());

        final DefaultParser parser = new DefaultParser();

        return parser.parse(options, args);
    }

    private static String loadMDX(CommandLine commandLine) throws IOException {
        final String file = commandLine.getOptionValue(FILE);
        try (final FileReader fileReader = new FileReader(file)) {
            return IOUtils.toString(fileReader);
        }
    }

}
