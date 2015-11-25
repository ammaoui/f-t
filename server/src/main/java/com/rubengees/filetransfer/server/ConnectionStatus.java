package com.rubengees.filetransfer.server;

/**
 * Todo: Describe class
 *
 * @author Ruben Gees
 */
public class ConnectionStatus {

    private String filePath;
    private int chunkSize;
    private int dataPosition;
    private String data;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getDataPosition() {
        return dataPosition;
    }

    public void setDataPosition(int dataPosition) {
        this.dataPosition = dataPosition;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
