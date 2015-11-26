package com.rubengees.filetransfer.server.tcp;

import com.rubengees.filetransfer.server.ConnectionStatus;

import java.util.HashMap;

/**
 * Todo: Describe class
 *
 * @author Ruben Gees
 */
public class FiletransferTcpServer extends AbstractTcpServer {

    private String directory;
    private HashMap<String, ConnectionStatus> connectionMap = new HashMap<>();

    public FiletransferTcpServer(int port, String directory) {
        super(port);

        this.directory = directory;
    }

    @Override
    protected void processNewConnection(String clientIP, int clientPort) {
        connectionMap.put(clientIP + "." + clientIP, null);
    }

    @Override
    protected void processClosedConnection(String clientIP, int clientPort) {
        connectionMap.remove(clientIP + "." + clientPort);
    }

    @Override
    protected void processMessage(String clientIP, int clientPort, String message) {
        ConnectionStatus status = connectionMap.get(clientIP + "." + clientPort);

        if (message.startsWith("INITX")) {
            if (status == null) {
                String[] split = message.split(";");

                if (split.length == 3) {
                    try {
                        String fileName = split[2];
                        int chunkSize = Integer.parseInt(split[1]);

                        if (fileName.isEmpty()) {
                            sendError(clientIP, clientPort, "The filename must not be empty.");

                            return;
                        }

                        if (chunkSize <= 0) {
                            sendError(clientIP, clientPort,
                                    "The size of the chunk has to be a number which is greater then 0.");

                            return;
                        }

                        connectionMap.put(clientIP + "." + clientPort, new ConnectionStatus(fileName, chunkSize));

                        send(clientIP, clientPort, "OK");
                    } catch (NumberFormatException e) {
                        sendError(clientIP, clientPort,
                                "The specified argument for chunk size was not a valid number.");
                    }
                } else {
                    sendError(clientIP, clientPort, "The call to init doesn't have the right amount of arguments.");
                }
            } else {
                sendError(clientIP, clientPort, "Called init after initialization already performed.");
            }
        } else {
            sendError(clientIP, clientPort, "Not an allowed command.");
        }
    }

    private void sendError(String clientIP, int clientPort, String message) {
        send(clientIP, clientPort, "ERROR;" + message);
    }
}
