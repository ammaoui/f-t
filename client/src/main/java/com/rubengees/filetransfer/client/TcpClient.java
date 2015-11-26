package com.rubengees.filetransfer.client;

import com.rubengees.filetransfer.core.TcpConnection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
public class TcpClient implements Client {

    public static final int CHUNK_SIZE = 1;
    private TcpConnection connection;

    @Override
    public void connect(String ip, int port) throws IOException {
        connection = new TcpConnection(ip, port);
    }

    @Override
    public String getFile(String fileName) throws IOException {
        String current;

        connection.send("INITX;" + CHUNK_SIZE + ";" + fileName);
        current = connection.receive();

        if (current.equals("OK")) {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));
            do {
                connection.send("GET");

                current = connection.receive();

                if (current.startsWith("DATA;")) {
                    writer.write(current.substring(5, current.length()));
                } else if (!current.equals("FINISH")) {
                    writer.flush();
                    writer.close();

                    if (current.startsWith("ERROR;")) {
                        return current.substring(6, current.length());
                    } else {
                        return "Unknown";
                    }
                }
            } while (!current.equals("FINISH"));

            writer.flush();
            writer.close();
        } else if (current.startsWith("ERROR;")) {
            return current.substring(6, current.length());
        } else {
            return "Unknown";
        }

        return null;
    }

}
