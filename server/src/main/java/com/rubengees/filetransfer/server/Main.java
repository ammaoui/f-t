package com.rubengees.filetransfer.server;

import com.rubengees.filetransfer.server.logic.tcp.FiletransferTcpServer;
import com.rubengees.filetransfer.server.logic.udp.FiletransferUdpServer;
import org.apache.commons.cli.*;

import java.io.Closeable;

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
public class Main {

    public static void main(String[] args) {
        Options options = new Options();
        Option tcp = Option.builder("t").hasArg(false).longOpt("tcp")
                .desc("Make this server use the tcp protocol. (default)").build();
        Option udp = (Option.builder("u").hasArg(false).longOpt("udp")
                .desc("Make this server use the udp protocol.").build());
        Option port = Option.builder("p").hasArg().argName("port").longOpt("port")
                .desc("make this server use the specified port.").build();
        Option directory = Option.builder("d").hasArg().argName("directory").longOpt("directory")
                .desc("Set the working directory..").build();
        Option help = Option.builder("h").hasArg(false).longOpt("help").desc("Print this message.").build();

        options.addOption(tcp);
        options.addOption(udp);
        options.addOption(port);
        options.addOption(directory);
        options.addOption(help);

        try {
            CommandLine cmd = new DefaultParser().parse(options, args);
            boolean useTcp = true;
            int portToUse = 8999;
            String directoryToUse = "";

            if (cmd.hasOption("h") || cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();

                formatter.printHelp("file-transfer-server", options);
            } else {
                if (cmd.hasOption("u") || cmd.hasOption("udp")) {
                    useTcp = false;
                }

                if (cmd.hasOption("d")) {
                    directoryToUse = cmd.getOptionValue("d");
                } else if (cmd.hasOption("directory")) {
                    directoryToUse = cmd.getOptionValue("directory");
                }

                try {
                    if (cmd.hasOption("p")) {
                        portToUse = Integer.parseInt(cmd.getOptionValue("p"));
                    } else if (cmd.hasOption("port")) {
                        portToUse = Integer.parseInt(cmd.getOptionValue("port"));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("The parameter for the port was not valid," +
                            " the default port (8999) will be used.");
                }

                Closeable server;

                if (useTcp) {
                    server = new FiletransferTcpServer(portToUse, directoryToUse);
                } else {
                    server = new FiletransferUdpServer(portToUse, directoryToUse);
                }

            }
        } catch (ParseException e) {
            System.out.println("An error occurred while parsing the command line arguments. See the help. (-h)");
        }
    }

}
