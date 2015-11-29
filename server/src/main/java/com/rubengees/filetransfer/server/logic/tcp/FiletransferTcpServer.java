package com.rubengees.filetransfer.server.logic.tcp;

import com.rubengees.filetransfer.server.logic.ConnectionStatus;
import com.rubengees.filetransfer.server.logic.MessageProcessor;
import com.rubengees.filetransfer.server.logic.ServerState;

import java.io.IOException;

/**
 * Todo: Describe class
 *
 * @author Ruben Gees
 */
public class FiletransferTcpServer extends com.rubengees.filetransfer.server.logic.tcp.AbstractTcpServer {

    public FiletransferTcpServer(int port, String directory) {
        super(port);

        ServerState.getInstance().setDirectory(directory);
    }

    @Override
    protected void processNewConnection(String clientIP, int clientPort) {
        ServerState.getInstance().addConnectionInfo(clientIP, clientPort, null);

        System.out.println("New connection from:" + clientIP + ":" + clientPort + ".");
    }

    @Override
    protected void processClosedConnection(String clientIP, int clientPort) {
        ServerState.getInstance().removeConnectionInfo(clientIP, clientPort);

        System.out.println(clientIP + ":" + clientPort + " disconnected.");
    }

    @Override
    protected void processMessage(String clientIP, int clientPort, String message) {
        ConnectionStatus status = ServerState.getInstance().getConnectionInfo(clientIP, clientPort);

        try {
            send(clientIP, clientPort, MessageProcessor.processMessage(clientIP, clientPort, status, message));
        } catch (IOException e) {
            ServerState.getInstance().removeConnectionInfo(clientIP, clientPort);
        }
    }
}
