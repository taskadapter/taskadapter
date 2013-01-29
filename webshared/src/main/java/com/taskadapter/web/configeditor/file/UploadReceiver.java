package com.taskadapter.web.configeditor.file;

import com.vaadin.ui.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class UploadReceiver implements Upload.Receiver {
    private final Logger logger = LoggerFactory.getLogger(UploadReceiver.class);

    private String fileName;
    private int total;
    private byte[] bytes = new byte[ServerModeFilePanel.MAX_FILE_SIZE_BYTES];
    private final File userContentDirectory;

    public UploadReceiver(File userContentDirectory) {
        this.userContentDirectory = userContentDirectory;
    }

    public OutputStream receiveUpload(String filename, String mimeType) {
        //TODO !!! : and fix this security bug too...
        total = 0;
        fileName = filename;
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(userContentDirectory, filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new LimitedOutputStream(fos,
                ServerModeFilePanel.MAX_FILE_SIZE_BYTES);
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, total);
    }
}
