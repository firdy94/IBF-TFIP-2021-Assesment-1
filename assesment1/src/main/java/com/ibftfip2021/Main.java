package com.ibftfip2021;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Hello world!
 *
 */
public class Main {

    private static int portNum = 3000;
    private static HashMap<String, String> inputFlags = new HashMap<>();
    private static List<String> pathInputs = new ArrayList<>();
    private static String currentPath = new File("").getAbsolutePath();

    public static void main(String[] args) {

        List<String> userInputs = Arrays.asList(args);

        for (String arg : userInputs) {
            if (arg.equals("--port")) {
                int argValueIndex = userInputs.indexOf(arg) + 1;
                portNum = Integer.parseInt(userInputs.get(argValueIndex));
            } else if (arg.equals("--docRoot")) {
                int argValueIndex = userInputs.indexOf(arg) + 1;
                String paths = userInputs.get(argValueIndex);
                if (paths.contains(":")) {
                    List<String> sepPaths = Arrays.asList(paths.split(":"));
                    for (String path : sepPaths) {
                        String fullPath = Paths.get(currentPath, path).toString();
                        pathInputs.add(fullPath);
                    }
                } else {
                    String fullPath = Paths.get(currentPath, paths).toString();
                    pathInputs.add(fullPath);
                }
            } else {
                String fullPath = Paths.get(currentPath, "static").toString();
                pathInputs.add(fullPath);

            }
        }

        // Options options = new Options();

        // Option portInput = Option.builder("p").longOpt("port").desc("<port
        // number>").build();
        // portInput.setRequired(false);
        // options.addOption(portInput);

        // Option pathInput = Option.builder("d").longOpt("docRoot").desc("<colon
        // delimited list of directories")
        // .valueSeparator(':').build();
        // pathInput.setRequired(false);
        // options.addOption(pathInput);

        // CommandLineParser parser = new DefaultParser();
        // try {
        // CommandLine cmd = parser.parse(options, args);
        // if (cmd.hasOption("port")) {
        // portNum = Integer.parseInt(cmd.getOptionValue("port"));
        // }
        // if (cmd.hasOption("docRoot")) {
        // for (String path : cmd.getOptionValues("docRoot")) {
        // String fullPath = Paths.get(currentPath, path).toString();
        // pathInputs.add(fullPath);

        // }
        // } else {
        // String fullPath = Paths.get(currentPath, "static").toString();
        // pathInputs.add(fullPath);

        // }

        // } catch (ParseException e) {
        // e.printStackTrace();
        // }

        HTTPServer myServer = new HTTPServer(portNum, pathInputs);
        try {
            myServer.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
