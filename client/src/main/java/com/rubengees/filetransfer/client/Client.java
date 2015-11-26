package com.rubengees.filetransfer.client;

import java.io.IOException;

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
public interface Client {

    void connect(String ip, int port) throws IOException;

    String getFile(String fileName) throws IOException;

}
