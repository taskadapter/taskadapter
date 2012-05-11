package com.taskadapter.web.configeditor.file;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Author: Alexander Kulik
 * Date: 11.05.12 20:38
 */
class FileDownloadResource extends FileResource {

    public FileDownloadResource(File sourceFile, Application application) {
        super(sourceFile, application);
    }

    public DownloadStream getStream() {
        try {
            final DownloadStream ds = new DownloadStream(
                    new FileInputStream(getSourceFile()), getMIMEType(),
                    getFilename());
            ds.setParameter("Content-Disposition", "attachment; filename=" + getFilename());
            ds.setCacheTime(getCacheTime());
            return ds;
        } catch (final FileNotFoundException e) {
            // No logging for non-existing files at this level.
            return null;
        }
    }
}
