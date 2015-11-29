package com.rubengees.filetransfer.server.logic;

/**
 * Todo: Describe class
 *
 * @author Ruben Gees
 */
public class ConnectionStatus {

    private String fileName;
    private int chunkSize;
    private int dataPosition;
    private String data;

    public ConnectionStatus(String fileName, int chunkSize) {
        this.fileName = fileName;
        this.chunkSize = chunkSize;
        this.dataPosition = 0;
    }

    public String getFileName() {
        return fileName;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getDataPosition() {
        return dataPosition;
    }

    public void incrementDataPosition() {
        dataPosition += chunkSize;
    }

    public boolean isEndReached() {
        return dataPosition >= data.length();
    }

    public String getNextData() {
        int nextPosition = dataPosition + chunkSize;

        return data.substring(dataPosition,
                nextPosition >= data.length() ? data.length() : nextPosition);
    }

    public void setData(String data) {
        this.data = data;
    }
}
