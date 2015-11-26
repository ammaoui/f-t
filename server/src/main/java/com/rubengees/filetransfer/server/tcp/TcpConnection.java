package com.rubengees.filetransfer.server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpConnection extends Thread {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String serverName;
    private int port;

    public TcpConnection(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;

        connect();
    }

    public TcpConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        port = this.clientSocket.getLocalPort();

        try {
            out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            clientSocket = new Socket(serverName, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {

        }
    }

    public String receive() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public void send(String message) {
        out.println(message);
        out.flush();
    }

    public boolean isConnected() {
        return (clientSocket != null);
    }

    public boolean isClosed() {
        return clientSocket.isClosed();
    }

    public String getRemoteIP() {
        return "" + clientSocket.getInetAddress();
    }

    public String getLocalIP() {
        return "" + clientSocket.getLocalAddress();
    }

    public int getRemotePort() {
        return clientSocket.getPort();
    }

    public int getLocalPort() {
        return clientSocket.getLocalPort();
    }

    public void close() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

