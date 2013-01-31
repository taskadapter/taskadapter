package com.taskadapter.web.configeditor.file;

import java.io.File;

/**
 * File processing result specification.
 * 
 */
public class FileProcessingResult {
    /**
     * Resulting file, optional.
     */
    private final File resultFile;

    /**
     * File processing message, required.
     */
    private final String message;

    public FileProcessingResult(File resultFile, String message) {
        this.resultFile = resultFile;
        this.message = message;
    }

    public File getResultFile() {
        return resultFile;
    }

    public String getMessage() {
        return message;
    }
}
