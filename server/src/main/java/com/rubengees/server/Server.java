package com.rubengees.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
public class Server {

    private InetAddress host;
    private int port;
    private boolean tcp;

    public Server(int port, boolean tcp) throws UnknownHostException {
        this.host = InetAddress.getLocalHost();
        this.port = port;
        this.tcp = tcp;
    }

    public Server(int port) throws UnknownHostException {
        this(port, true);
    }

    public Server() throws UnknownHostException {
        this(8999, true);
    }

}
