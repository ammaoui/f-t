package com.rubengees.filetransfer.server.logic.udp;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Todo: Describe class
 *
 * @author Ruben Gees
 */
public abstract class AbstractUdpServer implements Closeable {

    private DatagramSocket serverSocket;
    private DatagramPacket in;
    private Thread messageHandler;

    public AbstractUdpServer(int port) {
        try {
            serverSocket = new DatagramSocket(port);
            in = new DatagramPacket(new byte[128], 128);

            createUdpMessageHandler();
        } catch (SocketException ignored) {

        }
    }

    private void createUdpMessageHandler() {
        messageHandler = new Thread() {
            @Override
            public void run() {
                try {
                    serverSocket.receive(in);

                    processMessage(in.getAddress().getHostAddress(), in.getPort(), Arrays.toString(in.getData()));
                } catch (IOException ignored) {

                }
            }
        };

        messageHandler.start();
    }

    @Override
    public void close() {
        if (serverSocket != null) {
            serverSocket.close();
        }

        if (messageHandler != null) {
            messageHandler.interrupt();
        }
    }

    protected final void send(String clientIP, int clientPort, String message) throws IOException {
        byte[] messageBytes = message.getBytes();
        DatagramPacket out = new DatagramPacket(messageBytes, messageBytes.length, InetAddress.getByName(clientIP),
                clientPort);

        serverSocket.send(out);
    }

    protected abstract void processMessage(String clientIP, int clientPort, String message);
}
