package com.rubengees.filetransfer.client.logic.udp;

import com.rubengees.filetransfer.client.logic.Client;
import com.rubengees.filetransfer.client.logic.ProgressListener;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.rubengees.filetransfer.core.Protocol.*;

/**
 * Todo: Describe class
 *
 * @author Ruben Gees
 */
public class UdpClient implements Client {

    private DatagramSocket socket;
    private DatagramPacket out;

    @Override
    public void connect(String ip, int port) throws IOException {
        socket = new DatagramSocket();
        byte[] buffer = new byte[128];
        out = new DatagramPacket(buffer, 128, InetAddress.getByName(ip), port);
    }

    @Override
    public void disconnect() {
        if (socket != null) {
            socket.close();
        }

        out = null;
    }

    @Override
    public String getFile(String fileName, ProgressListener listener) throws IOException {
        String current;

        send(INIT + CHUNK_SIZE + ";" + fileName);
        current = receive(128);

        if (current.equals(OK)) {
            BufferedWriter writer = null;

            try {
                writer = Files.newBufferedWriter(Paths.get(fileName));
                do {
                    send(GET);
                    current = receive(CHUNK_SIZE);

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

    private void send(String message) throws IOException {
        out.setData(message.getBytes());
        socket.send(out);
    }

    private String receive(int length) throws IOException {
        DatagramPacket in = new DatagramPacket(new byte[length], length);
        socket.receive(in);

        return new String(in.getData()).trim();
    }
}
