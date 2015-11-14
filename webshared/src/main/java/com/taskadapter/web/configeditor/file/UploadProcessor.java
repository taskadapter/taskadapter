package com.taskadapter.web.configeditor.file;

import java.io.File;

/**
 * Handler of uploaded files.
 */
public interface UploadProcessor {
    /**
     * Processes an uploaded file.
     * @param uploadedFile uploaded file.
     * @return file processing result.
     */
    FileProcessingResult processFile(File uploadedFile);
}
