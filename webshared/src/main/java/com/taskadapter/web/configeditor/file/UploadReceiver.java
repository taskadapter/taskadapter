package com.taskadapter.web.configeditor.file;

import com.taskadapter.web.FileManager;
import com.vaadin.ui.Upload;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
* Author: Alexander Kulik
*/
public class UploadReceiver implements Upload.Receiver {

    private String fileName;
    private boolean sleep;
    private int total;
    private byte[] bytes = new byte[ServerModeFilePanel2.MAX_FILE_SIZE_BYTES];

    public OutputStream receiveUpload(String filename, String mimeType) {
        total = 0;
        fileName = filename;
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                bytes[total++] = (byte) b;
                if (sleep && total % 10000 == 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (total == ServerModelFilePanelPresenter.MAX_FILE_SIZE_BYTES) {
                    throw new RuntimeException("Max file size reached: " + ServerModeFilePanel2.MAX_FILE_SIZE_BYTES + " bytes");
                }
            }
        };
    }

    public String getFileName() {
        return fileName;
    }

    public void setSlow(boolean value) {
        sleep = value;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, total);
    }

    public boolean saveFile(String userName) {
        try {
            new FileManager().saveFileOnServer(userName, getFileName(), getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
