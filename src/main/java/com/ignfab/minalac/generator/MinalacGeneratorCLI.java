package com.ignfab.minalac.generator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A command line parser and basic processor.
 * MinalacGeneratorCLI performs parsing, basic validation and some basic tasks such as retrieving generation parameters.
 */
public class MinalacGeneratorCLI {

    private static final String PARAMS_ENVVAR_NAME = "MINALAC_PARAMS";

    private Path outputPath;
    private Path parametersPath;

    private boolean generationDisabled;
    private boolean saveDisabled;

    private final Options options;

    /**
     * Creates a new GeneratorCommandLine.
     */
    public MinalacGeneratorCLI() {
        options = new Options();
        options.addOption(new Option("h", "help", false, "Display command usage"));
        options.addOption(new Option("p", "param-file", true, "Get generation params from file"));
        options.addOption(new Option(null, "generation-disabled", false, "Stop before starting generation, after parameters parsed"));
        options.addOption(new Option(null, "save-disabled", false, "Stop before saving output file, after generation done"));
    }

    /**
     * Parses given arguments.
     *
     * @param args Argument array (usually those from main method)
     */
    public void parse(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            usage();
            System.exit(1);
            return; // Must please linter with uninitialized cmd
        }

        if (cmd.hasOption("h")) {
            usage();
            System.exit(0);
        }

        if (cmd.hasOption("p")) {
            try {
                parametersPath = Paths.get(cmd.getOptionValue("p"));
            } catch (InvalidPathException e) {
                System.out.println("Invalid parameters file path");
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }

        generationDisabled = cmd.hasOption("--generation-disabled");
        saveDisabled = cmd.hasOption("--save-disabled");

        if (cmd.getArgs().length != 1) {
            System.out.println("Please provide output path");
            usage();
            System.exit(1);
        }

        try {
            outputPath = Paths.get(cmd.getArgs()[0]);
        } catch (InvalidPathException e) {
            System.out.println("Invalid output path");
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Prints command line usage.
     */
    public void usage() {
        HelpFormatter helper = new HelpFormatter();
        helper.printHelp("[-h] [-p <parametersFilePath>] <outputPath>", options);
    }

    /**
     * Reads generation parameters from where they are (file or environment variable).
     *
     * @return Content of parameters file/variable
     */
    public String readParameters() {
        String parameters = null; // Must please linter with uninitialized parameters

        if (parametersPath != null) {
            if (!Files.exists(parametersPath)) {
                System.out.printf("%s does not exist%n", parametersPath);
                System.exit(1);
            }

            if (!Files.isRegularFile(parametersPath)) {
                System.out.printf("%s is not a regular file%n", parametersPath);
                System.exit(1);
            }

            if (!Files.isReadable(parametersPath)) {
                System.out.printf("%s is not readable%n", parametersPath);
                System.exit(1);
            }

            try {
                parameters = Files.readString(parametersPath);
            } catch (IOException e) {
                System.out.printf("Error reading parameters from %s%n", parametersPath);
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            parameters = System.getenv(PARAMS_ENVVAR_NAME);
        }

        if (parameters == null) {
            System.out.printf("Please provide generation parameters (either with -p option or using %s environment variable)%n", PARAMS_ENVVAR_NAME);
            usage();
            System.exit(1);
        }
        return parameters;
    }

    /**
     * Returns output path.
     * Allows saving the result of the generator.
     *
     * @return output path
     */
    public Path outputPath() {
        return outputPath;
    }

    /**
     * Returns generation disabled.
     * Allows stopping execution when the generator finishes deserializing the parameters.
     *
     * @return generation disabled flag
     */
    public boolean generationDisabled() {
        return generationDisabled;
    }

    /**
     * Returns save disabled.
     * Allows stopping execution after the game map generation.
     *
     * @return save disabled flag
     */
    public boolean saveDisabled() {
        return saveDisabled;
    }
}
