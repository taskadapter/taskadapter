package com.taskadapter.web.configeditor.file;

import com.vaadin.ui.Upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class UploadReceiver implements Upload.Receiver {
    private final File userContentDirectory;

    public UploadReceiver(File userContentDirectory) {
        this.userContentDirectory = userContentDirectory;
    }

    public OutputStream receiveUpload(String filename, String mimeType) {
        userContentDirectory.mkdirs();
        filename = FileUtils.basename(filename);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(userContentDirectory, filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new LimitedOutputStream(fos,
                ServerModeFilePanel.MAX_FILE_SIZE_BYTES);
    }
}
