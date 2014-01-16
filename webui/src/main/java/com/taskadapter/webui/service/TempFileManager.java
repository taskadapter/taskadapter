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
     * Generates a next temp file.
     * 
     * @return temp file.
     */
    public File nextFile() {
        return new File(root, Integer.toString(idx.incrementAndGet()));
    }

}
