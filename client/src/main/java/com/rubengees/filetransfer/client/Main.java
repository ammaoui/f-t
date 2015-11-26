package com.rubengees.filetransfer.client;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        Option tcp = Option.builder("t").hasArg(false).longOpt("tcp")
                .desc("Make this client use the tcp protocol. (default)").build();
        Option udp = (Option.builder("u").hasArg(false).longOpt("udp")
                .desc("Make this client use the udp protocol.").build());
        Option help = Option.builder("h").hasArg(false).longOpt("help").desc("Print this message.").build();

        options.addOption(tcp);
        options.addOption(udp);
        options.addOption(help);

        try {
            CommandLine cmd = new DefaultParser().parse(options, args);
            boolean useTcp = true;
            int portToUse = 8999;

            if (cmd.hasOption("h") || cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();

                formatter.printHelp("file-transfer-server", options);
            } else {
                if (cmd.hasOption("u") || cmd.hasOption("udp")) {
                    useTcp = false;
                }

                Client client;

                if (useTcp) {
                    client = new TcpClient();
                } else {
                    client = null;
                }

                client.connect(readHost(), readPort());
                client.getFile(readFileName());
            }
        } catch (ParseException e) {
            System.out.println("An error occurred while parsing the command line arguments. See the help. (-h)");
        }
    }

    private static String readHost() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("Host to connect to: ");
            String host = reader.readLine();

            if (host.equals("localhost") || Validator.validateIP(host)) {
                return host;
            } else {
                System.out.println("The entered ip were not in the correct format.");
            }
        }
    }

    private static int readPort() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("Port to connect to: ");
            String port = reader.readLine();

            try {
                int portAsInt = Integer.parseInt(port);

                if (portAsInt > 0) {
                    return portAsInt;
                } else {
                    System.out.println("The post has to be a number larger than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("The post has to be a number larger than 0.");
            }
        }
    }

    private static String readFileName() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("File to download: ");
            String fileName = reader.readLine();

            if (fileName.isEmpty()) {
                System.out.println("The filename cannot be empty.");
            } else {
                return fileName;
            }
        }
    }

}
