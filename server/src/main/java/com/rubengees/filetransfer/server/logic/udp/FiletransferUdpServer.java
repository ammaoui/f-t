package com.rubengees.filetransfer.server.logic.udp;

import com.rubengees.filetransfer.server.logic.ConnectionStatus;
import com.rubengees.filetransfer.server.logic.MessageProcessor;
import com.rubengees.filetransfer.server.logic.ServerState;

import java.io.IOException;

/**
 * Todo: Describe class
 *
 * @author Ruben Gees
 */
public class FiletransferUdpServer extends AbstractUdpServer {

    public FiletransferUdpServer(int port, String directory) {
        super(port);

        ServerState.getInstance().setDirectory(directory);
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
