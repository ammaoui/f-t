package com.rubengees.filetransfer.client;

import com.rubengees.filetransfer.client.logic.Client;
import com.rubengees.filetransfer.client.logic.tcp.TcpClient;
import com.rubengees.filetransfer.client.logic.util.Pair;
import com.rubengees.filetransfer.client.logic.util.Validator;
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

    private static Client client;

    public static void main(String[] args) {
        boolean success = false;

        try {
            setupClient(args);
        } catch (ParseException ignored) {
            System.exit(-1);
        }

        while (true) {
            try {
                connect();

                success = true;
            } catch (IOException e) {
                System.out.println("An error occurred while connecting: " + e.getMessage());

                try {
                    if (readNextCommand(new Pair<>("e", "exit"), new Pair<>("r", "retry")).equalsIgnoreCase("e")) {
                        System.exit(0);
                    }
                } catch (IOException ignored) {

                }
            }

            if (success) {
                while (true) {
                    try {
                        download();
                    } catch (IOException e) {
                        System.out.println("An error occurred while downloading: " + e.getMessage());

                        break;
                    }

                    try {
                        String nextCommand = readNextCommand(new Pair<>("e", "exit"), new Pair<>("d", "disconnect"),
                                new Pair<>("n", "download another file"));

                        if (nextCommand.equalsIgnoreCase("e")) {
                            System.exit(0);
                        } else if (nextCommand.equalsIgnoreCase("d")) {
                            client.disconnect();

                            break;
                        }
                    } catch (IOException ignored) {

                    }
                }
            }
        }
    }

    private static void setupClient(String[] args) throws ParseException {
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

            if (cmd.hasOption("h") || cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();

                formatter.printHelp("file-transfer-server", options);
            } else {
                if (cmd.hasOption("u") || cmd.hasOption("udp")) {
                    useTcp = false;
                }
            }

            if (useTcp) {
                client = new TcpClient();
            } else {
                client = null;
            }
        } catch (ParseException e) {
            System.out.println("An error occurred while parsing the command line arguments. See the help. (-h)");

            throw e;
        }
    }

    private static void connect() throws IOException {
        client.connect(readHost(), readPort());
    }

    private static void download() throws IOException {
        String error = client.getFile(readFileName(), () -> {
            System.out.print(".");
        });

        System.out.println();

        if (error != null) {
            System.out.println("An error occurred while downloading the file: " + error);
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
                    System.out.println("The port has to be a number larger than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("The port has to be a number larger than 0.");
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

    @SafeVarargs
    private static String readNextCommand(Pair<String, String>... commands) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Next command?");
            for (Pair<String, String> command : commands) {
                System.out.println(command.getFirst() + " - " + command.getSecond());
            }

            String nextCommand = reader.readLine();

            if (nextCommand.isEmpty()) {
                System.out.println("The command cannot be empty.");
            } else {
                for (Pair<String, String> command : commands) {
                    if (command.getFirst().equalsIgnoreCase(nextCommand)) {
                        return nextCommand;
                    }
                }

                System.out.println("The command must be one of the available ones.");
            }
        }
    }
}
