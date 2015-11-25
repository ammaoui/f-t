package com.rubengees.filetransfer.server;

import java.util.HashMap;

/**
 * Todo: Describe class
 *
 * @author Ruben Gees
 */
public class FiletransferTcpServer extends AbstractTcpServer {

    private HashMap<String, ConnectionStatus> connectionMap = new HashMap<>();

    public FiletransferTcpServer(int port) {
        super(port);
    }

    @Override
    protected void processNewConnection(String clientIP, int clientPort) {
        connectionMap.put(clientIP + "." + clientIP, new ConnectionStatus());
    }

    @Override
    protected void processClosedConnection(String clientIP, int clientPort) {
        connectionMap.remove(clientIP + "." + clientPort);
    }

    @Override
    protected void processMessage(String clientIP, int clientPort, String message) {

    }
}
