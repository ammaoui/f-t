package com.rubengees.filetransfer.server.logic;

import java.util.HashMap;

/**
 * Todo: Describe class
 *
 * @author Ruben Gees
 */
public class ServerState {
    private static ServerState ourInstance = new ServerState();
    private HashMap<String, ConnectionStatus> connectionMap = new HashMap<>();
    private String directory = ".";

    private ServerState() {
    }

    public static ServerState getInstance() {
        return ourInstance;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void addConnectionInfo(String ip, int port, ConnectionStatus status) {
        connectionMap.put(ip + "." + port, status);
    }

    public void removeConnectionInfo(String ip, int port) {
        connectionMap.remove(ip + "." + port);
    }

    public ConnectionStatus getConnectionInfo(String ip, int port) {
        String key = ip + "." + port;

        if (connectionMap.containsKey(key)) {
            return connectionMap.get(ip + "." + port);
        } else {
            return null;
        }
    }
}
