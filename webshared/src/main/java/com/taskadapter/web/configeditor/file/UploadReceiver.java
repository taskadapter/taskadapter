package com.taskadapter.web.configeditor.file;

import com.taskadapter.FileManager;
import com.vaadin.ui.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class UploadReceiver implements Upload.Receiver {
    private final Logger logger = LoggerFactory.getLogger(UploadReceiver.class);

    private String fileName;
    private boolean sleep;
    private int total;
    private byte[] bytes = new byte[ServerModeFilePanel.MAX_FILE_SIZE_BYTES];
    private FileManager fileManager = new FileManager();

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
                        logger.error("interrupted while peacefully sleeping. so rude!" + e.getMessage(), e);
                    }
                }
                if (total == ServerModelFilePanelPresenter.MAX_FILE_SIZE_BYTES) {
                    throw new RuntimeException("Max file size reached: " + ServerModeFilePanel.MAX_FILE_SIZE_BYTES + " bytes");
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
            fileManager.saveFileOnServer(userName, getFileName(), getBytes());
        } catch (IOException e) {
            logger.error("IO Error when saving file on the server. file name: " + getFileName() + ", error: " + e.getMessage(), e);
            return false;
        }
        return true;
    }
}
