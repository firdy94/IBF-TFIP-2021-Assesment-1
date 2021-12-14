package com.ibftfip2021;

import org.apache.commons.cli.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Hello world!
 *
 */
public class Main {

    private static int portNum = 3000;
    private static List<String> pathInputs = new ArrayList<>();
    private static String currentPath = new File("").getAbsolutePath();

    public static void main(String[] args) {

        Options options = new Options();

        Option portInput = Option.builder("p").longOpt("port").desc("<port number>").build();
        portInput.setRequired(false);
        options.addOption(portInput);

        Option pathInput = Option.builder("d").longOpt("docRoot").desc("<colon delimited list of directories").valueSeparator(':').build();
        pathInput.setRequired(false);
        options.addOption(pathInput);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("port")) {
                portNum = Integer.parseInt(cmd.getOptionValue("port"));
            }
            if (cmd.hasOption("docRoot")) {
                for (String path : cmd.getOptionValues("docRoot")) {
                    String fullPath = Paths.get(currentPath, path).toString();
                    pathInputs.add(fullPath);

                }
            }
            else {
                String fullPath = Paths.get(currentPath, "static").toString();
                pathInputs.add(fullPath);

            }
            
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        HTTPServer myServer = new HTTPServer(portNum, pathInputs);
        myServer.startServer(portNum, inputPath);
        myServer.startServer();

    }
}
