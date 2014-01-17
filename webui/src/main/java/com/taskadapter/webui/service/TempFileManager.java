package com.taskadapter.webui.service;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Temporary file manager.
 */
public final class TempFileManager {
    /**
     * Temp root directory.
     */
    private final File root;

    /**
     * File index.
     */
    private final AtomicInteger idx = new AtomicInteger();

    /**
     * Creates a new temp file manager.
     * 
     * @param root
     *            root file.
     */
    public TempFileManager(File root) {
        this.root = root;
        root.mkdirs();
        for (File ff : root.listFiles())
            ff.delete();
    }

    /**
     * Generates a file reference for the next temp file with the given extension.
     * It is important to save the original extension because MPXJ file loader depends on it
     * to determine the file type (XML / MPP/ MPT / etc).
     *
     * <p>This method does not create the file.
     *
     * @return temp file.
     */
    public File nextFile(String fileNameExtension) {
        String newFileName = Integer.toString(idx.incrementAndGet()) + "." + fileNameExtension;
        return new File(root, newFileName);
    }

}
