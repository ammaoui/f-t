package com.rubengees.filetransfer.server.logic.tcp;

import com.rubengees.filetransfer.core.TcpConnection;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTcpServer implements Closeable {

    private ServerSocket serverSocket;
    private Thread TcpConnectionHandler;
    private List<Thread> handlerList = new ArrayList<>();
    private List<TcpConnection> connectionList = new ArrayList<>();

    public AbstractTcpServer(int port) {
        try {
            serverSocket = new ServerSocket(port);

            createTcpConnectionHandler();
        } catch (Exception ignored) {

        }
    }

    private void createTcpConnectionHandler() {
        TcpConnectionHandler = new Thread() {
            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    try {
                        handleNewTcpConnection(serverSocket.accept());
                    } catch (IOException ignored) {

                    }
                }
            }
        };

        TcpConnectionHandler.start();
    }

    private void handleNewTcpConnection(Socket socket) throws IOException {
        final TcpConnection connection = new TcpConnection(socket);
        Thread handler = new Thread() {
            @Override
            public void run() {
                String message = "";

                processNewConnection(connection.getRemoteIP(), connection.getRemotePort());

                while (!this.isInterrupted() && message != null) {
                    try {
                        message = connection.receive();
                    } catch (IOException e) {
                        message = null;
                    }

                    if (message != null) {
                        processMessage(connection.getRemoteIP(), connection.getRemotePort(), message);
                    } else {
                        closeConnection(connection.getRemoteIP(), connection.getRemotePort());
                    }
                }
            }
        };

        handler.start();
        connectionList.add(connection);
        handlerList.add(handler);
    }

    protected final void send(String clientIP, int clientPort, String message) throws IOException {
        for (TcpConnection connection : connectionList) {
            if (connection.getRemoteIP().equals(clientIP)
                    && connection.getRemotePort() == clientPort) {
                connection.send(message);

                return;
            }
        }
    }

    protected final void sendToAll(String message) throws IOException {
        for (TcpConnection connection : connectionList) {
            connection.send(message);
        }
    }

    protected final void closeConnection(String clientIP, int clientPort) {
        for (int i = 0; i < connectionList.size(); i++) {
            TcpConnection connection = connectionList.get(i);

            if (connection.getRemoteIP().equals(clientIP)
                    && connection.getRemotePort() == clientPort) {
                processClosedConnection(connection.getRemoteIP(), connection.getRemotePort());
                handlerList.get(i).interrupt();
                handlerList.remove(i);
                connection.close();
                connectionList.remove(i);
                return;
            }
        }

        System.err.println("ERROR: Client " + clientIP + " not found!");
    }

    @Override
    public final void close() {
        try {
            TcpConnectionHandler.interrupt();
            serverSocket.close();
        } catch (IOException ignored) {

        }

        for (int i = 0; i < connectionList.size(); i++) {
            TcpConnection connection = connectionList.get(i);

            handlerList.get(i).interrupt();
            handlerList.remove(i);
            connection.close();
            connectionList.remove(i);
        }
    }

    protected abstract void processNewConnection(String clientIP, int clientPort);

    protected abstract void processClosedConnection(String clientIP, int clientPort);

    protected abstract void processMessage(String clientIP, int clientPort, String message);
}
