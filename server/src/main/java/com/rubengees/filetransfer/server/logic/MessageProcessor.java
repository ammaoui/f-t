package com.rubengees.filetransfer.server.logic;

import com.rubengees.filetransfer.server.logic.util.FileReader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;

import static com.rubengees.filetransfer.core.Protocol.*;

/**
 * Todo: Describe class and find better name :D
 *
 * @author Ruben Gees
 */
public class MessageProcessor {

    public static String processMessage(String clientIp, int clientPort, ConnectionStatus status, String message) {
        if (message.startsWith(INIT)) {
            if (status == null) {
                String[] split = message.split(";");

                if (split.length == 3) {
                    try {
                        String fileName = split[2];
                        int chunkSize = Integer.parseInt(split[1]);

                        if (fileName.isEmpty()) {
                            return ERROR + "The filename must not be empty.";
                        }

                        if (chunkSize <= 6) {
                            return ERROR + "The size of the chunk has to be a number which is greater then 6.";
                        }

                        status = new ConnectionStatus(fileName, chunkSize);
                        try {
                            String data = FileReader.readFile(Paths.get(ServerState.getInstance().getDirectory()
                                    + "/" + fileName));

                            if (data.isEmpty()) {
                                return ERROR + "The file is empty";
                            } else {
                                status.setData(data);
                                ServerState.getInstance().addConnectionInfo(clientIp, clientPort, status);

                                return OK;
                            }
                        } catch (IOException | UncheckedIOException e) {
                            return ERROR + "The file didn't exist or an other error occurred.";
                        }
                    } catch (NumberFormatException e) {
                        return ERROR + "The specified argument for chunk size was not a valid number.";
                    }
                } else {
                    return ERROR + "The call to init doesn't have the right amount of arguments.";
                }
            } else {
                return ERROR + "Called init after initialization already performed.";
            }
        } else if (message.startsWith(GET)) {
            if (status == null) {
                return ERROR + "Init was not called.";
            } else {
                if (status.isEndReached()) {
                    ServerState.getInstance().removeConnectionInfo(clientIp, clientPort);

                    return FINISH;
                } else {
                    String result = DATA + status.getNextData();
                    status.incrementDataPosition();

                    return result;
                }
            }
        } else {
            return ERROR + "Not an allowed command.";
        }
    }

}
