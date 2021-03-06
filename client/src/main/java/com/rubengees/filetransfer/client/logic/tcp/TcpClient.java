package com.rubengees.filetransfer.client.logic.tcp;

import com.rubengees.filetransfer.client.logic.Client;
import com.rubengees.filetransfer.client.logic.ProgressListener;
import com.rubengees.filetransfer.core.TcpConnection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.rubengees.filetransfer.core.Protocol.*;

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
public class TcpClient implements Client {

    private TcpConnection connection;

    @Override
    public void connect(String ip, int port) throws IOException {
        connection = new TcpConnection(ip, port);
    }

    @Override
    public void disconnect() {
        connection.close();
    }

    @Override
    public String getFile(String fileName, ProgressListener listener) throws IOException {
        String current;

        connection.send(INIT + CHUNK_SIZE + ";" + fileName);
        current = connection.receive();

        if (current.equals(OK)) {
            BufferedWriter writer = null;

            try {
                writer = Files.newBufferedWriter(Paths.get(fileName));
                do {
                    connection.send(GET);

                    current = connection.receive();

                    if (current.startsWith(DATA)) {
                        writer.write(current.substring(5, current.length()));

                        if (listener != null) {
                            listener.onProgress();
                        }
                    } else if (!current.equals(FINISH)) {
                        writer.flush();
                        writer.close();

                        if (current.startsWith(ERROR)) {
                            return current.substring(6, current.length());
                        } else {
                            return "Unknown";
                        }
                    }
                } while (!current.equals(FINISH));

                writer.flush();
                writer.close();
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } else if (current.startsWith(ERROR)) {
            return current.substring(6, current.length());
        } else {
            return "Unknown";
        }

        return null;
    }

}
