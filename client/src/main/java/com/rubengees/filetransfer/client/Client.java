package com.rubengees.filetransfer.client;

import java.io.IOException;

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
public interface Client {

    int CHUNK_SIZE = 256;

    void connect(String ip, int port) throws IOException;

    void disconnect();

    String getFile(String fileName, ProgressListener listener) throws IOException;

}
